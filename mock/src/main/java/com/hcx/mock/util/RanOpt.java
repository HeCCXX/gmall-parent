package com.hcx.mock.util;

/**
 * @ClassName RanOpt
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-06 19:23
 * @Version 1.0
 **/
public class RanOpt<T> {
    T value ;
    int weight;

    public RanOpt ( T value, int weight ){
        this.value=value ;
        this.weight=weight;
    }

    public T getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }
}
