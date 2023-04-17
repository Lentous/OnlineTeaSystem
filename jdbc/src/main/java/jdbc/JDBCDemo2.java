package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 执行DML语句
 */
public class JDBCDemo2 {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/birddb?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true",
                "root",
                "root");
        System.out.println("与数据库连接成功!");
        Statement statement = connection.createStatement();
        /*
            向userinfo表中插入一条记录
            INSERT INTO userinfo (username,password,nickname,age)
            VALUES('张三','123456','阿三',22)
         */
//        String sql = "INSERT INTO userinfo (username,password,nickname,age) " +
//                     "VALUES('张三','123456','阿三',22)";

        System.out.println("欢迎注册");
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入用户名");
        String username = scanner.nextLine();
        System.out.println("请输入密码");
        String password = scanner.nextLine();
        System.out.println("请输入昵称");
        String nickname = scanner.nextLine();
        System.out.println("请输入年龄");
        int age = scanner.nextInt();
        String sql = "INSERT INTO userinfo (username,password,nickname,age) " +
                     "VALUES('"+username+"','"+password+"','"+nickname+"',"+age+")";

        /*
            int executeUpdate(String sql)
            用于执行DML语句的方法,该方法的返回值表示执行后影响了表中多少条记录
         */
        int num = statement.executeUpdate(sql);
        if(num>0){
            System.out.println("插入成功");
        }

        connection.close();
    }
}
