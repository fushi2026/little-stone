package com.fushi.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fushi.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        try {
            logger.error("认证失败：{}, 异常类型: {}", authException.getMessage(), authException.getClass(), authException);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(ApiResponse.error(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage())));
            writer.flush();

            response.flushBuffer();
        } catch (Exception e) {
            logger.error("响应认证失败信息时出错！", e);
            throw new ServletException("处理认证失败响应出错！", e);
        }
    }
}
