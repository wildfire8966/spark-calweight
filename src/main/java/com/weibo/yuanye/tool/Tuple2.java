package com.weibo.yuanye.tool;

import java.io.Serializable;

/**
 * 自定义元组类，用于libsvm格式解析
 * Created by yuanye8 on 2016/11/23.
 */
public class Tuple2 implements Serializable {
    private String index;
    private String value;

    public Tuple2(String index, String value) {
        this.index = index;
        this.value = value;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIndex() {

        return index;
    }

    public String getValue() {
        return value;
    }
}
