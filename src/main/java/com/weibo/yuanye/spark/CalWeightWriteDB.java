package com.weibo.yuanye.spark;

import com.weibo.yuanye.tool.LrParser;
import com.weibo.yuanye.tool.TreeParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * 将spark计算结果写入Mysql数据库中或Redis数据库中
 * 主要利用了foreachPartition函数，使得每个执行节点建立数据库连写批量写入数据
 * Created by yuanye8 on 2016/11/23.
 */
public class CalWeightWriteDB {
    public static void main(final String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: <tree-model> <lr-model> <input-path> <output-path>");
            System.exit(-1);
        }

        final String treeModelPath = args[0];
        final String lrModelPath = args[1];

        String inputPath = args[2];
        String outputpath = args[3];

        SparkConf sc = new SparkConf();
        JavaSparkContext jsc = new JavaSparkContext(sc);

        jsc.addFile(treeModelPath);
        jsc.addFile(lrModelPath);

        final TreeParser treeModel = new TreeParser(treeModelPath);
        final LrParser lrModel = new LrParser(lrModelPath);

        JavaRDD<String> input = jsc.textFile(inputPath);
        input.map(new Function<String, String>() {
            public String call(String s) throws Exception {
                StringBuilder sb = new StringBuilder();
                String[] splits = s.split("\t");
                String uid = splits[0];
                String svmStr = splits[1];
                Object[] newFeatureArray = treeModel.get_new_features_array(svmStr);
                double weight = lrModel.getFinalWeight(newFeatureArray);
                sb.append(uid).append("\t").append(weight).toString();
                return sb.toString();
            }
        }).foreachPartition(new DbOperateFunction("10.73.20.42", "6479", "1qaz2wsx", "redis"));
    }
}

class DbOperateFunction implements VoidFunction<Iterator<String>> {
    private String url;
    private String host;
    private String user;
    private String password;
    private String port;
    private String type;

    public DbOperateFunction(String arg1, String arg2, String arg3, String type) {
        if (type.equals("redis")) {
            this.host = arg1;
            this.port = arg2;
            this.password = arg3;
            this.type = type;
        } else {
            this.url = arg1;
            this.user = arg2;
            this.password = arg3;
        }
    }

    public void call(Iterator<String> iter) throws Exception {
        if (!host.equals("")) {
            Jedis conn = new Jedis(host, Integer.parseInt(port));
            Pipeline p =  conn.pipelined();
            conn.auth(password);
            //每次批量写入10万条
            int COUNT = 100000;
            int count = 0;
            while (iter.hasNext()) {
                String in = iter.next();
                String[] splits = in.split("\t");
                p.set(splits[0], splits[1]);
                if (count == COUNT) {
                    p.sync();
                }
            }
            p.sync();
        } else {
            Connection conn = null;
            PreparedStatement pst = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, password);
                conn.setAutoCommit(false);
                pst = conn.prepareStatement("insert into test(uid, weight) values(?, ?)");
                while (iter.hasNext()) {
                    String str = iter.next();
                    String[] splits = str.split("\t");
                    pst.setString(1, splits[0]);
                    pst.setString(2, splits[1]);
                    pst.addBatch();
                }
                pst.executeBatch();
                conn.commit();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }
}
