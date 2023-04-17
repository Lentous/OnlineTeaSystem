package com.ycy.tea.core;

import com.ycy.tea.http.EmptyRequestException;
import com.ycy.tea.http.HttpServletRequest;
import com.ycy.tea.http.HttpServletResponse;

import java.io.IOException;
import java.net.Socket;

/**
 * 该线程的任务是与指定的客户端完成HTTP交互
 * HTTP协议要求一问一答
 * 对此,这里的处理大致分为三步
 * 1:解析请求
 * 2:处理请求
 * 3:发送响应
 */
public class ClientHandler implements Runnable{
    private Socket socket;
    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            //1解析请求
            HttpServletRequest request = new HttpServletRequest(socket);
            HttpServletResponse response = new HttpServletResponse(socket);

            //2处理请求
            DispatcherServlet.getInstance().service(request,response);

            //3发送响应
            response.response();


        } catch (IOException e) {
            e.printStackTrace();
        //V11新增内容,捕获空请求,但是不做任何处理,目的仅是为了忽略try中处理请求与发送响应操作
        } catch (EmptyRequestException e) {
        } finally {
            //遵循HTTP协议要求,一问一答后与客户端断开连接
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}
