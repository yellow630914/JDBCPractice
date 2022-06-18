import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCPoolUtils {

    private static ComboPooledDataSource cpds = new ComboPooledDataSource("hellc3p0");

    public static Connection getC3P0Connection() throws SQLException {
        return cpds.getConnection();
    }
    private  static DataSource ds;
    static{
        try {
            Properties pros = new Properties();

            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
            pros.load(is);

            ds = DruidDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Connection getDruidConnection() throws SQLException {return ds.getConnection();}
}
