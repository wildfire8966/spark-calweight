package com.weibo.yuanye.exception;

import java.io.Serializable;

/**
 * Created by yuanye8 on 2016/11/23.
 */
public class TreeParseException extends Exception implements Serializable {
    public TreeParseException() {
    }

    public TreeParseException(String msg) {
        super(msg);
    }
}
