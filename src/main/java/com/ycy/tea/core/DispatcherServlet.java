package com.ycy.tea.core;

import com.ycy.tea.http.HttpServletRequest;
import com.ycy.tea.http.HttpServletResponse;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

/**
 * V8新增能容:
 * DispatcherServlet是由SpringMVC框架提供的一个Servlet类,用于与Tomcat整合使用的类.
 * 具体作用是接收处理请求的工作
 *
 * 这里我们先将原ClientHandler中处理请求的所有工作移动到这个类的service方法上
 * 注:
 * Tomcat中处理请求的类都需要继承HttpServlet,而实现类都需要重写方法:service,用于处理请求
 * SpringMVC提供的DispatcherServlet实际上也这样做了.我们这里不需要真的去继承了,但是格式上
 * 与其保持一致.
 */
public class DispatcherServlet {

    private static DispatcherServlet instance = new DispatcherServlet();
    private static File baseDir;
    private static File staticDir;
    static{
        try {
            baseDir = new File(
                    DispatcherServlet.class.getClassLoader().getResource(".").toURI()
            );
            staticDir = new File(baseDir,"static");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private DispatcherServlet(){}

    public void service(HttpServletRequest request, HttpServletResponse response){
        String path = request.getRequestURI();
        System.out.println("抽象路径:"+path);

        //判断本次请求是否为请求业务
        //V18:将原来扫描Controller的工作移动到HandlerMapping中,这里仅反射调用即可
        Method method = HandlerMapping.getMethod(path);//根据请求路径获取对应的业务方法
        if(method!=null){//如果为null说明本次请求不是处理业务,所以要添加分支判断
            /*
                通过方法对象获取该方法所属的类
                例如:
                Class cls = method.getDeclaringClass();
                如果当前method表达的是UserController中的reg()方法.
                那么上述代码获取的类对象cls表示的就是reg方法所属的类UserController
             */
            try {
                Class cls = method.getDeclaringClass();
                Object obj = cls.newInstance();
                method.invoke(obj,request,response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            File file = new File(staticDir, path);
            if (file.isFile()) {
                response.setContentFile(file);
                response.addHeader("Server", "BirdServer");
            } else {
                file = new File(staticDir, "404.html");
                response.setStatusCode(404);
                response.setStatusReason("NotFound");
                response.setContentFile(file);
                response.addHeader("Server", "BirdServer");
            }
        }

    }

    public static DispatcherServlet getInstance(){
        return instance;
    }
}
