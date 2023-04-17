package com.ycy.tea.core;

import com.ycy.tea.annotations.Controller;
import com.ycy.tea.annotations.RequestMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * V18新增内容
 * 在SpringMVC框架中使用这个类维护请求与对应的Controller处理业务方法的对应关系
 */
public class HandlerMapping {
    /*
        保存请求路径与对应的处理方法
        key:请求路径
        value:处理该请求的某Controller中的某个方法
     */
    private static Map<String, Method> mapping = new HashMap<>();
    /*
        开发中不建议在静态块中直接堆代码,可读性差.
        可以将要做的事情封装为一个方法,用方法名概括代码的含义,然后在这里调用.
        如果是多件事,可以定义多个方法来处理,并在这里陆续调用.
     */
    static{
        initMapping();
    }

    /**
     * 扫描controller包中的所有Controller(该类被@Controller标注的类)
     * 中所有的处理方法(该方法被@RequestMapping标注)根据其对应的路径作为key,该方法对象
     * 作为value全部保存到mapping中.
     */
    private static void initMapping(){
        try {
            //类加载路径
            File baseDir = new File(
                    HandlerMapping.class.getClassLoader().getResource(".").toURI()
            );
            //通过类加载路径定位项目中的controller包
            File dir = new File(baseDir,"com/birdboot/controller");
            //1获取该目录下的所有.class文件
            File[] subs = dir.listFiles(f->f.getName().endsWith(".class"));
            for(File sub : subs){
                //根据class文件的文件名来确定类名
                String fileName = sub.getName();//UserController.class
                String className = fileName.substring(0,fileName.indexOf("."));//UserController
                //加载类对象
                Class cls = Class.forName("com.birdboot.controller."+className);
                //判断该类是否被注解@Controller标注
                if(cls.isAnnotationPresent(Controller.class)){
                    //扫描里面所有的方法，并查看有注解@RequestMapping的方法
                    Method[] methods = cls.getDeclaredMethods();
                    for(Method method : methods){
                        //判断该方法是否被注解@RequestMapping标注
                        if(method.isAnnotationPresent(RequestMapping.class)){
                            //通过@RequestMapping注解获取上面的参数(该方法处理的请求路径)
                            RequestMapping rm = method.getAnnotation(RequestMapping.class);
                            String value = rm.value();
                            //将该方法以及其对应的路径存入mapping中
                            mapping.put(value,method);
                        }
                    }
                }
            }
        } catch (URISyntaxException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据请求路径获取对应的处理方法
     * @param path
     * @return
     */
    public static Method getMethod(String path){
        return mapping.get(path);
    }


    public static void main(String[] args) {
        Method method = mapping.get("/loginUser");
        System.out.println(method);
    }

}
