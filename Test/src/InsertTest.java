import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 使用PreparedStatement實現批量數具操作
 *
 * update、delete本身就具有批量效果
 *
 *
 */

public class InsertTest {

    /**
     * 批量插入20000條資料到goods表中
     */
    @Test
    public void testInsert() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getTestConnection();
            String sql = "insert into goods(name)values(?)";
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < 20000; i++) {
                ps.setObject(1,"good_" + i);

                ps.execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps);
        }


    }


    /**
     * 運用addBatch(),executeBatch(),clearBatch()更快的執行sql語句
     *
     * !!!!  注意:sql默認不支持Batch,需要在Connection的url後加入
     *          ?rewriteBatchedStatements=true   !!!!
     *
     * Connection.setAutoCommit(false):設置為不要自動提交
     * Connection.commit:手動提交
     *
     */
    @Test
    public void testInsertBetter(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getTestConnection();

            //設置不允許自動提交數據
            conn.setAutoCommit(false);

            String sql = "insert into goods(name)values(?)";
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < 20000; i++) {
                ps.setObject(1,"good_" + i);

                //1.存下sql語句
                ps.addBatch();

                if(i % 500 == 0){
                    //2.執行Batch中的sql
                    ps.executeBatch();
                    //3.清空Batch
                    ps.clearBatch();
                }

            }
            //統一提交
            conn.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps);
        }
    }
}
