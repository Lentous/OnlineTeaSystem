package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 关联查询
 */
public class JDBCDemo4 {
    public static void main(String[] args) {
        try (
                Connection connection = DBUtil.getConnection();
        ){
            Statement statement = connection.createStatement();
            /*
                查询刘苍松所带班级的学生信息
                列出:学生名字,年龄,性别,所在班级名称,楼层,老师的名字,年龄,工资
                SELECT s.name,s.age,s.gender,c.name,c.floor,t.name,t.age,t.salary
                FROM teacher t
                JOIN class c ON c.teacher_id=t.id
                JOIN student s ON s.class_id=c.id
                WHERE t.name='刘苍松'
             */
            String sql = "SELECT s.name,s.age,s.gender,c.name,c.floor,t.name,t.age,t.salary " +
                         "FROM teacher t " +
                         "JOIN class c ON c.teacher_id=t.id " +
                         "JOIN student s ON s.class_id=c.id " +
                         "WHERE t.name='刘苍松'";
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                String sname = rs.getString("s.name");
                int sage = rs.getInt("s.age");
                String sgender = rs.getString("s.gender");
                String cname = rs.getString("c.name");
                int cfloor = rs.getInt("c.floor");
                String tname = rs.getString("t.name");
                int tage = rs.getInt("t.age");
                int tsalary = rs.getInt("t.salary");
                System.out.println(sname+","+sage+","+sgender+","+cname+","+cfloor+","+tname+","+tage+","+tsalary);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}






