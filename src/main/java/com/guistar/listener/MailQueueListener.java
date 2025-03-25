package com.guistar.listener;

import com.guistar.entity.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = Const.MAIL_QUEUE)
public class MailQueueListener {
    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler
    public void sendMailMsg(Map<String,Object> data){
        String email = data.get("email").toString();
        String code = data.get("code").toString();
        SimpleMailMessage msg = switch (data.get("type").toString())
        {
            case "register" -> createMsg("欢迎注册我们的网站！","您的验证码为："+code + "有效时间三分钟",email,username);
            case "reset" -> createMsg("重置密码","您的验证码为："+code + "有效时间三分钟",email,username);
            default -> null;
        };
        if(msg == null) return;
        sender.send(msg);
    }
    private SimpleMailMessage createMsg(String title,String content,String email,String from){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setSubject(title);
        msg.setText(content);
        msg.setFrom(from);
        msg.setTo(email);
        return msg;
    }
}
