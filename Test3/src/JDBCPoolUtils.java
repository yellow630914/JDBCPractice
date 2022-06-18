import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCPoolUtils {

    private static ComboPooledDataSource cpds = new ComboPooledDataSource("hellc3p0");

    public static Connection getC3P0Connection() throws SQLException {
        return cpds.getConnection();
    }
}
