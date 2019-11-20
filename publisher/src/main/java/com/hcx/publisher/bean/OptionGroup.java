package com.hcx.publisher.bean;

import java.util.List;

/**
 * @ClassName OptionGroup
 * @Description TODO
 * @Author 贺楚翔
 * @Date 2019-11-18 15:47
 * @Version 1.0
 **/
public class OptionGroup {
    String title;

    List<Option> options;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public OptionGroup(String title, List<Option> options) {
        this.title = title;
        this.options = options;
    }
}
