package com.weibo.yuanye.tool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by yuanye8 on 2016/11/23.
 */
public class TreeNode implements Comparable<TreeNode>, Serializable {
    /**
     * 分别为：树位置节点，节点特征标号，节点特征值，节点左孩子，节点右孩子。
     * 若节点为叶子节点，左右孩子均为字符串"None"
     */
    private String index;
    private String featureIndex;
    private String featureValue;
    private String leftChild;
    private String rightChild;

    /**
     * 非叶节点构造函数
     *
     * @param index
     * @param featureIndex
     * @param featureValue
     * @param leftChild
     * @param rightChild
     */
    public TreeNode(String index, String featureIndex, String featureValue, String leftChild, String rightChild) {
        this.index = index;
        this.featureIndex = featureIndex;
        this.featureValue = featureValue;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    /**
     * 叶子节点构造函数
     *
     * @param index
     * @param featureIndex
     * @param featureValue
     */
    public TreeNode(String index, String featureIndex, String featureValue) {
        this.index = index;
        this.featureIndex = featureIndex;
        this.featureValue = featureValue;
        this.leftChild = "None";
        this.rightChild = "None";
    }

    /**
     * 节点排序标准：树节点位置标号
     *
     * @param t
     * @return
     */
    public int compareTo(TreeNode t) {
        int result = 0;
        return Integer.parseInt(this.index) - Integer.parseInt(t.index);
    }

    public void setLeftChild(String leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(String rightChild) {
        this.rightChild = rightChild;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setFeatureIndex(String featureIndex) {
        this.featureIndex = featureIndex;
    }

    public void setFeatureValue(String featureValue) {
        this.featureValue = featureValue;
    }

    public String getIndex() {
        return index;
    }

    public String getFeatureIndex() {
        return featureIndex;
    }

    public String getFeatureValue() {
        return featureValue;
    }

    public String getLeftChild() {

        return leftChild;
    }

    public String getRightChild() {
        return rightChild;
    }

    public static void main(String[] args) {
        ArrayList<TreeNode> list = new ArrayList();
        TreeNode t1 = new TreeNode("1", "5", "3.8");
        TreeNode t2 = new TreeNode("0", "4", "0.1");
        TreeNode t3 = new TreeNode("9", "18", "2.5");
        list.add(t1);
        list.add(t2);
        list.add(t3);
        Collections.sort(list);
        for (TreeNode t : list) {
            System.out.println(t.getIndex());
        }
    }
}
