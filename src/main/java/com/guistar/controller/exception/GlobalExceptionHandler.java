package com.guistar.controller.exception;

import com.guistar.entity.utils.RestBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public RestBean<?> handleBusinessException(BusinessException e){
        log.error("业务异常:{}",e.getMessage());
        return RestBean.failure(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestBean<?> handleValidationException(MethodArgumentNotValidException e){
        String errMsg = e.getBindingResult().getFieldErrors()
                .stream().map(fieldError -> fieldError.getField() + ":" + fieldError.getDefaultMessage())
                .collect(Collectors.joining(";"));
        return RestBean.failure(400,errMsg);
    }

    @ExceptionHandler(Exception.class)
    public RestBean<?> handleException(Exception e) {
        log.error("系统异常:{}",e.getMessage());
        return RestBean.failure(500,"系统错误，请联系管理员");
    }

    @ExceptionHandler(IllegalAccessException.class)
    public RestBean<?> handleIllegalArgsException(IllegalAccessException e){
        log.error("参数异常:{}",e.getMessage());
        return RestBean.illegalArgs(e.getMessage());
    }
}
