package com.hcx.app

import java.util.Properties

import com.alibaba.fastjson.JSON
import com.hcx.{GmallConstant, MyEsUtil}
import com.hcx.bean.OrderInfo
import com.hcx.util.MyKafkaUtil
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object OrderApp {

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("order_app").setMaster("local[*]")
    val ssc = new StreamingContext(conf,Seconds(5))

    //读取数据
    val inputStream: InputDStream[ConsumerRecord[String, String]] = MyKafkaUtil.getKafkaStream(GmallConstant.KAFKA_TOPIC_ORDER,ssc)

    //将数据进行处理
    val orderInfoDstream: DStream[OrderInfo] = inputStream.map {
      record =>
        val jsonstr: String = record.value()
        val orderInfo: OrderInfo = JSON.parseObject(jsonstr, classOf[OrderInfo])

        val telSplit: (String, String) = orderInfo.consigneeTel.splitAt(4)
        orderInfo.consigneeTel = telSplit._1 + "*******"
        val TimeArr: Array[String] = orderInfo.createTime.split(" ")
        orderInfo.createDate = TimeArr(0)
        val time: Array[String] = TimeArr(1).split(":")
        orderInfo.createHour = time(0)
        orderInfo.createHourMinute = time(0) + ":" + time(1)
        orderInfo
    }
    //将数据保存到es
    orderInfoDstream.foreachRDD{
      rdd =>
        rdd.foreachPartition(orderItr =>
        MyEsUtil.indexBulk(GmallConstant.ES_INDEX_ORDER,orderItr.toList))
    }


    ssc.start()
    ssc.awaitTermination()
  }

}
