package com.guistar.controller;

import com.guistar.entity.utils.RestBean;
import com.guistar.service.AccountService;
import com.guistar.vo.RegisterEmailVO;
import com.guistar.vo.ResetEmailVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Resource
    AccountService accountService;

    @PostMapping("/register")
    public RestBean<Void> registerEmailAccount(@RequestBody @Valid RegisterEmailVO registerEmailVO){
        return messageHandle(() -> accountService.registerEmailAccount(registerEmailVO));
    }

    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp = "(register|reset)") String type,
                                        @RequestParam HttpServletRequest request){
        String message = accountService.askVerifyCodeByEmail(email,type,request.getRequestURI());
        if(message == null) return RestBean.success();
        return RestBean.forbidden(message);
    }

    @PostMapping("/reset")
    public RestBean<Void> resetEmailAccount(@RequestBody @Valid ResetEmailVO resetEmailVO){
        return messageHandle(() -> accountService.resetEmailAccount(resetEmailVO));
    }

    private RestBean<Void> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message == null) return RestBean.success();
        return RestBean.failure(400,message);
    }
}
