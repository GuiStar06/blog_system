package com.guistar.controller;

import com.guistar.entity.utils.RestBean;
import com.guistar.service.AccountService;
import com.guistar.vo.AccountVO;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/account")
public class AccountController {

    @Resource
    AccountService accountService;

    @GetMapping("/{accountId}/profile")//展示主页
    public RestBean<AccountVO> getAccountProfile(@RequestParam @PathVariable Long accountId){
        if(accountId == null) return RestBean.illegalArgs("用户id不能为空");
        return RestBean.success(accountService.convertToAccountVO(accountService.findAccountById(accountId)));
    }
}
