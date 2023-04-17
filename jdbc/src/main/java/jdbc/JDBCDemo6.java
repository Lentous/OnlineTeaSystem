package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 用户登录
 */
public class JDBCDemo6 {
    public static void main(String[] args) {
        System.out.println("欢迎登录");
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入用户名");
        String username = scanner.nextLine();
        System.out.println("请输入密码");
        String password = scanner.nextLine();

        try (
                Connection connection = DBUtil.getConnection();
        ){
            Statement statement = connection.createStatement();
            /*
                SELECT id,username,password,nickname,age
                FROM userinfo
                WHERE username='XXX' AND password='XXX'

                拼接SQL语句存在SQL注入攻击
                例如:
                用户输入的用户名:aaa(随便写)
                密码:aaa' OR '1'='1

                此时拼接到上面的SQL后语义发生了改变
                SELECT id,username,password,nickname,age
                FROM userinfo
                WHERE username='aaa' AND password='aaa' OR '1'='1'

             */
            String sql = "SELECT id,username,password,nickname,age " +
                         "FROM userinfo " +
                         "WHERE username='"+username+"' " +
                         "AND password='"+password+"'";
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){//根据该用户名和密码作为过滤条件查询到记录,说明登录成功
                String nickname = rs.getString("nickname");
                System.out.println("登录成功!欢迎回来:"+nickname);
            }else{
                System.out.println("登录失败,用户名或密码错误");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
