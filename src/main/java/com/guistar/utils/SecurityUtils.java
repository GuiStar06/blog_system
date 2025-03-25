package com.guistar.utils;

import com.guistar.entity.Account;
import com.guistar.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    @Resource
    AccountService accountService;

    public Long getCurrentAccountId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) throw new RuntimeException("用户未登陆");
        User user = (User) authentication.getPrincipal();
        Account ac = accountService.findAccountByUsernameOrEmail(user.getUsername());
        return ac.getId();
    }
}
