import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ApacheDBUtilsTest {

    /**
     * update
     */
    @Test
    public void testInsert(){
        Connection conn = null;
        try {
            QueryRunner qr = new QueryRunner();
            conn = JDBCPoolUtils.getDruidConnection();
            String sql = "insert into customers(name,email,birth)values(?,?,?)";

            int insertCount = qr.update(conn,sql,"愛雅法拉","eyjafjalla@rhodes.com","1783-10-2");
            System.out.println("添加了" + insertCount + "條紀錄");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    /**
     * BeanHandler
     */
    @Test
    public void testQuery1() {
        Connection conn = null;
        try {
            QueryRunner qr = new QueryRunner();
            conn = JDBCPoolUtils.getDruidConnection();
            String sql = "select id,name,email,birth from customers where id=?";
            BeanHandler<Customer> handler = new BeanHandler<>(Customer.class);
            Customer customer = qr.query(conn,sql,handler,28);
            System.out.println(customer);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    /**
     * BeanListHandler
     */
    @Test
    public void testQuery2(){
        Connection conn = null;
        try {
            QueryRunner qr = new QueryRunner();
            conn = JDBCPoolUtils.getDruidConnection();
            String sql = "select id,name,email,birth from customers where id>?";
            BeanListHandler<Customer> handler = new BeanListHandler<>(Customer.class);
            List<Customer> list = qr.query(conn,sql,handler,7);
            for (Customer cust:list) {
                System.out.println(cust);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    /**
     * MapHandler
     */
    @Test
    public void testQuery3(){
        Connection conn = null;
        try {
            QueryRunner qr = new QueryRunner();
            conn = JDBCPoolUtils.getDruidConnection();
            String sql = "select id,name,email,birth from customers where id=?";
            MapHandler handler = new MapHandler();
            Map<String,Object> map = qr.query(conn,sql,handler,7);
            System.out.println(map);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    /**
     * MapListHandler
     */
    @Test
    public void testQuery4(){
        Connection conn = null;
        try {
            QueryRunner qr = new QueryRunner();
            conn = JDBCPoolUtils.getDruidConnection();
            String sql = "select id,name,email,birth from customers where id>?";
            MapListHandler handler = new MapListHandler();
            List<Map<String,Object>> list = qr.query(conn,sql,handler,7);
            for (Map m:list) {
                System.out.println(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    /**
     * ScalarHandler
     */
    @Test
    public void testQuery5(){
        Connection conn = null;
        try {
            QueryRunner qr = new QueryRunner();
            conn = JDBCPoolUtils.getDruidConnection();
            String sql = "select count(*) from customers";
            ScalarHandler<Long> handler = new ScalarHandler();
            Long customersCount = qr.query(conn,sql,handler);
            System.out.println(customersCount);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }
    @Test
    public void testQuery6(){
        Connection conn = null;
        try {
            QueryRunner qr = new QueryRunner();
            conn = JDBCPoolUtils.getDruidConnection();
            String sql = "select max(birth) from customers";
            ScalarHandler<Date> handler = new ScalarHandler();
            Date maxBirth = qr.query(conn,sql,handler);
            System.out.println(maxBirth);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

}
