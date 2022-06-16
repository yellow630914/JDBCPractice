import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrepareStatementQuery {

    @Test
    public void testGetInstances(){
        String sql1 = "SELECT id,name,email,birth FROM customers WHERE id > ?";
        List<Customer> customerList = getInstances(Customer.class,sql1,3);
        customerList.forEach(System.out::println);

    }

    @Test
    public void testGetInstance(){
        String sql1 = "SELECT id,name,email,birth FROM customers WHERE id = ?";
        Customer customer = getInstance(Customer.class,sql1,12);
        System.out.println(customer);

        String sql2 = """
                select order_id orderId,order_name orderName,order_date orderDate
                from `order`
                where order_id = ?;""";
        Order order = getInstance(Order.class,sql2,2);
        System.out.println(order);

    }

    /**
     * 回傳一個SQL搜尋語句的List
     * @param clazz 回傳的類型,取決於哪個表的
     * @param sql sql語句
     * @param args 填充佔位符
     * @param <T> 限定回傳的類型
     * @return List<T> 回傳一個T類型的List
     */
    public <T> List<T> getInstances(Class<T> clazz,String sql,Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //1.取得連接
            conn = JDBCUtils.getTestConnection();
            //2.預編譯SQL
            ps = conn.prepareStatement(sql);
            for (int i = 0;i<args.length;i++) {
                ps.setObject(i + 1,args[i]);
            }
            //3.取得結果集
            rs = ps.executeQuery();
            //取得結果集的元數據:ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通過ResultSetMetaData獲取資料的列數
            int columnCount = rsmd.getColumnCount();

            List<T> list = new ArrayList<>();
            while (rs.next()){
                //先創造一個空的物件
                T newInstance = clazz.newInstance();
                //依資料列數塞入屬性
                for(int i = 0;i<columnCount;i++){
                    //取得資料的值
                    Object columnValue = rs.getObject(i + 1);
                    //透過元資料取得此列的名稱
                    //getColumnName:直接取得列名
                    //getColumnLabel:取得列的別名
                    String ColumnLabel = rsmd.getColumnLabel(i + 1);

                    Field field = clazz.getDeclaredField(ColumnLabel);
                    field.setAccessible(true);
                    field.set(newInstance,columnValue);
                }
                list.add(newInstance);
            }
            return list;
        } catch (IOException | ClassNotFoundException | SQLException | NoSuchFieldException | IllegalArgumentException | SecurityException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }

        return null;
    }


    /**
     * 回傳一個sql查詢的T物件
     * @param clazz T的類型,取決於表
     * @param sql sql語句
     * @param args 填充佔位符
     * @param <T> 要取得的類型
     * @return 單個T物件
     */
    public <T> T getInstance(Class<T> clazz,String sql,Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //1.取得連接
            conn = JDBCUtils.getTestConnection();
            //2.預編譯SQL
            ps = conn.prepareStatement(sql);
            for (int i = 0;i<args.length;i++) {
                ps.setObject(i + 1,args[i]);
            }
            //3.取得結果集
            rs = ps.executeQuery();
            //取得結果集的元數據:ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通過ResultSetMetaData獲取資料的列數
            int columnCount = rsmd.getColumnCount();

            if(rs.next()){
                //先創造一個空的物件
                T newInstance = clazz.newInstance();
                //依資料列數塞入屬性
                for(int i = 0;i<columnCount;i++){
                    //取得資料的值
                    Object columnValue = rs.getObject(i + 1);
                    //透過元資料取得此列的名稱
                    //getColumnName:直接取得列名
                    //getColumnLabel:取得列的別名
                    String ColumnLabel = rsmd.getColumnLabel(i + 1);

                    Field field = clazz.getDeclaredField(ColumnLabel);
                    field.setAccessible(true);
                    field.set(newInstance,columnValue);
                }
                return newInstance;
            }
        } catch (IOException | ClassNotFoundException | SQLException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }

        return null;
    }
}
