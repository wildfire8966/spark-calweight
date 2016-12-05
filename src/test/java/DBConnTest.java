import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by yuanye8 on 2016/12/5.
 */
public class DBConnTest {
    @Test
    public void testMysqlConn() throws Exception {
        String url = "jdbc:mysql://10.73.20.42:3306/spark_test";
        String user = "root";
        String passwd = "root";
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, passwd);
            conn.setAutoCommit(true);
            pst = conn.prepareStatement("insert into test(uid, weight) values(?, ?)");
            pst.setString(1, "2");
            pst.setString(2, "b");
            pst.executeUpdate();
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

    @Test
    public void testRedisConn() throws Exception {
        Jedis jis = new Jedis("10.73.20.42", 6479, 1000);
        jis.auth("1qaz2wsx");
        jis.set("yuanye","hello,world");
        System.out.println(jis.get("yuanye"));
    }
}
