package com.weibo.yuanye.tool;

import com.weibo.yuanye.exception.TreeParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * java版本xgboost模型解析类
 * Created by yuanye8 on 2016/11/23.
 */
public class TreeParser implements Serializable {
    private String modelPath;
    private ArrayList<Map<String, TreeNode>> trees = new ArrayList();

    public TreeParser(String modelPath) {
        this.modelPath = modelPath;
        this.parseTree();
    }

    /**
     * 解析文件为树结构,每颗数为一个列表,节点为元组表示: (树节点index, 特征index, 值, 左孩子, 右孩子)
     */
    private void parseTree() {
        boolean isFirst = true;
        String str = null;
        HashMap<String, TreeNode> tree = new HashMap<String, TreeNode>();
        try {
            FileReader reader = new FileReader(modelPath);
            BufferedReader br = new BufferedReader(reader);
            while (null != (str = br.readLine())) {
                str = str.trim();
                if (str.startsWith("booster")) {
                    if (isFirst) {
                        isFirst = false;
                        continue;
                    }
                    trees.add(tree);
                    tree = new HashMap<String, TreeNode>();
                    continue;
                }
                String[] splits = str.split(":");
                String index = splits[0];
                String content = splits[1];
                if (content.contains("leaf")) {
                    String leafVal = content.trim().split("=")[1];
                    tree.put(index, new TreeNode(index, "-1", leafVal));
                } else {
                    String featureIndex = null;
                    String nodeVal = null;
                    int lessIndex = content.indexOf("<");
                    int endIndex = content.indexOf("]");
                    String[] leftRightMiss = content.split(" ")[1].split(",");
                    String leftChild = leftRightMiss[0].split("=")[1];
                    String rightChild = leftRightMiss[1].split("=")[1];
                    if (endIndex != -1 && lessIndex != -1) {
                        featureIndex = content.substring(2, lessIndex);
                        nodeVal = content.substring(lessIndex + 1, endIndex);
                    } else {
                        throw new TreeParseException();
                    }
                    tree.put(index, new TreeNode(index, featureIndex, nodeVal, leftChild, rightChild));
                }
            }
            trees.add(tree);
            br.close();
        } catch (FileNotFoundException e1) {
            System.out.println("[FileNotFoundError]:" + modelPath);
            System.exit(-1);
        } catch (IOException e2) {
            System.out.println("[IOError]:" + str);
            System.exit(-1);
        } catch (TreeParseException e3) {
            System.out.println("[LoadModelError]:" + modelPath);
            System.exit(-1);
        }
    }

    /**
     * 解析输入原始特征载入内存,libsvm格式
     *
     * @param line
     * @return
     */
    private ArrayList<Tuple2> trans_libsvm_with_label(String line) {
        ArrayList<Tuple2> result = new ArrayList();
        String[] items = line.trim().split(" ");
        result.add(new Tuple2("-1", items[0]));
        for (int i = 1; i < items.length; i++) {
            String[] splits = items[i].split(":");
            result.add(new Tuple2(splits[0], splits[1]));
        }
        return result;
    }

    /**
     * 解析输入原始特征载入内存,libsvm格式,没有label标签
     *
     * @param line
     * @return
     */
    private ArrayList<Tuple2> trans_libsvm(String line) {
        ArrayList<Tuple2> result = new ArrayList();
        String[] items = line.trim().split(" ");
        for (int i = 0; i < items.length; i++) {
            String[] splits = items[i].split(":");
            result.add(new Tuple2(splits[0], splits[1]));
        }
        return result;
    }

    /**
     * 将叶子节点转化为向量，加在原向量后
     *
     * @param node
     * @param tree
     */
    public void transLeafsToVector(ArrayList<Tuple2> oldFeatures, TreeNode node, Map<String, TreeNode> tree, String theChosenOne) {
        int beginIndex = oldFeatures.size() + 1;
        if (Math.abs(Double.valueOf(node.getFeatureValue())) > 0.0000000000000001) {
            ArrayList<TreeNode> leafs = new ArrayList();
            for (Map.Entry t : tree.entrySet()) {
                TreeNode cur = (TreeNode) t.getValue();
                if (cur.getFeatureIndex().equals("-1")) {
                    leafs.add(cur);
                }
            }
            Collections.sort(leafs);
            for (TreeNode leaf : leafs) {
                if (leaf.getIndex().equals(theChosenOne)) {
                    oldFeatures.add(new Tuple2(String.valueOf(beginIndex++), "1"));
                } else {
                    oldFeatures.add(new Tuple2(String.valueOf(beginIndex++), "0"));
                }
            }
        }
    }

    /**
     * 获取新特征向量,将新特征添加在原始特征之后,以libsvm格式进行输出
     *
     * @param line
     * @return
     */
    public String get_new_features_line(String line) {
        StringBuilder sb = new StringBuilder();
        ArrayList<Tuple2> oldFeatures = trans_libsvm(line);
        if (oldFeatures.size() == 0) {
            return sb.toString();
        }
        for (Map<String, TreeNode> tree : trees) {
            String n = "0";
            TreeNode node = tree.get(n);
            int feature = 0;
            double value = 0.0;
            while (!node.getFeatureIndex().equals("-1")) {
                feature = Integer.valueOf(node.getFeatureIndex());
                value = Double.valueOf(node.getFeatureValue());
                if (Double.valueOf(oldFeatures.get(feature - 1).getValue()) <= value) {
                    n = node.getLeftChild();
                } else {
                    n = node.getRightChild();
                }
                node = tree.get(n);
            }
            transLeafsToVector(oldFeatures, node, tree, n);
        }
        for (int i = 0; i < oldFeatures.size(); i++) {
            Tuple2 t = oldFeatures.get(i);
            sb.append(t.getIndex()).append(":").append(t.getValue()).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * 获取新特征向量,将新特征添加在原始特征之后,以double数组进行输出
     *
     * @param line
     * @return
     */
    public Object[] get_new_features_array(String line) {
        ArrayList<Double> result = new ArrayList<Double>();
        ArrayList<Tuple2> oldFeatures = trans_libsvm(line);
        if (oldFeatures.size() == 0) {
            return null;
        }
        //libsvm 格式特征从1开始
        for (Map<String, TreeNode> tree : trees) {
            String n = "0";
            TreeNode node = tree.get(n);
            int feature = 0;
            double value = 0.0;
            while (!node.getFeatureIndex().equals("-1")) {
                feature = Integer.valueOf(node.getFeatureIndex());
                value = Double.valueOf(node.getFeatureValue());
                //旧的属性列表下标从0开始
                Tuple2 current = oldFeatures.get(feature - 1);
                if (Double.valueOf(current.getValue()) <= value) {
                    n = node.getLeftChild();
                } else {
                    n = node.getRightChild();
                }
                node = tree.get(n);
            }
            transLeafsToVector(oldFeatures, node, tree, n);
        }
        for (int i = 0; i < oldFeatures.size(); i++) {
            result.add(Double.valueOf(oldFeatures.get(i).getValue()));
        }
        return result.toArray();
    }

    @Override
    public String toString() {
        System.out.println("tree size: " + trees.size());
        StringBuilder sb = new StringBuilder();
        sb.append("[").append("\n");
        for (Map<String, TreeNode> tree : trees) {
            sb.append("\t[");
            for (Map.Entry t : tree.entrySet()) {
                TreeNode cur = (TreeNode) t.getValue();
                sb.append("(").append(cur.getIndex()).append(",").append(cur.getFeatureIndex())
                        .append(",").append(cur.getFeatureValue()).append(",")
                        .append(cur.getLeftChild()).append(",").append(cur.getRightChild()).append(")").append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]").append(",").append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    public void outputArray(Object[] array) {
        System.out.println("Output size : " + array.length);
        System.out.println("Output array format : ");
        for (Object o : array) {
            System.out.print((Double) o);
            System.out.print(", ");
        }
        System.out.println();
    }

}
