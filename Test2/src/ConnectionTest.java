import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionTest {

    @Test
    public void test() throws SQLException, IOException, ClassNotFoundException {
        Connection conn = JDBCUtils.getTestConnection();
        System.out.println(conn);
    }

}
