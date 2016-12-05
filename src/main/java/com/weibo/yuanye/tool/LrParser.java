package com.weibo.yuanye.tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * LR模型解析与预测类
 * Created by yuanye8 on 2016/11/23.
 */
public class LrParser implements Serializable {
    private ArrayList<Double> model = new ArrayList<Double>();
    private String modelPath;
    private DecimalFormat format = new DecimalFormat("00.000");

    public LrParser(String modelPath) {
        this.modelPath = modelPath;
        readModelFile();
    }

    public void readModelFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(modelPath));
            String str = null;
            while (null != (str = br.readLine())) {
                double param = Double.parseDouble(str);
                model.add(param);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("[LoadModelError]:" + modelPath);
            System.exit(-1);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (double d : model) {
            sb.append(d).append(", ");
        }
        sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    public double getFinalWeight(Object[] values) {
        double sum = 0.0;
        Object[] params = model.toArray();
        for (int i = 0; i < params.length; i++) {
            sum += (Double) params[i] * (Double) values[i];
        }
        return Double.valueOf(format.format(sum));
    }

}
