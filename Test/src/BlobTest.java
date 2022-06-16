import org.junit.Test;

import java.io.*;
import java.sql.*;

/**
 * 測試使用PreparedStatement操作Blob類型的數據
 */
public class BlobTest {
    //向customers插入Blob類型數據
    @Test
    public void testInsert() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getTestConnection();
            String sql = "insert into customers(name,email,birth,photo)values(?,?,?,?)";

            ps = conn.prepareStatement(sql);

            ps.setObject(1,"史爾特爾");
            ps.setObject(2,"surtr@rhodes.com");
            ps.setObject(3,"1971-05-06");
            FileInputStream fis = new FileInputStream(new File("surtr.jpg"));
            ps.setObject(4,fis);

            ps.execute();
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

    //向customers查詢Blob類型數據
    @Test
    public void testSearch() throws SQLException, IOException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            conn = JDBCUtils.getTestConnection();
            String sql = "select name,photo from customers where id =?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1,25);

            rs = ps.executeQuery();
            if(rs.next()){
                Customer customer = new Customer();

                String name = rs.getString("name");

                customer.setName(name);

                //將Blob類型的資料下載下來,保存在本地。
                Blob photo = rs.getBlob("photo");
                is = photo.getBinaryStream();
                fos = new FileOutputStream("surtrFromSql.jpg");
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1){
                    fos.write(buffer, 0, len);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(conn,ps,rs);
        }

    }
}
