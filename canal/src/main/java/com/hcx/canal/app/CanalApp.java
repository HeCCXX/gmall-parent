package com.hcx.canal.app;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hcx.canal.handler.CanalHandler;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @ClassName CanalApp
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-15 16:36
 * @Version 1.0
 **/
public class CanalApp {
    public static void main(String[] args) {
        CanalConnector canalConnector = CanalConnectors.newSingleConnector(new InetSocketAddress("hadoop1", 11111), "example", "", "");
        while (true){
            //连接、订阅表、获取数据
            canalConnector.connect();
            canalConnector.subscribe("gmall.order_info");
            Message message = canalConnector.get(100);
            int size = message.getEntries().size();
            if (size == 0){
                try {
                    System.out.println("no Data...");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else {
                for (CanalEntry.Entry entry : message.getEntries()) {

                    //判断时间类型，只处理行变化业务
                    if (entry.getEntryType().equals(CanalEntry.EntryType.ROWDATA)){
                        //将数据集进行反序列化
                        ByteString storeValue = entry.getStoreValue();
                        CanalEntry.RowChange rowChange = null;
                        try {
                             rowChange = CanalEntry.RowChange.parseFrom(storeValue);

                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                        // 获取行集
                        List<CanalEntry.RowData> rowDatasList = rowChange.getRowDatasList();
                        //操作类
                        CanalEntry.EventType eventType = rowChange.getEventType();
                        //表名
                        String tableName = entry.getHeader().getTableName();
                        CanalHandler.handle(tableName,eventType,rowDatasList);
                    }
                }
            }
        }
    }
}
