package com.ycy.tea.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * V4新加内容:
 * 请求对象
 * 该类的每一个实例用于表示HTTP协议规定的浏览器发送给服务端的一个请求
 *
 * 这里我们要将之前在ClientHandler中解析请求得到的请求行信息和消息头信息都定义成属性放在
 * 这里,并提供get方法,这样一来将来想获取请求中某个信息时只需要调用对应的get方法即可
 *
 * 这个对象就是我们之前在SpringBoot中创建Controller后,在处理某个业务的方法里定义的第一个参数
 */
public class HttpServletRequest {
    private Socket socket;
    //请求行相关信息
    private String method;              //请求方式
    private String uri;                 //抽象路径
    private String protocol;            //协议版本

    /*
        V12新增内容
        例如:
        uri--->/regUser?username=fancq&password=123456&nickname=chuanqi&age=22
        新添加的三个属性最终应当保存的信息如下
        requestURI:/regUser
        queryString:username=fancq&password=123456&nickname=chuanqi&age=22
        parameters:
            key             value
          username          fancq
          password          123456
          nickname          chuanqi
          age               22
     */
    private String requestURI;          //uri中?左侧的请求部分
    private String queryString;         //uri中?右侧的参数部分
    private Map<String,String> parameters = new HashMap<>();//保存每一组参数

    //消息头相关信息
    private Map<String,String> headers = new HashMap<>(); //所有消息头


    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;
        //1.1解析请求行
        parseRequestLine();
        //1.2解析消息头
        parseHeaders();
        //1.3解析消息正文
        parseContent();
    }

    //解析请求行
    private void parseRequestLine() throws IOException, EmptyRequestException {
        String line = readLine();

        //V11自增内容:如果请求行是空行则对外抛出空请求异常
        if(line.isEmpty()){//如果请求行是空行,则说明本次为空请求
            throw new EmptyRequestException();
        }

        System.out.println("请求行:"+line);
        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];
        //V12新增,当通过请求行解析出uri后,就进一步解析它
        parseUri();
        protocol = data[2];
        System.out.println("method:"+method);
        System.out.println("uri:"+uri);
        System.out.println("protocol:"+protocol);
    }
    //V12新增内容:进一步解析uri
    private void parseUri(){
        /*
            uri有两种情况:
            1:不含有参数的
              例如: /index.html
              直接将uri的值赋值给requestURI即可.

            2:含有参数的
              例如:/regUser?username=fancq&password=123456&nickname=chuanqi&age=22
              将uri中"?"左侧的请求部分赋值给requestURI
              将uri中"?"右侧的参数部分赋值给queryString
              将参数部分首先按照"&"拆分出每一组参数，再将每一组参数按照"="拆分为参数名与参数值
              并将参数名作为key，参数值作为value存入到parameters中。
                requestURI:/regUser
                queryString:username=fancq&password=123456&nickname=chuanqi&age=22
                parameters:
                    key             value
                  username          fancq
                  password          123456
                  nickname          chuanqi
                  age               22




              如果表单某个输入框没有输入信息，那么存入parameters时对应的值应当保存为空字符串


              当页面form表单中指定了action="/regUser",但是该表单中所有输入框均没有指定名字时:
              /regUser?
         */
        /*
            三种情况:
            /index.html===>split("\\?")====>["/index.html"]

            /regUser?username=fancq&password=&nickname=chuanqi&age=22
            ["/regUser", "username=fancq&password=&nickname=chuanqi&age=22"]

            /regUser?
            ["/regUser"]
         */
        String[] data = uri.split("\\?");
        requestURI = data[0];
        if(data.length>1){//如果拆分出第二项,说明"?"右面有参数
            queryString = data[1];//先将"?"后面的参数部分赋值给queryString
            //进一步拆分每一组参数
            //V15改动:拆分参数改为调用方法
            parseParameters(queryString);

        }

        System.out.println("requestURI:"+requestURI);
        System.out.println("queryString:"+queryString);
        System.out.println("parameters:"+parameters);
    }

    /*
        V15新增内容
        由于表单提交方式不同(GET或POST),表单提交的数据所在位置也不同,要么在抽象路径中"?"右侧
        要么在消息正文中,但是无论在哪里,格式都是统一的,因此拆分参数并存入到parameters中的逻辑
        是相同的,所以我们将原进一步解析uri的方法parseUri中拆分参数的逻辑提取到这个方法中重用
     */
    private void parseParameters(String line){
        //V16新增内容:在解析参数前,现将参数转码(将中文部分正确转码)
        try {
            line = URLDecoder.decode(line,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] paraArray = line.split("&");
        for(String para : paraArray){
            String[] arr = para.split("=",2);
            parameters.put(arr[0], arr[1]);
        }
    }

    //解析消息头
    private void parseHeaders() throws IOException {
        while(true) {
            String line = readLine();
            if(line.isEmpty()){
                break;
            }
            System.out.println("消息头:" + line);
            String[] data = line.split(":\\s");
            headers.put(data[0],data[1]);
        }
        System.out.println("headers:"+headers);
    }

    //解析消息正文
    private void parseContent() throws IOException {
        /*
            V15新增内容:
            如果消息头中包含:Content-Length则说明浏览器告知本次请求含有正文,并使用这个头
            来表示正文的长度(字节数)
            因此我们首先要判断是否含有这个头来确定是否要解析正文
            之后按照Content-Length指定的长度从输入流中读取正文数据.然后在根据消息头
            Content-Type来确定正文类型从而正确解析对应的正文内容.
            这里我们仅处理一种类型:application/x-www-form-urlencoded
            如果Content-Type是上述类型,则浏览器表达正文是一个字符串,内容是form表单
            提交的数据,该字符串格式就是原GET请求提交表单时在抽象路径中"?"右侧的内容
            对此我们将该字符串读取后按照之前进一步解析uri中解析参数的操作拆分,然后存入
            到属性parameters中.这样后续操作里Controller中还可以通过getParameter这个
            方法获取到表单提交的数据了.
         */
        //1判断本次请求是否包含Content-Length这个头
        if(headers.containsKey("Content-Length")){
            //获取正文长度
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            System.out.println("==============正文长度:"+contentLength);
            //根据正文长度将正文读取出来
            byte[] data = new byte[contentLength];
            InputStream in = socket.getInputStream();
            in.read(data);//将正文数据全部读取存入data数组备用
            //根据消息头Content-Type来解码正文数据
            String contentType = headers.get("Content-Type");
            if("application/x-www-form-urlencoded".equals(contentType)){
                //表单不含附件的数据
                //将正文直接转换为字符串
                String line = new String(data, StandardCharsets.ISO_8859_1);
                System.out.println("===========正文内容:"+line);
                parseParameters(line);
            }
//            else if(){//后期可扩展其它正文类型并解析
//
//            }

        }

    }



    /*
        V4:为属性提供get方法
        这里不需要提供set方法,因为这些属性保存的是浏览器发送过来的请求内容,不需要改动
     */
    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }
    /*
        V4:
        获取消息头的操是根据给定的消息头的名字返回对应的value
        在解析消息头时,我们将消息头的名字作为key,值作为value保存到headers中了,因此这里
        的操作相当于就是根据key获取value.
     */
    public String getHeader(String name) {
        return headers.get(name);
    }

    /**
     * V4新增内容,这个是从ClientHandler中剪切过来的
     * @return
     */
    private String readLine() throws IOException {
        /*
            socket对象没有发生改变,那么无论调用这个对象多少次获取流的操作时,获取的始终是
            同一条流(输入,输出都一样)
         */
        InputStream in = socket.getInputStream();
        int d;
        StringBuilder builder = new StringBuilder();//保存拼接后的一行字符串
        char cur='a',pre='a';//cur表示本次读取的字符  pre表示上次读取的字符
        while((d = in.read())!=-1){
            cur = (char)d;//将本次读取的字符赋值给cur
            if(pre==13 && cur==10){//判断上次是否读取的回车符,本次是否为换行符
                break;//若连续读取了回车+换行就停止读取(一行结束了)
            }
            builder.append(cur);
            pre=cur;//在读取下一个字符前,将本次读取的字符记作"上次读取的字符"
        }
        return builder.toString().trim();
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }
}
