package com.ycy.tea.controller;

import com.ycy.tea.annotations.Controller;
import com.ycy.tea.annotations.RequestMapping;
import com.ycy.tea.entity.User;
import com.ycy.tea.http.HttpServletRequest;
import com.ycy.tea.http.HttpServletResponse;
import com.ycy.tea.util.DBUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  处理用户相关业务的类
 */
//V17新增内容 添加相应注释
@Controller
public class UserController {

    //用于表示存储所有用户文件的users目录
    private static File userDir;

    static{
        userDir = new File("./users");
        if(!userDir.exists()){
            userDir.mkdirs();
        }
    }

    /**
     * 处理用户注册
     * @param request
     * @param response
     */
    @RequestMapping("/regUser")//V17新增内容 添加相应注释
    public void reg(HttpServletRequest request, HttpServletResponse response){
        System.out.println("开始处理用户注册!!!!!!!");
        /*
                1通过request获取表单数据
                2将用户信息以User对象表示,并序列化到文件中
                3设置响应对象让浏览器查看处理结果页面
         */
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");
        System.out.println(username+","+password+","+nickname+","+ageStr);

        //必要的验证
        if(username==null||username.isEmpty()||
                password==null||password.isEmpty()||
                nickname==null||nickname.isEmpty()||
                ageStr==null||ageStr.isEmpty()||!ageStr.matches("[0-9]{1,3}")
        ){
            response.sendRedirect("/reg_info_error.html");
            return;
        }

        //V20改为数据库操作
        try (
                Connection connection = DBUtil.getConnection();
        ){
            //判断该用户是否重复--用该用户名去userinfo表中查询是否存在
            String sql1 = "SELECT username FROM userinfo WHERE username=?";
            PreparedStatement ps1 = connection.prepareStatement(sql1);
            ps1.setString(1,username);
            ResultSet rs = ps1.executeQuery();
            if(rs.next()){//查询到记录说明为重复用户名
                response.sendRedirect("/have_user.html");
                return;
            }
            //不是重复用户就保存到userinfo表中
            String sql2 = "INSERT INTO userinfo(username,password,nickname,age) " +
                          "VALUES (?,?,?,?)";
            PreparedStatement ps2 = connection.prepareStatement(sql2);
            ps2.setString(1,username);
            ps2.setString(2,password);
            ps2.setString(3,nickname);
            int age = Integer.parseInt(ageStr);//年龄要转换为整数
            ps2.setInt(4,age);
            int num = ps2.executeUpdate();
            if(num>0){
                response.sendRedirect("/reg_success.html");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
//
//        File file = new File(userDir,username+".obj");
//        if(file.exists()){
//            response.sendRedirect("/have_user.html");
//            return;
//        }
//        int age = Integer.parseInt(ageStr);//年龄要转换为整数
//        User user = new User(username,password,nickname,age);
//        try (
//                FileOutputStream fos = new FileOutputStream(file);
//                ObjectOutputStream oos = new ObjectOutputStream(fos);
//        ){
//            oos.writeObject(user);
//            response.sendRedirect("/reg_success.html");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @RequestMapping("/loginUser")//V17新增内容 添加相应注释
    public void login(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理登录!!!!");
        //1获取登录信息
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //必要验证
        if(username==null||username.isEmpty()||password==null||password.isEmpty()){
            response.sendRedirect("/login_info_error.html");
            return;
        }

        //根据该登录用户的名字去定位users下该用户的注册信息
        File file = new File(userDir,username+".obj");
        //判断该文件是否存在,不存在则说明该用户没有注册过
        if(file.exists()){
            //将该用户曾经的注册信息读取出来用于比较密码
            try (
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ){
                User user = (User)ois.readObject();//读取注册信息
                //比较本次登录的密码是否与注册时该用户输入的密码一致
                if(user.getPassword().equals(password)){
                    //密码一致则登录成功
                    response.sendRedirect("/login_success.html");
                    return;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        //如果程序可以执行到这里,则说明要么是用户名没输入对,要么是密码没有输入对.都属于登录失败
        response.sendRedirect("/login_fail.html");

    }

    @RequestMapping("/updatePWD")//V17新增内容 添加相应注释
    public void updatePWD(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理修改密码功能!!!!!");
    }
}
