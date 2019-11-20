package com.hcx.publisher.bean;

import java.util.List;
import java.util.Map;

/**
 * @ClassName SaleDetailInfo
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-18 15:47
 * @Version 1.0
 **/
public class SaleDetailInfo {
    Integer total;

    List<OptionGroup> stat;

    List<Map> detail;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<OptionGroup> getStat() {
        return stat;
    }

    public void setStat(List<OptionGroup> stat) {
        this.stat = stat;
    }

    public List<Map> getDetail() {
        return detail;
    }

    public void setDetail(List<Map> detail) {
        this.detail = detail;
    }

    public SaleDetailInfo(Integer total, List<OptionGroup> stat, List<Map> detail) {

        this.total = total;
        this.stat = stat;
        this.detail = detail;
    }
}
