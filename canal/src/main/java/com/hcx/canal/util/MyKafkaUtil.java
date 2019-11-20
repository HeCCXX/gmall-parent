package com.hcx.canal.util;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @ClassName MyKafkaUtil
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-15 16:38
 * @Version 1.0
 **/
public class MyKafkaUtil {
    public static KafkaProducer<String,String> kafkaProducer = null;

    public static void send(String kafkaTopicOrder, String toJSONString) {
        if (kafkaProducer == null){
            kafkaProducer = createKafkaProducer();
        }
        kafkaProducer.send(new ProducerRecord<>(kafkaTopicOrder,toJSONString));

    }

    private static KafkaProducer<String, String> createKafkaProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers","hadoop1:9092,hadoop2:9092,hadoop3:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String,String> producer = null;

        try {
            producer = new KafkaProducer<String, String>(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return producer;

    }
}
