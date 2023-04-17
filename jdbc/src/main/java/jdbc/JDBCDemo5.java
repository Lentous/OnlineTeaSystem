package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 别名
 * 当我们的SELECT子句中含有函数,聚合函数,表达式时应当为该字段添加别名,然后在遍历结果集时通过
 * 别名获取该字段的值
 */
public class JDBCDemo5 {
    public static void main(String[] args) {
        try (
                Connection connection = DBUtil.getConnection();
        ){
            //统计1年级1班一共多少人
            /*
                SELECT COUNT(*) count
                FROM class c
                JOIN student s ON s.class_id=c.id
                WHERE c.name='1年级1班'
             */
            Statement statement = connection.createStatement();
            String sql = "SELECT COUNT(*) count " +
                         "FROM class c " +
                         "JOIN student s ON s.class_id=c.id " +
                         "WHERE c.name='1年级1班'";
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){//因为统计的结果集只有一条记录,因此使用了if而不是while
                int count = rs.getInt("count");
                System.out.println("共"+count+"人");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}




