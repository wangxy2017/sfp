package com.wxy.sfp.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author wxy
 * @Date 19-7-17 下午3:29
 * @Description TODO
 **/
@Data
public class ApiResponse {
    private String currentPath;
    private List<FileVo> list;

    public ApiResponse(String currentPath, List<FileVo> list) {
        this.currentPath = currentPath;
        this.list = list;
    }
}
