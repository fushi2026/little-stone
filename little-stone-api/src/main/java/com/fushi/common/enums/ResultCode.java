package com.fushi.common.enums;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    USER_EXIST(1001, "用户已存在"),
    PARAM_ERROR(1002, "参数错误"),
    LOGIN_FAIL(1003, "用户名或密码错误"),
    TOKEN_INVALID(1004, "Token无效或已过期");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
