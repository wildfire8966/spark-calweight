package com.weibo.yuanye.spark.stream;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * Created by yuanye8 on 2016/12/15.
 */
public class SparkStreamTest {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        JavaStreamingContext jsc = new JavaStreamingContext(conf, Durations.seconds(10));
        JavaDStream<String> lines = jsc.socketTextStream("localhost", 7777);
        JavaDStream<String> yeslines = lines.filter(new Function<String, Boolean>() {
            public Boolean call(String s) throws Exception {
                return s.contains("yes");
            }
        });
        yeslines.print();

        jsc.start();
        jsc.awaitTermination();
    }
}
