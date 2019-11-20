package com.hcx.publisher.service.impl;

import com.hcx.GmallConstant;
import com.hcx.publisher.service.PublisherService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PublisherServiceImpl
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-14 11:15
 * @Version 1.0
 **/
@Service
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    JestClient jestClient;

    @Override
    public Integer getDauTotal(String date) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(new BoolQueryBuilder().filter(new TermQueryBuilder("logDate",date)));

        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(GmallConstant.ES_INDEX_DAU).addType("_doc").build();
        Integer total = 0;
        try {
            SearchResult result = jestClient.execute(search);
            total = result.getTotal();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    @Override
    public Map getDauHourMap(String date) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(new BoolQueryBuilder().filter(new TermQueryBuilder("logDate", date)));

        TermsBuilder agg = AggregationBuilders.terms("groupbylogHour").field("logHour").size(24);
        searchSourceBuilder.aggregation(agg);
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(GmallConstant.ES_INDEX_DAU).addType("_doc").build();
        Map dauHourMap = new HashMap();
        try {
            SearchResult result = jestClient.execute(search);
            List<TermsAggregation.Entry> buckets = result.getAggregations().getTermsAggregation("groupbylogHour").getBuckets();
            for (TermsAggregation.Entry bucket:
                 buckets) {
                String key = bucket.getKey();
                Long count = bucket.getCount();
                dauHourMap.put(key,count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dauHourMap;
    }

    @Override
    public Double getOrderSum(String date) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(new BoolQueryBuilder().filter(new TermQueryBuilder("createDate", date)));
        SumBuilder sumBuilder = AggregationBuilders.sum("total_sum").field("totalAmount");
        searchSourceBuilder.aggregation(sumBuilder);
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(GmallConstant.ES_INDEX_ORDER).addType("_doc").build();
        Double total_sum = 0D;
        try {
            SearchResult searchResult = jestClient.execute(search);
            total_sum = searchResult.getAggregations().getSumAggregation("total_sum").getSum();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total_sum;
    }

    @Override
    public Map getOrderHourMap(String date) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(new BoolQueryBuilder().filter(new TermQueryBuilder("createDate", date)));
        SumBuilder sumBuilder = AggregationBuilders.sum("total_sum").field("totalAmount");
        TermsBuilder termsBuilder = AggregationBuilders.terms("groupbyHour").field("createHour").size(24);

        termsBuilder.subAggregation(sumBuilder);
        searchSourceBuilder.aggregation(termsBuilder);

        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(GmallConstant.ES_INDEX_ORDER).addType("_doc").build();
        Map<String,Double> hourMap = new HashMap<>();
        try {
            SearchResult searchResult = jestClient.execute(search);
            List<TermsAggregation.Entry> buckets = searchResult.getAggregations().getTermsAggregation("groupbyHour").getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                String key = bucket.getKey();
                Double total_sum = bucket.getSumAggregation("total_sum").getSum();
                hourMap.put(key,total_sum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hourMap;
    }



    @Override
    public Map getSaleDetailMap(String date, String keyword, int pageNo, int pageSize, String aggsFieldName, int aggsSize) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //过滤
        boolQueryBuilder.filter(new TermQueryBuilder("dt",date));
        //全文匹配
        boolQueryBuilder.must(new MatchQueryBuilder("sku_name",keyword).operator(MatchQueryBuilder.Operator.AND));
        searchSourceBuilder.query(boolQueryBuilder);

        //聚合
        TermsBuilder termsBuilder = AggregationBuilders.terms("groupby_" + aggsFieldName).field(aggsFieldName).size(aggsSize);
        searchSourceBuilder.aggregation(termsBuilder);

        //分页
        searchSourceBuilder.from((pageNo-1)*pageSize);
        searchSourceBuilder.size(pageSize);
        System.out.println(searchSourceBuilder.toString());

        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(GmallConstant.ES_INDEX_SALE).addType("_doc").build();

        Integer total = 0;
        List<Map> detailList = new ArrayList<>();
        Map<String,Long> aggsMap = new HashMap<>();

        Map saleMap = new HashMap();
        try {
            SearchResult result = jestClient.execute(search);
             total = result.getTotal();

            List<SearchResult.Hit<Map, Void>> hits = result.getHits(Map.class);
            for (SearchResult.Hit<Map, Void> hit : hits) {
                detailList.add(hit.source);
            }
            //取聚合结果
            List<TermsAggregation.Entry> buckets = result.getAggregations().getTermsAggregation("groupby_" + aggsFieldName).getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                aggsMap.put(bucket.getKey(),bucket.getCount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        saleMap.put("total",total);
        saleMap.put("detail",detailList);
        saleMap.put("aggsMap",aggsMap);
        return saleMap;
    }
}
