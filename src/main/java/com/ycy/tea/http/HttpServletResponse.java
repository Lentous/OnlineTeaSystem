package com.ycy.tea.http;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * V7新增内容
 * 响应对象
 * 该类的每一个实例用于表示HTTP协议规定的服务端给客户端发送的响应内容
 *
 * 每个响应由三部分构成:
 * 状态行,响应头,响应正文
 */
public class HttpServletResponse {
    //V10新增内容:MimetypesFileTypeMap定义为静态的,全局唯一,重复利用
    private static MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();

    private Socket socket;
    //状态行相关信息
    private int statusCode = 200;               //状态代码
    private String statusReason = "OK";         //状态描述
    //响应头相关信息
    //V9新加内容:添加一个Map类型的属性用于保存所有待发送的响应头
    private Map<String,String> headers = new HashMap<>();


    //响应正文相关信息
    private File contentFile;                   //正文对应的文件

    public HttpServletResponse(Socket socket){
        this.socket = socket;
    }

    public void response() throws IOException {
        //1发送状态行
        sendStatusLine();
        //2发送响应头
        sendHeaders();
        //3发送响应正文
        sendContent();
    }

    //发送状态行
    private void sendStatusLine() throws IOException {
        println("HTTP/1.1"+" "+statusCode+" "+statusReason);//这里使用属性
    }
    //发送响应头
    private void sendHeaders() throws IOException {
//        println("Content-Type: text/html");
//        println("Content-Length: "+contentFile.length());
        /*
            V9新加内容:
            通过遍历headers这个Map,将所有待发送的响应头发送给浏览器
            headers
                key             value
            Content-Type        text/html
            Content-Length      3455
            Server              BirdServer
            ...                 ...
         */
        Set<Map.Entry<String,String>> entrySet = headers.entrySet();
        for(Map.Entry<String,String> e :entrySet){
            String key = e.getKey();//key就是响应头的名字
            String value = e.getValue();//value就是该响应头对应的值
            println(key+": "+value);
        }

        println("");
    }
    //发送响应正文
    private void sendContent() throws IOException {
        //V13新增内容:添加判定,如果存在正文文件才发送,避免空指针
        if(contentFile!=null) {
            OutputStream out = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(contentFile);
            byte[] data = new byte[1024 * 10];
            int len;
            while ((len = fis.read(data)) != -1) {
                out.write(data, 0, len);
            }
        }
    }



    /**
     * V7:从ClientHandler中移动过来
     * 向客户端发送一行字符串
     */
    private void println(String line) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(line.getBytes(StandardCharsets.ISO_8859_1));
        out.write(13);//单独发送回车符
        out.write(10);//单独发送换行符
    }

    //为属性添加get,set方法
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public File getContentFile() {
        return contentFile;
    }

    //V10:将添加响应头Content-Type和Content-Length操作移动到设置正文方法中
    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;
        /*
            V10:MimetypesFileTypeMap每次实例化都要读取类加载路径下META-INF里的文件
            mime.types,这个文件有1000多行,读取文件就是读取磁盘,性能低下.因此这个对象
            初始化一次后,重复利用即可.没必要每次处理请求都初始化一遍.
            改动:将MimetypesFileTypeMap定义为静态属性即可
         */
//        MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();
        String contentType = mftm.getContentType(contentFile);
        addHeader("Content-Type",contentType);
        addHeader("Content-Length",contentFile.length()+"");
    }

    public void addHeader(String name,String value){
        headers.put(name,value);
    }

    /*
        V13新增内容
        实现重定向功能
        重定向的关键点:
        1:状态代码为302
        2:要包含响应头Location指定希望浏览器重新访问的位置
        3:重定向的响应不需要正文
     */
    public void sendRedirect(String location){
        setStatusCode(302);
        setStatusReason("Moved Temporarily");
        addHeader("Location",location);
    }

}




