package com.wxy.sfp.entity;

import lombok.Data;

/**
 * @Author wxy
 * @Date 19-7-17 上午10:56
 * @Description TODO
 **/
@Data
public class FileVo {
    private String name;
    private String path;
    private Boolean isDirectory;
}
