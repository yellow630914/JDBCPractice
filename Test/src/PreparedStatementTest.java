import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class PreparedStatementTest {

    @Test
    public void test(){
        String sql = "insert into customers(name,email,birth)values(?,?,?)";
        update(sql,"Tom","gggg@gggg","1999-05-05");
    }

    /**
     * 通用的SQL增刪改操作
     * String sql是含有佔位符的sql語句
     * Object args是要填入佔位符的陣列
     * @param sql
     * @param args
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void update(String sql,Object ...args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.取得連接
            conn = JDBCUtils.getTestConnection();
            //2.預編譯sql語句
            ps = conn.prepareStatement(sql);
            //3.填充佔位符
            for(int i = 0;i< args.length;i++){
                ps.setObject(i + 1,args[i]);
            }
            //4.執行
            ps.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5.關閉資源
            JDBCUtils.closeResource(conn,ps);
        }

    }

    //向test/customer添加一條紀錄
    @Test
    public void testInsert() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            //1.讀取配置文件中的信息
            InputStream inputStream = ConnectionSQL.class.getClassLoader().getResourceAsStream("jdbc.properties");

            Properties pros = new Properties();
            pros.load(inputStream);

            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            String url = pros.getProperty("url");
            String driverClass = pros.getProperty("driverClass");

            //2.加載驅動
            Class.forName(driverClass);

            //3.獲取連接
            connection = DriverManager.getConnection(url,user,password);

            //4.創建預處理sql
            String sql = "insert into customers(name,email,birth)values(?,?,?)"; //?是佔位符
            ps = connection.prepareStatement(sql);
            //5.填充佔位符(?)
            ps.setString(1, "阿米婭"); //注意編碼集
            ps.setString(2,"Amiya@rhodes.com");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse("1785-01-01");
            ps.setDate(3,new Date(date.getTime()));

            //6.執行
            ps.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }



    }
    //向test/customer修改一條紀錄
    @Test
    public void testUpdate() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.獲取連接
            conn = JDBCUtils.getTestConnection();
            //2.預編譯sql語句
            String sql = "update customers set name = ? where id = ?";
            ps = conn.prepareStatement(sql);
            //3.填充佔位符
            ps.setObject(1,"魔王");
            ps.setObject(2,"19");
            //4.執行
            ps.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5.資源關閉
            JDBCUtils.closeResource(conn,ps);
        }

    }
    //向test/customer刪除一條紀錄
    @Test
    public void testDelete() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.獲取連接
            conn = JDBCUtils.getTestConnection();
            //2.預編譯sql語句
            String sql = "DELETE FROM customers WHERE id = ?";
            ps = conn.prepareStatement(sql);
            //3.填充佔位符
            ps.setObject(1,"19");
            //4.執行
            ps.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5.資源關閉
            JDBCUtils.closeResource(conn,ps);
        }

    }
}
