package com.ycy.tea.http;

/**
 * V11新增内容
 * 空请求异常,这是一个自定义异常.
 * 当浏览器发送空请求时(连接服务端后,没有发送请求内容,而只发送了一个回车+换行),响应对象
 * HttpServletRequest会抛出该异常.
 */
public class EmptyRequestException extends Exception{
    public EmptyRequestException() {
    }

    public EmptyRequestException(String message) {
        super(message);
    }

    public EmptyRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyRequestException(Throwable cause) {
        super(cause);
    }

    public EmptyRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}









