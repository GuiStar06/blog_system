package com.guistar.filter;

import com.guistar.entity.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(isWhiteResource(request.getRequestURI())){
            filterChain.doFilter(request,response);
            return;
        }
        long startTime = System.currentTimeMillis();
        String reqId = requestId();
        MDC.put("reqId",reqId);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        try{
            filterChain.doFilter(request,response);
        }catch (Exception e){
            logger.error("请求异常:{}",e.getMessage());
            throw e;
        }finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();
            String reqUrl = requestWrapper.getRequestURI();
            String method = requestWrapper.getMethod();
            logger.info("请求:{},方法:{},时间:{},请求结果:{}",reqUrl,method,duration,status);
            responseWrapper.copyBodyToResponse();
            MDC.remove("reqId");
        }
    }

    public static boolean isWhiteResource(String reqUrl){
        int dotIndex = reqUrl.lastIndexOf(".");
        if(dotIndex != -1){
            String ext = reqUrl.substring(dotIndex);
            return Const.WHITE_RESOURCE.contains(ext);
        }
        return Const.WHITE_RESOURCE.stream().anyMatch(reqUrl::startsWith);
    }


    private static String requestId(){
        return UUID.randomUUID().toString();
    }
}
