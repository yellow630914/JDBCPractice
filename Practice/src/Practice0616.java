import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Practice0616 {

    @Test
    public void practice1() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("請輸入用戶名:");
        String name = scanner.next();
        System.out.println("請輸入email:");
        String email = scanner.next();
        System.out.println("請輸入生日(EX:1997-02-09):");
        String birth = scanner.next();

        String sql = "insert into customers(name,email,birth)values(?,?,?)";
        int changeCount = update(sql,name,email,birth);
        if(changeCount > 0){
            System.out.println("添加成功");
        }else {
            System.out.println("添加失敗");
        }

    }

    @Test
    public void practice2(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("輸入考試分級(4/6):");
        int type = scanner.nextInt();
        System.out.println("身分證號:");
        String IDCard = scanner.next();
        System.out.println("考試證號:");
        String examCard = scanner.next();
        System.out.println("學生姓名:");
        String studentName = scanner.next();
        System.out.println("地點:");
        String location = scanner.next();
        System.out.println("成績:");
        String grade = scanner.next();

        String sql = "insert into examstudent(type,IDCard,ExamCard,StudentName,location,grade)values(?,?,?,?,?,?)";
        int updateCount = update(sql,type,IDCard,examCard,studentName,location,grade);
        if(updateCount > 0){
            System.out.println("添加成功");
        }else {
            System.out.println("添加失敗");
        }

    }

    @Test
    public void queryWithIDCardOrExamCard(){
        System.out.println("請選擇要輸入的類型:");
        System.out.println("a.准考證號");
        System.out.println("b.身分證號");
        Scanner scanner = new Scanner(System.in);
        String selection = scanner.next();
        if("a".equalsIgnoreCase(selection)){
            System.out.println("請輸入准考證號: ");
            String examCard = scanner.next();
            String sql = """
                    select FlowID flowID,`Type` `type`,IDCard ,ExamCard examCard,StudentName name,Location location,Grade grade\s
                    from examstudent
                    where ExamCard = ?;""";
            ExamStudent student = getInstance(ExamStudent.class,sql,examCard);
            if(student != null){
                System.out.println(student);
            }else{
                System.out.println("輸入的准考證號有誤!");
            }
        }else if("b".equalsIgnoreCase(selection)){
            System.out.println("請輸入身分證號: ");
            String IDCard = scanner.next();
            String sql = """
                    select FlowID flowID,`Type` `type`,IDCard ,ExamCard examCard,StudentName name,Location location,Grade grade\s
                    from examstudent
                    where IDCard = ?;""";
            ExamStudent student = getInstance(ExamStudent.class,sql,IDCard);
            if(student != null){
                System.out.println(student);
            }else{
                System.out.println("輸入的身分證號有誤!");
            }
        }else{
            System.out.println("輸入有誤,請重新進入程序。");
        }
    }

    @Test
    public void testDeleteExamStudent(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("請輸入准考證號: ");
        String examCard = scanner.next();

        String sql = "delete from examstudent where examCard = ?";
        int deleteCount = update(sql,examCard);
        if(deleteCount > 0){
            System.out.println("刪除成功");
        }else {
            System.out.println("查無此人,刪除失敗");
        }
    }

    static public int update(String sql,Object ...args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1.取得連接
            conn = JDBCUtils.getTestConnection();
            //2.預編譯sql語句
            ps = conn.prepareStatement(sql);
            //3.填充佔位符
            for(int i = 0;i< args.length;i++){
                ps.setObject(i + 1,args[i]);
            }
            //4.執行
            /*
                ps.execute();
                如果執行的是查詢操作,有返回結果,返回true
                如果執行的是增刪改操作,返回false

                ps.executeUpdate()
                返回增刪查操作影響的行數
             */

            return ps.executeUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5.關閉資源
            JDBCUtils.closeResource(conn,ps);
        }
        return 0;
    }

    static public <T> List<T> getInstances(Class<T> clazz, String sql, Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //1.取得連接
            conn = JDBCUtils.getTestConnection();
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
        } catch (IOException | ClassNotFoundException | SQLException | NoSuchFieldException | IllegalArgumentException | SecurityException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }

        return null;
    }

    public <T> T getInstance(Class<T> clazz,String sql,Object ...args){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //1.取得連接
            conn = JDBCUtils.getTestConnection();
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
        } catch (IOException | ClassNotFoundException | SQLException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }

        return null;
    }
}


