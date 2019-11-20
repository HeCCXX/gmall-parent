package com.hcx.export.app

import com.hcx.{GmallConstant, MyEsUtil}
import com.hcx.export.bean.SaleDetailDaycount
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.collection.mutable.ListBuffer

/**
 * @Author HCX
 * @Description //TODO 将hive数据导入到es，便于搜索查询
 * @Date 15:39 2019-11-18
 *
 * @return
 * @exception
 **/

object ExportSaleApp {

  def main(args: Array[String]): Unit = {
    var date = ""
    if (args!=null && args.size >0){
      date = args(0)
    }else{
      date = "2019-11-18"
    }

    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("exportSale")

    val sparkSession: SparkSession = SparkSession.builder().enableHiveSupport().getOrCreate()
    import sparkSession.implicits._
    val saleDetailRDD: RDD[SaleDetailDaycount] = sparkSession.sql("select user_id,sku_id,user_gender,cast(user_age as int) user_age,user_level,cast(sku_price as double),sku_name,sku_tm_id, sku_category3_id,sku_category2_id,sku_category1_id,sku_category3_name,sku_category2_name,sku_category1_name,spu_id,sku_num,cast(order_count as bigint) order_count,cast(order_amount as double) order_amount,dt"
      + " from dws_sale_detail_daycount where dt='" + date + "'").as[SaleDetailDaycount].rdd
    saleDetailRDD.foreachPartition{
      saleItr =>
        val listBuffer: ListBuffer[SaleDetailDaycount] = ListBuffer()
        for (saleDetail <- saleItr){
          listBuffer += saleDetail
          if (listBuffer.size >=100){
            MyEsUtil.indexBulk(GmallConstant.ES_INDEX_SALE,listBuffer.toList)
            listBuffer.clear()
          }
        }
        if(listBuffer.size > 0){
          MyEsUtil.indexBulk(GmallConstant.ES_INDEX_SALE,listBuffer.toList)
        }
    }


  }

}
