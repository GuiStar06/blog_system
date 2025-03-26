package com.guistar.controller.exception;

import com.guistar.entity.utils.RestBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestBean<?> handleValidationException(MethodArgumentNotValidException e){
        String errMsg = e.getBindingResult().getFieldErrors()
                .stream().map(fieldError -> fieldError.getField() + ":" + fieldError.getDefaultMessage())
                .collect(Collectors.joining(";"));
        return RestBean.failure(400,errMsg);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public RestBean<?> handleAccessDeniedException(AccessDeniedException e){
        log.error("权限不足:{}",e.getMessage());
        return RestBean.failure(403,"权限不足");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public RestBean<?> handleIllegalArgsException(IllegalArgumentException e){
        log.error("参数异常:{}",e.getMessage());
        return RestBean.illegalArgs(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public RestBean<?> handleException(Exception e) {
        log.error("系统异常:{}",e.getMessage());
        return RestBean.failure(500,"系统错误，请联系管理员");
    }


}
