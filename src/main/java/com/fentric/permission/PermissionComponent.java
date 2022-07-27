package com.fentric.permission;

import com.fentric.pojo.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("fentric")
public class PermissionComponent {
    //自定授权处理
    public boolean hasAuthority(String authority){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Set<String> permissions = loginUser.getPermissions();
        return permissions.contains(authority);
    }
}
