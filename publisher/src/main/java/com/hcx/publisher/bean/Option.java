package com.hcx.publisher.bean;

/**
 * @ClassName Option
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-18 15:46
 * @Version 1.0
 **/
public class Option {
    String name;

    Double value;

    public Option(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
