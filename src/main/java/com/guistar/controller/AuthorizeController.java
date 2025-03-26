package com.guistar.controller;

import com.guistar.entity.utils.RestBean;
import com.guistar.service.AccountService;
import com.guistar.dto.RegisterEmailDTO;
import com.guistar.dto.ResetEmailDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Resource
    AccountService accountService;

    @PostMapping("/register")
    public RestBean<Void> registerEmailAccount(@RequestBody @Valid RegisterEmailDTO registerEmailDTO){
        return messageHandle(() -> accountService.registerEmailAccount(registerEmailDTO));
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
    public RestBean<Void> resetEmailAccount(@RequestBody @Valid ResetEmailDTO resetEmailDTO){
        return messageHandle(() -> accountService.resetEmailAccount(resetEmailDTO));
    }

    private RestBean<Void> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message == null) return RestBean.success();
        return RestBean.failure(400,message);
    }
}
