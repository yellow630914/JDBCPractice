import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DruidTest {

    @Test
    public void test1() throws Exception {
        Properties pros = new Properties();

        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        pros.load(is);

        DataSource ds = DruidDataSourceFactory.createDataSource(pros);
        Connection conn = ds.getConnection();

        System.out.println(conn);

    }

    @Test
    public void testDruidUtil() throws SQLException {
        System.out.println(JDBCPoolUtils.getDruidConnection());
    }
}
