import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DAOBase {
    /**
     * 通用查詢
     */
    public <T> T getInstance(Connection conn, Class<T> clazz, String sql, Object ...args){
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //2.預編譯SQL
            ps = conn.prepareStatement(sql);
            for (int i = 0;i<args.length;i++) {
                ps.setObject(i + 1,args[i]);
            }
            //3.取得結果集
            rs = ps.executeQuery();
            //取得結果集的元數據:ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通過ResultSetMetaData獲取資料的列數
            int columnCount = rsmd.getColumnCount();

            if(rs.next()){
                //先創造一個空的物件
                T newInstance = clazz.newInstance();
                //依資料列數塞入屬性
                for(int i = 0;i<columnCount;i++){
                    //取得資料的值
                    Object columnValue = rs.getObject(i + 1);
                    //透過元資料取得此列的名稱
                    //getColumnName:直接取得列名
                    //getColumnLabel:取得列的別名
                    String ColumnLabel = rsmd.getColumnLabel(i + 1);

                    Field field = clazz.getDeclaredField(ColumnLabel);
                    field.setAccessible(true);
                    field.set(newInstance,columnValue);
                }
                return newInstance;
            }
        } catch (SQLException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }

        return null;
    }

    /**
     * 通用多項查詢
     */
    public <T> List<T> getInstances(Connection conn,Class<T> clazz, String sql, Object ...args){
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //2.預編譯SQL
            ps = conn.prepareStatement(sql);
            for (int i = 0;i<args.length;i++) {
                ps.setObject(i + 1,args[i]);
            }
            //3.取得結果集
            rs = ps.executeQuery();
            //取得結果集的元數據:ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通過ResultSetMetaData獲取資料的列數
            int columnCount = rsmd.getColumnCount();

            List<T> list = new ArrayList<>();
            while (rs.next()){
                //先創造一個空的物件
                T newInstance = clazz.newInstance();
                //依資料列數塞入屬性
                for(int i = 0;i<columnCount;i++){
                    //取得資料的值
                    Object columnValue = rs.getObject(i + 1);
                    //透過元資料取得此列的名稱
                    //getColumnName:直接取得列名
                    //getColumnLabel:取得列的別名
                    String ColumnLabel = rsmd.getColumnLabel(i + 1);

                    Field field = clazz.getDeclaredField(ColumnLabel);
                    field.setAccessible(true);
                    field.set(newInstance,columnValue);
                }
                list.add(newInstance);
            }
            return list;
        } catch (SQLException | NoSuchFieldException | IllegalArgumentException | SecurityException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null,ps,rs);
        }

        return null;
    }

    /**
     * 通用增刪改
     */
    public int updatePlus(Connection conn,String sql,Object ...args) {
        PreparedStatement ps = null;
        try {
            //1.預編譯sql語句
            ps = conn.prepareStatement(sql);
            //2.填充佔位符
            for(int i = 0;i< args.length;i++){
                ps.setObject(i + 1,args[i]);
            }
            //3.執行
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //4.關閉資源
            JDBCUtils.closeResource(null,ps);
        }
        return 0;
    }

    /**
     * 查詢特殊值的通用方法
     */
    public <E> E getValue(Connection conn, String sql, Object ...args) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            rs = ps.executeQuery();
            if(rs.next()){
                return (E)rs.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null,ps,rs);
        }


        return null;
    }


}
