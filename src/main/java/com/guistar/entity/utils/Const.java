package com.guistar.entity.utils;

import java.util.List;

public class Const {
    public static final String DEFAULT_ROLE = "user";
    public static final String DEFAULT_AVATAR  = "";
    public static final String JWT_BLACK_LIST = "jwt:black:list";
    public static final List<String> WHITE_RESOURCE = List.of("/static/**","/public/**","/api/auth/**","*.js","*.css","*.png","*.jpg","*.ico","*.svg");
    public static final String MAIL_QUEUE = "mail";
    public static final int TOKENS_CAPACITY = 10;
    public static final double TOKEN_GENERATE_RATE = (double) 5 /60;
    public static final String USER_ID = "userId";
    public static final String REGISTER_EMAIL_CODE = "register:email:code";
    public static final String RESET_EMAIL_CODE = "reset:email:code";
}
