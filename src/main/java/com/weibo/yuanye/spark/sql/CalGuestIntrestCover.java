package com.weibo.yuanye.spark.sql;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;

/**
 * Created by yuanye8 on 2016/12/7.
 */
public class CalGuestIntrestCover {
    public static void main(String[] args) {
        SparkConf sc = new SparkConf();
        JavaSparkContext jsc = new JavaSparkContext(sc);
        HiveContext hct = new HiveContext(jsc.sc());

        DataFrame frame = hct.sql("select guest_type, count(*) from " +
                "  (" +
                "    select a.uid, b.guest_type " +
                "    from " +
                "    weibo_bigdata_dm_guest_predict_guest_gender_by_app_interest a join bigdata_visitor_login b " +
                "    on a.uid=b.guest_id where a.dt=20161207 and b.dt=20161207" +
                "  ) c group by guest_type;");
        Row firstRow = frame.first();
        System.out.println(firstRow.getInt(0) + "\t" + firstRow.getInt(1));
    }
}
