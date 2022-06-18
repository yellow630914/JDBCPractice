import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.jupiter.api.Test;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class C3P0Test {
    //方式一:
    @Test
    public void testGetConnection() throws PropertyVetoException, SQLException {
        //獲取c3p0數據庫連接池
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl("jdbc:mysql://localhost:3306/test?rewriteBatchedStatements=true");
        cpds.setUser("root");
        cpds.setPassword("ss630914");

        //setInitialPoolSize:設置初始時數據庫連接池中的連接數
        cpds.setInitialPoolSize(10);

        Connection conn = cpds.getConnection();
        System.out.println(conn);
    }

    //方式二:使用配置文件
    @Test
    public void docTest() throws SQLException {
        ComboPooledDataSource cpds = new ComboPooledDataSource("hellc3p0");
        Connection conn = cpds.getConnection();
        System.out.println(conn);
    }

    @Test
    public void TestUtils() throws SQLException {
        Connection conn = JDBCPoolUtils.getC3P0Connection();
        System.out.println(conn);
    }
}
