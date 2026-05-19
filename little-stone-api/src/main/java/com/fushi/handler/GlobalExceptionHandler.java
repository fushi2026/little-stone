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
// @RestControllerAdvice  // 注释掉以解决Swagger报错问题，保留代码便于后续启用
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持：{}", e.getMessage());
        return ApiResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(), "请求方法不支持");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handle404(NoHandlerFoundException e) {
        log.error("请求接口不存在：{}", e.getMessage());
        return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "请求接口不存在");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMethodArgNotValidException(Exception e) {
        log.error("参数校验异常：{}", e.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "参数校验异常");
    }
}