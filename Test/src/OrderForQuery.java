import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;

public class OrderForQuery {
    @Test
    public void testOrderForQuery(){
        String sql = "select order_id orderId,order_name orderName,order_date orderDate\n" +
                "from `order`\n" +
                "where order_id = ?;";
        Order order = orderForQuery(sql,1);
        System.out.println(order);
    }

    public Order orderForQuery(String sql, Object...args){
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
                Order order = new Order();
                //依資料列數塞入屬性
                for(int i = 0;i<columnCount;i++){
                    //取得資料的值
                    Object columnValue = rs.getObject(i + 1);
                    //透過元資料取得此列的名稱
                    //getColumnName:直接取得列名
                    //getColumnLabel:取得列的別名
                    String ColumnLabel = rsmd.getColumnLabel(i + 1);

                    Field field = Order.class.getDeclaredField(ColumnLabel);
                    field.setAccessible(true);
                    field.set(order,columnValue);
                }
                return order;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }

        return null;
    }

}
