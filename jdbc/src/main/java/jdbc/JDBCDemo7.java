package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 预编译SQL
 * 当SQL中含有变量时,通常我们不能直接拼接SQL语句,因为这存在SQL注入攻击.
 *
 * 我们可以使用预编译SQL语句.在预编译SQL语句中变量部分先用"?"代替.然后将整条预编译SQL传给
 * 数据库使其生成执行计划(此时数据库就已经确定了SQL语义),然后等待我们传入"?"对应的值然后执行
 * 这条SQL.此时无论我们传入的值是什么,数据库都仅会将其当做值看待,并不会在改变SQL语义.
 *
 * 例如:
 * 为登录逻辑编写预编译SQL语句
 *
 * SELECT id,username,password,nickname
 * FROM userinfo
 * WHERE username=? AND password=?
 *
 * 先将这个SQL发送给数据库,此时数据库就理解了这个SQL的语义.但是还不能执行,因为缺少?表达的两个值
 *
 * 当我们需要执行这条SQL时,我们仅需要再传入两个值给数据库即可
 * 此时就算我们传入的是:
 * 用户名:aaa
 * 密码:aaa' OR '1'='1
 * 数据
 *
 *
 * 库也仅会将aaa理解为是用户名的值.而aaa' OR '1'='1仅会理解为是密码的值
 *
 *
 * 在预编译SQL中,"?"只能替代"值"
 */
public class JDBCDemo7 {
    public static void main(String[] args) {
        try (
                Connection connection = DBUtil.getConnection();
        ){
            String sql = "SELECT id,username,password,nickname,age " +
                         "FROM userinfo " +
                         "WHERE username=? AND password=?";
            //创建PrepareStatement是要现将预编译SQL传入,此时该方法会将该SQL传给数据库
            PreparedStatement ps = connection.prepareStatement(sql);
            //为?指定值
            //给第一个"?"(用户名的值)指定值,由于用户名为VARCHAR类型,因此这里要设定字符串
            //参数1:第几个"?".下标从1开始.
//            ps.setString(1,"张三");
//            ps.setString(2,"123456");
            ps.setString(1,"abbaba");
            ps.setString(2,"a' OR '1'='1");//不会改变语义,仅将其当做密码的值看待
            //执行该DQL,此时不需要再传入SQL语句了.执行时会将上述设置的"?"表达的值传递给数据库
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
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
