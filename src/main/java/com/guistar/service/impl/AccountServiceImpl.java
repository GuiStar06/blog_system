package com.guistar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guistar.entity.Account;
import com.guistar.entity.utils.Const;
import com.guistar.mapper.AccountMapper;
import com.guistar.service.AccountService;
import com.guistar.utils.FlowUtils;
import com.guistar.vo.AccountVO;
import com.guistar.dto.RegisterEmailDTO;
import com.guistar.dto.ResetEmailDTO;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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
            String code = generateCode(6);
            Map<String,Object> data = Map.of("email",email,"type",type,"code",code);
            amqpTemplate.convertAndSend(Const.MAIL_QUEUE,data);
            if(type.equals("register")) {
                template.opsForValue().set(Const.REGISTER_EMAIL_CODE + email,code,3, TimeUnit.MINUTES);
            }
            template.opsForValue().set(Const.RESET_EMAIL_CODE + email,code,3, TimeUnit.MINUTES);
        }
        return null;
    }

    @Override
    public String registerEmailAccount(RegisterEmailDTO registerEmailDTO) {
        String email = registerEmailDTO.getEmail();
        String code = template.opsForValue().get(Const.REGISTER_EMAIL_CODE + email);
        if(code == null || registerEmailDTO.getCode() == null) return "请先获取验证码";
        if(isExistEmail(email)) return "邮箱已被注册";
        if(!code.equals(registerEmailDTO.getCode())) return "验证码错误，请检查验证码";
        String password = encoder.encode(registerEmailDTO.getPassword());
        Account ac =
                new Account(null, registerEmailDTO.getUsername(),password,email,Const.DEFAULT_ROLE, registerEmailDTO.getNickname(), Const.DEFAULT_AVATAR,new Date());
        if(!this.save(ac)){
            return "系统错误，请联系管理员";
        }
        deleteVerifyCode(Const.REGISTER_EMAIL_CODE + email);
        return null;
    }

    @Override
    public String resetEmailAccount(ResetEmailDTO resetEmailDTO) {
        String email = resetEmailDTO.getEmail();
        String code = template.opsForValue().get(Const.RESET_EMAIL_CODE + email);
        if(isExistEmail(email)) return "邮箱已被注册";
        if(code == null || resetEmailDTO.getCode() == null) return "请先获取验证码";
        if(!code.equals(resetEmailDTO.getCode())) return "验证码错误，请检查验证码";
        String password = encoder.encode(resetEmailDTO.getPassword());
        boolean update = this.update().eq("email",email).set("password",password).update();
        if(update){
            deleteVerifyCode(code);
            return null;
        }
        return "内部错误，请联系管理员";
    }

    @Override
    public Account findAccountById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public AccountVO convertToAccountVO(Account account) {
        return account.asViewObj(AccountVO.class);
    }

    private boolean isExistEmail(String email){
        return this.query().eq("email",email).exists();
    }

    private void deleteVerifyCode(String key){
        template.delete(key);
    }
    public String generateCode(int length){
        StringBuilder code = new StringBuilder();
        for(int i = 0;i < length;i++){
            code.append(new Random().nextInt(10));
        }
        return code.toString();
    }
}
