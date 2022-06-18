import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionSQL {

    //方式一
    @Test
    public void testConnection1() throws SQLException {
        Driver driver = new com.mysql.cj.jdbc.Driver();
        //jdbc:mysql 協議
        //localhost:3306 IP
        //test 資料庫
        String url = "jdbc:mysql://localhost:3306/test";
        //帳號密碼
        Properties info = new Properties();
        info.setProperty("user","root");
        info.setProperty("password","ss630914");


        Connection connection = driver.connect(url,info);
        System.out.println(connection);
    }

    //方式二: 使用反射讓第三方API不直接出現在code之中,具有更好的可移植性。
    @Test
    public void testConnection2() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        //1.獲取Driver實現類對象: 使用反射
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver)clazz.newInstance();
        //2.提供資料庫
        String url = "jdbc:mysql://localhost:3306/test";
        //3.提供連接所需資料
        Properties info = new Properties();
        info.setProperty("user","root");
        info.setProperty("password","ss630914");
        //4.獲取連接
        Connection connection = driver.connect(url,info);
        System.out.println(connection);
    }

    //方式三:使用DriverManager取代Driver
    @Test
    public void testConnection3() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        //1.獲取Driver實現類對象: 使用反射
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver)clazz.newInstance();
        //2.提供連接所需資料
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "ss630914";
        //3.註冊驅動
        DriverManager.registerDriver(driver);
        //4.獲取連接
        Connection connection = DriverManager.getConnection(url,user,password);
        System.out.println(connection);
    }

    //方式四:在mysql的Diver類中的靜態方法可以自動查找已加載的Driver
    @Test
    public void testConnection4() throws ClassNotFoundException, SQLException {
        //1.提供三個連接的基本信息
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "ss630914";
        //2.加載Driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        //3.獲取連接
        Connection connection = DriverManager.getConnection(url,user,password);
        System.out.println(connection);

    }

    //方式五:將資料庫連接所需的信息聲明在配置文件中,通過讀取配置文件的方式獲取連接
    //代碼的解耦: 資料與代碼分開
    //可移值性: 只需更改配置文件即可移值
    //
    @Test
    public void testConnection5() throws IOException, ClassNotFoundException, SQLException {
        //1.讀取配置文件中的信息
        InputStream inputStream = ConnectionSQL.class.getClassLoader().getResourceAsStream("jdbc.properties");

        Properties pros = new Properties();
        pros.load(inputStream);

        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //2.加載驅動
        Class.forName(driverClass);

        //3.獲取連接
        Connection connection = DriverManager.getConnection(url,user,password);
        System.out.println(connection);

    }
}
