package com.guistar.entity.utils;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.slf4j.MDC;

import java.util.Optional;

public record RestBean<T>(long reqId,int code, T data, String message) {
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(requestId(),200,data,"请求成功!");
    }
    public static <T> RestBean<T> success(){
        return success(null);
    }
    public static <T> RestBean<T> failure(int code,String message){
        return new RestBean<>(requestId(),code,null,message);
    }
    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
    public static <T> RestBean<T> forbidden(String message){
        return failure(403,message);
    }
    public static <T> RestBean<T> unauthorized(String message){
        return failure(401,message);
    }
    private static long requestId(){
        String id = Optional.ofNullable(MDC.get("reqId")).toString();
        return Long.parseLong(id);
    }
}
