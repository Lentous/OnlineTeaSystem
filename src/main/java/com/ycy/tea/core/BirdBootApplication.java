package com.ycy.tea.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 项目的主启动类
 */
public class BirdBootApplication {
    private ServerSocket serverSocket;
    //V19新增内容
    private ExecutorService pool;//线程池

    public BirdBootApplication(){
        try {
            System.out.println("正在启动服务端...");
            serverSocket = new ServerSocket(8088);
            //V19新增内容
            pool = Executors.newFixedThreadPool(50);
            System.out.println("服务端启动完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            //V5新增:由于一问一答已经实现,可以重复接收客户端连接了
            while(true) {
                System.out.println("等待客户端连接...");
                Socket socket = serverSocket.accept();
                System.out.println("一个客户端连接了!");
                //启动一个线程来处理与该客户端的交互
                ClientHandler handler = new ClientHandler(socket);
                //V19改动内容,将原有的创建线程改为将任务交给线程池处理
                pool.execute(handler);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        BirdBootApplication application = new BirdBootApplication();
        application.start();
    }

}
