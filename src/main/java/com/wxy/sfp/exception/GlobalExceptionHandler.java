package com.wxy.sfp.exception;

import com.wxy.sfp.entity.ApiResponse;
import com.wxy.sfp.util.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author wxy
 * @Date 19-7-1 下午1:55
 * @Description TODO 全局异常处理
 **/
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseBody
    public ApiResponse exceptionHandle(Exception e) {
        log.error("系统错误：{}，IP地址：{}", e.getMessage(), IPUtils.getRemoteIp());
        return new ApiResponse(500, "系统错误", null);
    }
}
