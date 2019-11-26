package com.wxy.sfp.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @Author wxy
 * @Date 19-7-17 上午10:56
 * @Description TODO 文件视图类
 **/
@Data
@Builder
public class FileInfoVo {
    private String name;
    private String path;
    private Boolean isDirectory;
    private Long length;
    private Long lastModified;
}
