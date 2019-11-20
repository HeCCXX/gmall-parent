package com.hcx.util

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

object MyKafkaUtil {

  private  val properties :Properties = PropertiesUtil.load("config.properties")
  val broker_list = properties.getProperty("kafka.broker.list")

  //kafka消费者配置
  val kafkaParam = Map(
    "bootstrap.servers" -> broker_list,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id"-> "gmall_consumer_group",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (true:java.lang.Boolean)
  )
  //LocationStrategies : 根据给定的主题和集群地址创建consumer
  //LovationStrategies.PreferConsistent  持续的在所有Executor之间分配分区
  //ConsumerStrategies ： 选择如何在Driver和Executor上创建和配置kafka consumer
  //consumerStrategies.Subscribe    订阅一系列主题
  def getKafkaStream(topic :String,ssc:StreamingContext):InputDStream[ConsumerRecord[String,String]]={
    val DStream = KafkaUtils.createDirectStream[String,String](ssc,LocationStrategies.PreferConsistent,ConsumerStrategies.Subscribe[String,String](Array(topic),kafkaParam))
    DStream
  }
}
