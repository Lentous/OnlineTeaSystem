package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 执行DQL语句
 */
public class JDBCDemo3 {
    public static void main(String[] args) {
        try (
                //Connection可以利用自动关闭特性,使用完毕后关闭它
                Connection connection = DBUtil.getConnection();
        ){
            //查询老师的id,name,salary,age
            Statement statement = connection.createStatement();
            /*
                SELECT id,name,salary,age
                FROM teacher
             */
            String sql = "SELECT id,name,salary,age " +
                         "FROM teacher";
            /*
                ResultSet executeQuery(String sql)
                专门用来执行DQL语句的方法,方法执行后会返回一个查询结果集

                ResultSet的每一个实例用于表示一个查询结果集
             */
            ResultSet rs = statement.executeQuery(sql);
            /*
                boolean next()
                让结果集向下移动一条记录并表示该条记录
                当返回值为true时说明有下一条记录,false表示已经没有记录了
             */
            while(rs.next()){
                //通过结果集获取各字段的值
                //获取id字段的值 teacher表中id为INT型
                int id = rs.getInt("id");
//                int id = rs.getInt(1);//也可以指定字段下标,数据库下标从1开始
                //获取name字段的值 teacher表中name是VARCHAR类型(字符串)
                String name = rs.getString("name");
//                String name = rs.getString(2);
                //获取salary
                int salary = rs.getInt("salary");
//                int salary = rs.getInt(3);
                int age = rs.getInt("age");
//                int age = rs.getInt(4);
                System.out.println(id+","+name+","+salary+","+age);
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
