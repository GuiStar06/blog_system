package com.guistar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guistar.entity.Account;
import com.guistar.entity.utils.Const;
import com.guistar.mapper.AccountMapper;
import com.guistar.service.AccountService;
import com.guistar.utils.FlowUtils;
import com.guistar.vo.AccountVO;
import com.guistar.vo.ConfirmResetVO;
import com.guistar.vo.RegisterEmailVO;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {


    private static final ConcurrentHashMap<String,Object> locks = new ConcurrentHashMap<>();

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate template;

    @Resource
    PasswordEncoder encoder;

    @Resource
    FlowUtils flowUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account ac = findAccountByUsernameOrEmail(username);
        if(ac == null) throw new UsernameNotFoundException("用户名或密码错误");
        return User.withUsername(username)
                .password(ac.getPassword())
                .roles(ac.getRole())
                .build();
    }

    @Override
    public Account findAccountByUsernameOrEmail(String text) {
        return this.query()
                .eq("username",text).or().eq("email",text)
                .one();
    }

    @Override
    public String askVerifyCodeByEmail(String email, String type, String ip) {
        Object lock = locks.computeIfAbsent(ip,k -> new Object());
        synchronized (lock){
            if(!flowUtils.allowRequest(ip)){
                return "请求频繁，稍后再试";
            }
            int code = new Random().nextInt(900000) + 100000;
            Map<String,Object> data = Map.of("email",email,"type",type,"code",code);
            amqpTemplate.convertAndSend(Const.MAIL_QUEUE,data);
            template.opsForValue().set(Const.REGISTER_EMAIL_CODE + email,String.valueOf(code),3, TimeUnit.MINUTES);
        }
        return null;
    }

    @Override
    public String registerEmailAccount(RegisterEmailVO registerEmailVO) {
        return "";
    }

    @Override
    public String resetConfirm(ConfirmResetVO confirmResetVO) {
        return "";
    }

    @Override
    public String resetEmailAccount(RegisterEmailVO registerEmailVO) {
        return "";
    }

    @Override
    public Account findAccountById(Long id) {
        return null;
    }

    @Override
    public AccountVO convertToAccountVO(Account account) {
        return null;
    }
}
