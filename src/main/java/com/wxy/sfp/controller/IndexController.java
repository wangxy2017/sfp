package com.wxy.sfp.controller;

import com.wxy.sfp.entity.FileVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author wxy
 * @Date 19-7-17 上午10:55
 * @Description TODO
 **/
@Controller
@Slf4j
public class IndexController {
    @Value("${basedir}")
    private String basedir;

    @GetMapping
    public String index() {
        return "index";
    }

    /**
     * 读取文件列表
     *
     * @param path
     * @return
     */
    @GetMapping("/list")
    @ResponseBody
    public List<FileVo> list(@RequestParam(required = false) String path) {
        File file = new File(path == null ? basedir : path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            List<FileVo> list = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                FileVo f = new FileVo();
                f.setName(files[i].getName());
                f.setPath(files[i].getPath());
                f.setIsDirectory(files[i].isDirectory());
                list.add(f);
            }
            log.info("读取文件列表：path = {}", file.getPath());
            return list;
        }
        return null;
    }

    /**
     * 下载
     *
     * @param response
     * @param path
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, @RequestParam String path) {
        File file = new File(path);
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + file.getName());// 设置文件名

            byte[] buffer = new byte[1024 * 10];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                ServletOutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                log.info("下载文件：file = {}", file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                log.error("下载异常：{}", e.getLocalizedMessage());
            } finally {
                IOUtils.closeQuietly(fis);
                IOUtils.closeQuietly(bis);
            }
        }
    }
}
