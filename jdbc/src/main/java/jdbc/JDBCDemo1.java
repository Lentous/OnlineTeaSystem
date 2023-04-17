package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC Java DataBase Connectivity  java数据库连接
 * JDBC是JAVA提供的一套标准接口,用于连接并操作数据库.
 * 不同的数据库提供商都提供了一套JDBC的实现类用于操作其提供的DBMS.而这套实现类被我称为连接该
 * DBMS的驱动.
 *
 * 连接数据库的步骤
 * 1:在项目中导入对应DBMS的驱动包(Maven中添加对应依赖)
 * 2:使用JDBC进行数据库连接及操作
 *
 */
public class JDBCDemo1 {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        /*
            JDBC连接数据库的大致步骤
            1:加载驱动 Class.forName()去加载对应的驱动类
              不同的驱动,类名不完全一致
            2:使用DriverManager与数据库建立连接.此时需要指定数据库的地址,用户名和密码
              地址的格式不同的数据库也不完全一样
              连接后会获得一个Connection对象,表示该数据库的连接
            3:通过Connection获取执行SQL的执行对象Statement
            4:使用Statement执行SQL语句(DDL,DML,DQL...)
            5:获得执行结果,如果是执行的DQL会得到一个查询结果集
            6:遍历结果集得到查询数据
         */
        //不同数据库驱动类写法不同,但是每种数据库永远是固定的
        Class.forName("com.mysql.cj.jdbc.Driver");//mysql驱动
        /*
            参数1:数据库地址,不同的数据库格式不完全相同
            参数2:数据库用户名
            参数3:数据库密码
         */
        Connection connection = DriverManager.getConnection(
                //  jdbc:mysql://localhost:3306/数据库名?...
                "jdbc:mysql://localhost:3306/birddb?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true",
                "root",
                "root");
        System.out.println("与数据库连接成功!");

        //3通过连接对象创建一个执行对象(用于执行SQL语句)
        Statement statement = connection.createStatement();
        /*
            新建一张表userinfo
            CREATE TABLE userinfo(
                id INT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(30),
                password VARCHAR(30),
                nickname VARCHAR(30),
                age INT(3)
            )
         */
        String sql = "CREATE TABLE userinfo( " + //每行默认追加一个空格
                "                id INT PRIMARY KEY AUTO_INCREMENT, " +
                "                username VARCHAR(30), " +
                "                password VARCHAR(30), " +
                "                nickname VARCHAR(30), " +
                "                age INT(3) " +
                "     )";
        /*
            Statement提供的方法:
            boolean execute(String sql)
            该方法可以执行所有种类的SQL语句:DDL,DML,DQL都可以用这个执行
            当返回值为true时说明执行SQL后有一个查询结果集.
            由于DML,DQL有专属的执行方法,因此通常execute方法仅用于执行DDL
         */
        statement.execute(sql);
        System.out.println("表创建完毕!");

        connection.close();//当不再操作数据库时,关闭连接,释放资源


    }
}










