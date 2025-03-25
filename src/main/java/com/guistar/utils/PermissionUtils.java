package com.guistar.utils;

import com.guistar.entity.Account;
import com.guistar.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class PermissionUtils {

    @Resource
    AccountService accountService;


    public boolean isAdmin(Long currentAccountId){
        if(currentAccountId == null) throw new IllegalArgumentException("当前用户id不能为空");
        Account ac = accountService.findAccountById(currentAccountId);
        if(ac == null) throw new RuntimeException("用户不存在");
        String role = ac.getRole();
        return "admin".equalsIgnoreCase(role);
    }

    public boolean isAdminOrAuthor(Long currentAccountId,Long authorId){
        return isAdmin(currentAccountId) || currentAccountId.equals(authorId);
    }
}
