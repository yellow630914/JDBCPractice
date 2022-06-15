import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;

public class CustomerForQuery {
    @Test
    public void testQueryForCustomers(){
        String sql = "SELECT id,name,birth,email FROM customers WHERE id = ?";
        Customer customer = queryForCustomers(sql,20);
        System.out.println(customer);

    }

    /**
     * 取得單一個Customer查詢的通用操作
     * @param sql 帶有佔位符的SQL語句
     * @param args 填充佔位符的陣列
     * @return 一個Customer物件
     */
    public Customer queryForCustomers(String sql,Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            //1.取得連接
            conn = JDBCUtils.getTestConnection();

            ps = conn.prepareStatement(sql);
            //2.填充佔位符
            for (int i = 0;i<args.length;i++) {
                ps.setObject(i + 1,args[i]);
            }
            resultSet = ps.executeQuery();
            //獲取結果集的元數據:ResultSetMetaData
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            //通過ResultSetMetaData獲取資料的列數
            int columnCount = resultSetMetaData.getColumnCount();

            if(resultSet.next()){
                //先創造一個空的物件
                Customer customer = new Customer();
                //依資料列數塞入屬性
                for (int i = 0;i < columnCount;i++){
                    //取得資料的值
                    Object columnValue = resultSet.getObject(i + 1);
                    //透過元資料取得此列的名稱
                    String columnName = resultSetMetaData.getColumnName(i + 1);
                    //以取得地此列名稱創建Customer的屬性反射
                    Field field = Customer.class.getDeclaredField(columnName);
                    //開通修改權限
                    field.setAccessible(true);
                    //設定customer此列的屬性
                    field.set(customer,columnValue);
                }
                return customer;
            }
        } catch (IOException | ClassNotFoundException | SQLException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,resultSet);
        }


        return null;

    }

    @Test
    public void testQuery1() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            //1.取得連結
            conn = JDBCUtils.getTestConnection();
            //2.預編譯sql
            String sql = "SELECT id,name,email,birth FROM customers WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setObject(1,20);
            //3.執行,返回結果
            resultSet = ps.executeQuery();
            //處理結果集
            if(resultSet.next()){//next():判斷下一條是否有數據,如果有則返回true指針下移,若無則跳出。
                //獲取當前各段值
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                Date birth = resultSet.getDate(4);

                Customer customer = new Customer(id,name,email,birth);
                System.out.println(customer);
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            //4.關閉資源
            JDBCUtils.closeResource(conn,ps,resultSet);
        }


    }
}
