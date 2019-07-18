package com.wxy.sfp.entity;

import lombok.Data;

/**
 * @Author wxy
 * @Date 19-7-17 下午3:29
 * @Description TODO
 **/
@Data
public class ApiResponse {
    private Integer code;
    private String msg;
    private Object data;

    public ApiResponse(Integer code,String msg,Object data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
