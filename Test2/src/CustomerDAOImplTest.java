import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;


class CustomerDAOImplTest {

    private final CustomerDAOImpl dao = new CustomerDAOImpl();

    @org.junit.jupiter.api.Test
    void insert(){
        Connection conn = null;
        try {
            conn = JDBCUtils.getTestConnection();
            Customer customer = new Customer(1,"阿仁","afly@123.com",new Date(5616516189L));
            dao.insert(conn,customer);
            System.out.println("添加成功");
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @org.junit.jupiter.api.Test
    void deleteByID() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getTestConnection();
            int id = 27;
            dao.deleteByID(conn,id);
            System.out.println("刪除成功");
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @org.junit.jupiter.api.Test
    void update() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getTestConnection();
            Customer customer = new Customer(16,"阿仁","afly@123.com",new Date(5616516189L));
            dao.update(conn,customer);
            System.out.println("修改成功");
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @org.junit.jupiter.api.Test
    void getCustomerByID() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getTestConnection();
            int id = 26;
            Customer customer = dao.getCustomerByID(conn,id);
            System.out.println(customer);
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @org.junit.jupiter.api.Test
    void getAll() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getTestConnection();
            List<Customer> list = dao.getAll(conn);
            for (Customer cust:list) {
                System.out.println(cust);
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @org.junit.jupiter.api.Test
    void getCount() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getTestConnection();
            long count = dao.getCount(conn);
            System.out.println(count);
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @org.junit.jupiter.api.Test
    void getMaxBirth() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getTestConnection();
            Date maxBirth = dao.getMaxBirth(conn);
            System.out.println(maxBirth);
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }
}