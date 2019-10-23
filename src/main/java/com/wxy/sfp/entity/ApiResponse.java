package com.wxy.sfp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @Author wxy
 * @Date 19-7-17 下午3:29
 * @Description TODO
 **/
@Data
@AllArgsConstructor
@Builder
public class ApiResponse {
    private Integer code;
    private String msg;
    private Object data;
}
