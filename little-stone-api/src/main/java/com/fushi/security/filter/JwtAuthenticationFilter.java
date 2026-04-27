package com.fushi.security.filter;

import com.fushi.security.handler.JwtAuthenticationEntryPoint;
import com.fushi.security.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        //排除公开接口，不需要jwt验证
        String path = request.getRequestURI();
        if(path.startsWith("/api/auth")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/favicon.ico")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")) {
            jwtAuthenticationEntryPoint.commence(request, response, new BadCredentialsException("无效的token：缺少Authorization头或格式错误！"));
            return;
        }

        String token = header.substring(7).trim();

        String username = null;
        try {
            username = jwtTokenUtil.getUsernameFromToken(token);
        } catch (ExpiredJwtException e) {
            logger.error("token过期");
            jwtAuthenticationEntryPoint.commence(request, response, new CredentialsExpiredException("token过期"));
            return;
        } catch (Exception e) {
            logger.error("token验证失效：" + e.getMessage());
            jwtAuthenticationEntryPoint.commence(request, response, new BadCredentialsException("无效的token"));
            return;
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if(jwtTokenUtil.validateToken(token, userDetails)) {
                    // 4、构建认证信息（包含用户权限），设置到Security上下文
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                jwtAuthenticationEntryPoint.commence(request, response, new BadCredentialsException(e.getMessage()));
                return;
            }
        }

        //5、继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
