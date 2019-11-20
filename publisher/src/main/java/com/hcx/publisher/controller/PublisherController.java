package com.hcx.publisher.controller;

import com.alibaba.fastjson.JSON;
import com.hcx.publisher.bean.Option;
import com.hcx.publisher.bean.OptionGroup;
import com.hcx.publisher.bean.SaleDetailInfo;
import com.hcx.publisher.service.PublisherService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName PublisherController
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-14 11:08
 * @Version 1.0
 **/
@RestController
public class PublisherController {

    @Autowired
    PublisherService publisherService;

    @GetMapping("realtime-total")
    public String getTotal(@RequestParam("date") String date){
        List<Map> totalList = new ArrayList<>();
        Map dauMap = new HashMap();
        dauMap.put("id","dau");
        dauMap.put("name","新增日活");

        Integer dauTotal = publisherService.getDauTotal(date);
        dauMap.put("value",dauTotal);
        totalList.add(dauMap);

        Map newMidMap=new HashMap();
        newMidMap.put("id","newMid");
        newMidMap.put("name","新增设备");

        newMidMap.put("value",233);
        totalList.add(newMidMap);



        Map orderMap = new HashMap();
        orderMap.put("id","order");
        orderMap.put("name","销售额");
        Double orderSum = publisherService.getOrderSum(date);
        orderMap.put("value",orderSum);
        totalList.add(orderMap);

        return JSON.toJSONString(totalList);
    }

    @GetMapping("realtime-hour")
    public String getHourMap(@RequestParam("id") String id, @RequestParam("date") String today){
        if ("dau".equals(id)){
            Map dauHourMap = publisherService.getDauHourMap(today);
            String yesterday = getYesterday(today);
            Map yesterdayMap = publisherService.getDauHourMap(yesterday);
            Map hourMap = new HashMap();
            hourMap.put("today",dauHourMap);
            hourMap.put("yesterday",yesterdayMap);
            return JSON.toJSONString(hourMap);
        }else if ("order".equals(id)){
            Map hourMap = new HashMap();
            String yesterday = getYesterday(today);
            Map orderHourMap = publisherService.getOrderHourMap(today);
            Map yesterdayHourMap = publisherService.getOrderHourMap(yesterday);
            hourMap.put("today",orderHourMap);
            hourMap.put("yesterday",yesterdayHourMap);
            return JSON.toJSONString(hourMap);
        }
        return id;
    }

    private String getYesterday(String today) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = "";
        try {
            Date todayDate = simpleDateFormat.parse(today);
            Date yesterdayDate = DateUtils.addDays(todayDate, -1);
            yesterday = simpleDateFormat.format(yesterdayDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return yesterday;
    }

    @GetMapping("sale_detail")
    public String getSaleDetail(@RequestParam("date") String date,@RequestParam("startpage") int pageNo,@RequestParam("size") int pageSize,
                                @RequestParam("keyword") String keyword){
        Map user_gender = publisherService.getSaleDetailMap(date, keyword, pageNo, pageSize, "user_gender", 2);
        Integer total = (Integer)user_gender.get("total");
        List<Map> detail = (List<Map>)user_gender.get("detail");
        Map aggsMap = (Map)user_gender.get("aggsMap");

        Long femaleCount = (Long)aggsMap.getOrDefault("F", 0);
        Long maleCount = (Long)aggsMap.getOrDefault("M", 0);

        Double maleRatio = Math.round(maleCount*1000D/total)/10D;
        Double femaleRatio = Math.round(femaleCount*1000D/total)/10D;

        List<Option> optionList = new ArrayList<>();
        optionList.add(new Option("男",maleRatio));
        optionList.add(new Option("女",femaleRatio));

        OptionGroup optionGroup = new OptionGroup("性别占比", optionList);


        Map user_age = publisherService.getSaleDetailMap(date, keyword, pageNo, pageSize, "user_age", 100);
        Map aggsMapAge = (Map)user_age.get("aggsMap");

        Long age_20count = 0L;
        Long age20_30count = 0L;
        Long age30_count = 0L;

        for (Object o : aggsMapAge.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = (String)entry.getKey();
            Long value = (Long) entry.getValue();
            if (Integer.parseInt(key)<20){
                age_20count += value;
            }else if (Integer.parseInt(key)>=20 && Integer.parseInt(key)<=30){
                age20_30count += value;
            }else {
                age30_count += value;
            }
        }
        //占比
        double age_20Ratio = Math.round(age_20count * 1000D / total) / 10D;
        double age20_30Ratio = Math.round(age20_30count * 1000D / total) / 10D;
        double age30_Ratio = Math.round(age30_count * 1000D / total) / 10D;

        List<Option> optionListAge = new ArrayList<>();
        optionListAge.add(new Option("20岁以下",age_20Ratio));
        optionListAge.add(new Option("20岁-30岁",age20_30Ratio));
        optionListAge.add(new Option("30岁以上",age30_Ratio));
        OptionGroup optionGroupAge = new OptionGroup("年龄占比", optionListAge);

        List<OptionGroup> optionGroups = new ArrayList<>();
        optionGroups.add(optionGroup);
        optionGroups.add(optionGroupAge);

        SaleDetailInfo saleDetailInfo = new SaleDetailInfo(total, optionGroups, detail);
        return JSON.toJSONString(saleDetailInfo);
    }

}
