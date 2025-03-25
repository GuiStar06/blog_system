package com.guistar.utils;

import com.guistar.entity.utils.Const;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class FlowUtils {
    @Resource
    StringRedisTemplate template;

    ConcurrentHashMap<String,TokenBucketsUtils> buckets = new ConcurrentHashMap<>();

    private static Logger logger = LoggerFactory.getLogger(FlowUtils.class);

    public boolean allowRequest(String ip){
        TokenBucketsUtils bucket = buckets.computeIfAbsent(ip,k -> {
            logger.info("请求地址:{},token容量:{},token生成速率:{}",ip, Const.TOKENS_CAPACITY
            ,Const.TOKEN_GENERATE_RATE);
            return new TokenBucketsUtils(Const.TOKENS_CAPACITY,Const.TOKEN_GENERATE_RATE);
        });
        return bucket.tryRequest();
    }
    @Scheduled(fixedRate = 1000)
    private void refillAll(){
        buckets.values().forEach(TokenBucketsUtils::refillTokens);
    }
}