package com.hcx.canal.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.common.base.CaseFormat;
import com.hcx.GmallConstant;
import com.hcx.canal.util.MyKafkaUtil;

import java.util.List;

/**
 * @ClassName CanalHandler
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-15 16:37
 * @Version 1.0
 **/
public class CanalHandler {
    public static void handle(String tableName, CanalEntry.EventType eventType, List<CanalEntry.RowData> rowDatasList) {
        if ("order_info".equals(tableName)&&CanalEntry.EventType.INSERT.equals(eventType)){
            //下单操作
            for (CanalEntry.RowData rowData : rowDatasList) {
                //取数据变化后的每列
                List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
                JSONObject jsonObject = new JSONObject();

                for (CanalEntry.Column column : afterColumnsList) {
                    System.out.println(column.getName()+": " + column.getValue());
                    String propertyName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, column.getName());
                    jsonObject.put(propertyName,column.getValue());

                }
                MyKafkaUtil.send(GmallConstant.KAFKA_TOPIC_ORDER,jsonObject.toJSONString());
            }
        }
    }
}
