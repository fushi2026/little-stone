package com.fushi.util;

import com.fushi.security.model.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static LoginUser getLoginUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth != null && auth.getPrincipal() instanceof LoginUser){
                return (LoginUser) auth.getPrincipal();
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Long getUserId() {
        try {
            return  getLoginUser().getUserId();
        }
        catch (Exception e) {
            return 0L;
        }
    }

}
