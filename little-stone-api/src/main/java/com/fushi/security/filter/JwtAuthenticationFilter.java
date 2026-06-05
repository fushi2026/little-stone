package com.fushi.security.filter;

import com.fushi.security.model.LoginUser;
import com.fushi.security.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

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
            throw new BadCredentialsException("无效的token：缺少Authorization头或格式错误！");
        }

        String token = header.substring(7).trim();

        String username = null;
        try {
            username = jwtTokenUtil.getUsernameFromToken(token);
        } catch (ExpiredJwtException e) {
            logger.error("token过期");
            throw new CredentialsExpiredException("token已过期");
        } catch (Exception e) {
            logger.error("token验证失效：" + e.getMessage());
            throw new BadCredentialsException("无效的token");
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(username);
                if(jwtTokenUtil.validateToken(token, loginUser)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                throw new BadCredentialsException("无效的token");
            }
        }

        filterChain.doFilter(request, response);
    }
}
