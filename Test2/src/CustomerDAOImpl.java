import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class CustomerDAOImpl extends DAOBase implements CustomerDAO{
    @Override
    public void insert(Connection conn, Customer cust) {
        String sql = "insert into customers(name,email,birth)values(?,?,?)";
        updatePlus(conn,sql,cust.getName(),cust.getEmail(),cust.getBirth());
    }

    @Override
    public void deleteByID(Connection conn, int id) {
        String sql = "delete from customers where id =?";
        updatePlus(conn,sql,id);
    }

    @Override
    public void update(Connection conn, Customer cust) {
        String sql = "update customers set name=?,email=?,birth=? where id =?";
        updatePlus(conn,sql,cust.getName(),cust.getEmail(),cust.getBirth(),cust.getId());
    }

    @Override
    public Customer getCustomerByID(Connection conn,int id) {
        String sql = "select id,name,email,birth from customers where id=?";
        return getInstance(conn,Customer.class,sql,id);
    }

    @Override
    public List<Customer> getAll(Connection conn) {
        String sql = "select id,name,email,birth from customers";
        return getInstances(conn,Customer.class,sql);
    }

    @Override
    public Long getCount(Connection conn) throws SQLException {
        String sql = "select count(*) from customers";
        return getValue(conn,sql);
    }

    @Override
    public Date getMaxBirth(Connection conn) throws SQLException {
        String sql = "select max(birth) from customers";
        return getValue(conn,sql);
    }


}
