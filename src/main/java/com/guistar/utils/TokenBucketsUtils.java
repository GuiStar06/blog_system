package com.guistar.utils;

public class TokenBucketsUtils {
    private final int capacity;
    private final double rate;
    private int tokens;
    private long lastRefillTime;
    public TokenBucketsUtils(int capacity,double rate){
        this.capacity = capacity;
        this.rate = rate;
        this.tokens = capacity;
        this.lastRefillTime = System.currentTimeMillis();
    }
    public synchronized void refillTokens(){
        long now = System.currentTimeMillis();
        long time = (now - lastRefillTime) / 1000;
        int addTokens = (int) (time * rate);
        if(addTokens > 0){
            tokens = Math.min(capacity,tokens + addTokens);
            lastRefillTime = now;
        }
    }

    public synchronized boolean tryRequest(){
        refillTokens();
        if(tokens > 0){
            tokens--;
            return true;
        }
        return false;
    }
}
