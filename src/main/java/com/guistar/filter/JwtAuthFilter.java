package com.guistar.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.guistar.entity.utils.Const;
import com.guistar.entity.utils.RestBean;
import com.guistar.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {


    @Resource
    JwtUtils utils;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(RequestFilter.isWhiteResource(request.getRequestURI())){
            filterChain.doFilter(request,response);
            return;
        }
        String authorization = request.getHeader("Authorization");
        DecodedJWT jwt = utils.resolveJwt(authorization);
        if(jwt == null){
            sendUnauthorizedMsg(response,"请先登录");
            return;
        }else{
        User user = (User) utils.toUser(jwt);
        if(user == null){
            sendUnauthorizedMsg(response,"无效用户信息");
            return;
        }
        UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.setAttribute(Const.USER_ID,utils.toId(jwt));
        }
        filterChain.doFilter(request,response);
    }

    private void sendUnauthorizedMsg(HttpServletResponse response,String message) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(RestBean.unauthorized(message).asJsonString());
    }
}
