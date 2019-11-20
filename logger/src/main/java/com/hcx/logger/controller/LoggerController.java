package com.hcx.logger.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hcx.GmallConstant;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName LoggerController
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-07 9:11
 * @Version 1.0
 **/

@RestController //Controller + Responsebody
public class LoggerController {

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggerController.class);

    @PostMapping("/log")
    public String dolog(@RequestParam("log") String logJson){
        // 补时间戳
        JSONObject jsonObject = JSON.parseObject(logJson);
        jsonObject.put("ts",System.currentTimeMillis());

        // 落盘到logfile   log4j
        logger.info(jsonObject.toJSONString());

        //发送kafka
        if ("startup".equals(jsonObject.getString("type"))){
            kafkaTemplate.send(GmallConstant.KAFKA_TOPIC_STARTUP,jsonObject.toJSONString());
        }else{
            kafkaTemplate.send(GmallConstant.KAFKA_TOPIC_EVENT,jsonObject.toJSONString());
        }
        return "success";
    }
}
