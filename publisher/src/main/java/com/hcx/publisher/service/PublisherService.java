package com.hcx.publisher.service;

import java.util.Map;

/**
 * @ClassName PublisherService
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-14 11:15
 * @Version 1.0
 **/
public interface PublisherService {
    public Integer getDauTotal(String date);

    public Map getDauHourMap(String date);

    public Double getOrderSum(String date);

    public Map getOrderHourMap(String date);


    public Map getSaleDetailMap(String date,String keyword,int pageNo,int pageSize,String aggsFieldName,int aggsSize);
}
