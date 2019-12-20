package com.wxy.sfp.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @Author wxy
 * @Date 19-7-17 上午10:56
 * @Description TODO
 **/
@Data
@Builder
public class FileInfo {
    private String name;
    private String path;
    private Boolean isDirectory;
    private Long length;
    private Long lastModified;
}
