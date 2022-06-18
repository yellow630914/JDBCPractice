import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * 此接口針對於規範customers表的常用操作
 */
public interface CustomerDAO {
    /**
     * 添加cust到資料庫中
     */
    void insert(Connection conn,Customer cust);

    /**
     * 依ID刪除紀錄
     */
    void deleteByID(Connection conn,int id);

    /**
     * 依cust修改資料庫中的紀錄
     */
    void update(Connection conn,Customer cust);

    /**
     * 依ID查詢資料庫中的Customer
     */
    Customer getCustomerByID(Connection conn,int id);

    /**
     * 取得所有Customer
     */
    List<Customer> getAll(Connection conn);

    /**
     * 取得資料條目數
     */
    Long getCount(Connection conn) throws SQLException;

    /**
     * 取得最大生日
     */
    Date getMaxBirth(Connection conn) throws SQLException;

}
