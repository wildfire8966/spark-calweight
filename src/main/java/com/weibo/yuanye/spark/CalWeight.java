package com.weibo.yuanye.spark;

import com.weibo.yuanye.tool.LrParser;
import com.weibo.yuanye.tool.TreeParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

/**
 * Created by yuanye8 on 2016/11/23.
 */
public class CalWeight {
    public static void main(final String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: <tree-model> <lr-model> <input-path> <output-path>");
            System.exit(-1);
        }

        /**
         * 1,2,3,4分别对应低频、中频、高频、访客
         */
        final String treeModelPath1 = args[0] + "1";
        final String treeModelPath2 = args[0] + "2";
        final String treeModelPath3 = args[0] + "3";
        final String treeModelPath4 = args[0] + "4";
        final String lrModelPath1 = args[1] + "1";
        final String lrModelPath2 = args[1] + "2";
        final String lrModelPath3 = args[1] + "3";
        final String lrModelPath4 = args[1] + "4";

        String inputPath = args[2];
        String outputpath = args[3];

        SparkConf sc = new SparkConf();
        JavaSparkContext jsc = new JavaSparkContext(sc);

        jsc.addFile(treeModelPath1);
        jsc.addFile(treeModelPath2);
        jsc.addFile(treeModelPath3);
        jsc.addFile(treeModelPath4);
        jsc.addFile(lrModelPath1);
        jsc.addFile(lrModelPath2);
        jsc.addFile(lrModelPath3);
        jsc.addFile(lrModelPath4);

        final TreeParser treeModel1 = new TreeParser(treeModelPath1);
        final TreeParser treeModel2 = new TreeParser(treeModelPath2);
        final TreeParser treeModel3 = new TreeParser(treeModelPath3);
        final TreeParser treeModel4 = new TreeParser(treeModelPath4);
        final LrParser lrModel1 = new LrParser(lrModelPath1);
        final LrParser lrModel2 = new LrParser(lrModelPath2);
        final LrParser lrModel3 = new LrParser(lrModelPath3);
        final LrParser lrModel4 = new LrParser(lrModelPath4);

        JavaRDD<String> input = jsc.textFile(inputPath);
        input.map(new Function<String, String>() {
            public String call(String s) throws Exception {
                StringBuilder sb = new StringBuilder();
                String[] splits = s.trim().split("\t");
                String uid = splits[0];
                String frequencyTag = splits[1];
                String mid = splits[2];
                String source = splits[3];
                String svmStr = splits[4];

                Object[] newFeatureArray;
                double weight = 0.0;
                if (frequencyTag.equals("1") || frequencyTag.equals("2") ||
                        frequencyTag.equals("0") || frequencyTag.equals("9")) {
                    //低频、中低频、30天未活跃、30天新用户
                    newFeatureArray = treeModel1.get_new_features_array(svmStr);
                    weight = lrModel1.getFinalWeight(newFeatureArray);
                } else if (frequencyTag.equals("3")) {
                    //中高频
                    newFeatureArray = treeModel2.get_new_features_array(svmStr);
                    weight = lrModel2.getFinalWeight(newFeatureArray);
                } else if (frequencyTag.equals("4") || frequencyTag.equals("5")) {
                    //高频、全勤
                    newFeatureArray = treeModel3.get_new_features_array(svmStr);
                    weight = lrModel3.getFinalWeight(newFeatureArray);
                } else if (frequencyTag.equals("99")) {
                    //访客
                    newFeatureArray = treeModel4.get_new_features_array(svmStr);
                    weight = lrModel4.getFinalWeight(newFeatureArray);
                }
                sb.append(uid).append("\t").append(frequencyTag).append("\t")
                        .append(mid).append("\t").append(source).append("\t").append(weight);
                return sb.toString();
            }
        }).saveAsTextFile(outputpath);
    }
}
