package com.fushi.handler;

import com.fushi.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.BindException;

@Slf4j
public class GlobalExceptionHandler {
    //1、处理请求方法不支持（如GET调用POST接口）
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持：{}", e.getMessage());
        return ApiResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(), "请求方法不支持");
    }

    //2、处理404（接口不存在）
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handle404(NoHandlerFoundException e) {
        log.error("请求接口不存在：{}", e.getMessage());
        return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "请求接口不存在");
    }

    //3、处理参数校验异常
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMethodArgNotValidException(Exception e) {
        log.error("参数校验异常：{}", e.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "参数校验异常");
    }

    //4、处理业务异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleException(Exception e) {
        log.error("业务异常：{}", e.getMessage());
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "业务异常");
    }

}
