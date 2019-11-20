package com.hcx.app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.alibaba.fastjson.JSON
import com.hcx.{GmallConstant, MyEsUtil}
import com.hcx.bean.Startuplog
import com.hcx.util.{MyKafkaUtil, RedisUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

object DauApp {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("dau_app")
    val ssc = new StreamingContext(sparkConf,Seconds(5))

    val inputDstream: InputDStream[ConsumerRecord[String, String]] = MyKafkaUtil.getKafkaStream(GmallConstant.KAFKA_TOPIC_STARTUP,ssc)

    //转换操作
    val startuplogStream: DStream[Startuplog] = inputDstream.map {
      record =>
        val jsonStr: String = record.value()
        val startuplog: Startuplog = JSON.parseObject(jsonStr, classOf[Startuplog])
        val date = new Date(startuplog.ts)
        val DateStr: String = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date)
        val splits: Array[String] = DateStr.split(" ")
        startuplog.logDate = splits(0)
        startuplog.logHour = splits(1).split(":")(0)
        startuplog.logHourMinute = splits(1)
        startuplog
    }
    //利用redis进行去重过滤
    val filteredDstream: DStream[Startuplog] = startuplogStream.transform {
      rdd =>
        //driver  周期性执行
        val curdate: String = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
        val jedis: Jedis = RedisUtil.getJedisClient
        val key = "dau:" + curdate
        val dauSet: util.Set[String] = jedis.smembers(key)
        val dauBC: Broadcast[util.Set[String]] = ssc.sparkContext.broadcast(dauSet)
        val filteredRDD: RDD[Startuplog] = rdd.filter {
          startuplog =>
            //executor
            val dauSet: util.Set[String] = dauBC.value
            !dauSet.contains(startuplog.mid)
        }
        filteredRDD
    }
    val groupbyMidDstram: DStream[(String, Iterable[Startuplog])] = filteredDstream.map {
      startiplog => (startiplog.mid, startiplog)
    }.groupByKey()
    //去重思路，把相同mid的数据分成一组，每组去一个
    val distinctDstream: DStream[Startuplog] = groupbyMidDstram.flatMap {
      case (mid, startuplogItr) =>
        startuplogItr.take(1)
    }
    //保存到redis中
    distinctDstream.foreachRDD{rdd=>
      //driver
      //redis   type  set
      //key  dau:2019-06-03  value:mids
      rdd.foreachPartition{startuplogItr =>
        //executor
        val jedis: Jedis = RedisUtil.getJedisClient
        val list: List[Startuplog] = startuplogItr.toList
        for (startuplog<- list){
          val key = "dau:" + startuplog.logDate
          val value = startuplog.mid
          jedis.sadd(key,value)
          println(startuplog)
        }
        MyEsUtil.indexBulk(GmallConstant.ES_INDEX_DAU,list)
        jedis.close()
      }
    }
    ssc.start()
    ssc.awaitTermination()
  }
}
