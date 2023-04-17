package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 在DML语句中使用预编译SQL
 * INSERT语句:
 * INSERT INTO userinfo (username,password,nickname,age)
 * VALUES(?,?,?,?)
 *
 * UPDATE语句:
 * 例:修改密码
 * UPDATE userinfo
 * SET password=?
 * WHERE username=?
 *
 * DELETE语句
 * DELETE FROM userinfo
 * WHERE username=?
 *
 *
 */
public class JDBCDemo8 {
    public static void main(String[] args) {
        try (
                Connection connection = DBUtil.getConnection();
        ){
            String sql = "INSERT INTO userinfo(username,password,nickname,age) " +
                         "VALUES (?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,"王克晶");
            ps.setString(2,"666666");
            ps.setString(3,"克晶");
            ps.setInt(4,18);
            //由于上述预编译SQL属于DML语句,因此应当使用executeUpdate()
            int num = ps.executeUpdate();
            if(num>0){
                System.out.println("插入成功");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
