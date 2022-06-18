import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;

/**
 * 甚麼是事務操作?
 * 事務: 一組邏輯單元,使數據從一種狀態轉換到另一種狀態。
 *      >一組邏輯單元:一個或多個DML操作
 *
 * 2.事務處理原則:保证所有事务都作为一个工作单元来执行，即使出现了故障，都不能改变这种执行方式。
 *   当在一个事务中执行多个操作时，要么所有的事务都被提交(commit)，那么这些修改就永久地保存下来；
 *   要么数据库管理系统将放弃所作的所有修改，整个事务回滚(rollback)到最初状态。
 *
 * 3.數據依但提交就不可回滾。
 *
 * 4.那些操作會導致數據自動提交?
 *      >DDL操作一旦執行,都會自動提交。
 *      >DML操作默認會自動提交,但是可以通過 set autocommit = false的方式取消自動提交。
 *      >關閉連接時也會自動提交。
 */

public class TransactionTest {

    /**
     * 針對user_table來說,AA用戶給BB用戶轉帳100
     *
     * update user_table set balance = balance - 100 where user = 'AA';
     * update user_table set balance = balance + 100 where user = 'BB';
     */
    @Test
    public void testTransaction1(){
        String sql1 = "update user_table set balance = balance - 100 where user = ?";
        update(sql1,"AA");

        //模擬網路異常
        System.out.println(10/0);

        String sql2 = "update user_table set balance = balance + 100 where user = ?";
        update(sql2,"BB");

        System.out.println("轉帳成功");
    }

    /**
     * 同上,但運用事務操作避免異常導致操作錯誤
     */
    @Test
    public void testTransaction2() {
        Connection conn = null;
        try {
            //1.防止關閉連接的自動提交
            conn = JDBCUtils.getTestConnection();

            //2.取消自動提交
            conn.setAutoCommit(false);

            String sql1 = "update user_table set balance = balance - 100 where user = ?";
            updatePlus(conn,sql1,"AA");

            //模擬網路異常
            System.out.println(10/0);

            String sql2 = "update user_table set balance = balance + 100 where user = ?";
            updatePlus(conn,sql2,"BB");

            System.out.println("轉帳成功");

            //3.確認無異常一起提交
            conn.commit();
        } catch (IOException | ClassNotFoundException | SQLException | ArithmeticException e) {
            e.printStackTrace();
            //4.若是發生異常,回滾數據。
            try {
                conn.rollback();
                System.out.println("轉帳失敗");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {

            //主要針對於使用數據庫連接池時
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(conn,null);
        }


    }

    @Test
    public void testACID1() throws SQLException, IOException, ClassNotFoundException {
        Connection conn = JDBCUtils.getTestConnection();

        System.out.println(conn.getTransactionIsolation());
        conn.setAutoCommit(false);

        String sql = "select user,password,balance from user_table where user=?";
        User user = getInstance(conn,User.class,sql,"CC");



        System.out.println(user);
    }

    @Test
    public void testACID2() throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        Connection conn = JDBCUtils.getTestConnection();
        conn.setAutoCommit(false);

        String sql = "update user_table set balance = ? where user = ?";
        updatePlus(conn,sql,4000,"CC");

        Thread.sleep(15000);
        conn.commit();
        System.out.println("修改結束");


    }


    //通用的增刪改 ---- ver 1.0
    public int update(String sql,Object ...args) {
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
            return ps.executeUpdate();
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
        return 0;
    }

    //通用的增刪改 ---- ver 2.0 考慮事務操作
    public int updatePlus(Connection conn,String sql,Object ...args) {
        PreparedStatement ps = null;
        try {
            //1.預編譯sql語句
            ps = conn.prepareStatement(sql);
            //2.填充佔位符
            for(int i = 0;i< args.length;i++){
                ps.setObject(i + 1,args[i]);
            }
            //3.執行
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //4.關閉資源
            JDBCUtils.closeResource(null,ps);
        }
        return 0;
    }

    //通用的查詢操作  ---- 考慮事務操作
    public <T> T getInstance(Connection conn,Class<T> clazz,String sql,Object ...args){
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
