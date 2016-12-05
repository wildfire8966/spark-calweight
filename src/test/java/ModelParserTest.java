import com.weibo.yuanye.tool.LrParser;
import com.weibo.yuanye.tool.TreeParser;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by yuanye8 on 2016/12/5.
 */
public class ModelParserTest {
    TreeParser treeParser;
    LrParser lrParser;

    @Before
    public void setUp() throws Exception {
        treeParser = new TreeParser("/data0/yuanye8/download/tree_model.txt");
        lrParser = new LrParser("/data0/yuanye8/download/lr_model.txt");
    }

    @Test
    public void testTreeParser() throws Exception {
        String line = "1:0 2:8 3:1 4:15 5:10 6:3 7:12 8:0.3 9:10 10:27 11:3.4 12:0.9 13:0 14:1 15:0 16:7.8 17:0 18:1.2 19:3 20:4";
        System.out.println(treeParser.toString());
        Object[] newArray = treeParser.get_new_features_array(line);
        treeParser.outputArray(newArray);
        System.out.println("Output string format : " + treeParser.get_new_features_line(line));
        System.out.println("Output weight value : " + lrParser.getFinalWeight(newArray));
    }

    @Test
    public void testLrParser() throws Exception {
        System.out.println(lrParser.toString());
    }

    @Test
    public void testAllModel() throws Exception {
        String tree = "/data0/yuanye8/download/models/tree";
        String lr = "/data0/yuanye8/download/models/lr";
        final String treeModelPath1 = tree + "1";
        final String treeModelPath2 = tree + "2";
        final String treeModelPath3 = tree + "3";
        final String treeModelPath4 = tree + "4";
        final String lrModelPath1 = lr + "1";
        final String lrModelPath2 = lr + "2";
        final String lrModelPath3 = lr + "3";
        final String lrModelPath4 = lr + "4";
        final TreeParser treeModel1 = new TreeParser(treeModelPath1);
        final TreeParser treeModel2 = new TreeParser(treeModelPath2);
        final TreeParser treeModel3 = new TreeParser(treeModelPath3);
        final TreeParser treeModel4 = new TreeParser(treeModelPath4);
        final LrParser lrModel1 = new LrParser(lrModelPath1);
        final LrParser lrModel2 = new LrParser(lrModelPath2);
        final LrParser lrModel3 = new LrParser(lrModelPath3);
        final LrParser lrModel4 = new LrParser(lrModelPath4);
        System.out.println(lrModel4.toString());
        System.out.println(treeModel4.toString());
        String sample = "1:0.3820 2:0.0000 3:0.0000 4:0.0087 5:0 6:0 7:0 8:0 9:0 10:0.7443 11:0.8955 12:0.0000 13:0.0000 14:2863.2139 15:0 16:0 17:0 18:1.0000 19:0 20:1 21:0 22:0 23:0 24:0 25:0 26:0 27:1 28:0 29:0 30:0 31:0 32:0 33:0 34:0 35:0 36:0 37:0 38:0 39:0 40:0 41:1 42:0 43:0 44:0 45:0 46:0 47:0 48:0 49:0 50:0 51:0 52:0 53:0 54:0 55:0 56:0 57:0 58:0 59:0 60:0 61:0 62:0 63:0";
        testSample(sample, treeModel4, lrModel4);
    }

    public static void testSample(String sample, TreeParser tree, LrParser lr) {
        System.out.println(tree.get_new_features_line(sample));
        System.out.println(lr.getFinalWeight(tree.get_new_features_array(sample)));
    }
}
