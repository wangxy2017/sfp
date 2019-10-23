package com.wxy.sfp.controller;

import com.wxy.sfp.entity.ApiResponse;
import com.wxy.sfp.entity.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Author wxy
 * @Date 19-7-17 上午10:55
 * @Description TODO
 **/
@RestController
@Slf4j
public class FileController {
    @Value("${repository}")
    private String repository;

    /**
     * 读取文件列表
     *
     * @param path
     * @return
     */
    @GetMapping("/list")
    public ApiResponse list(@RequestParam(required = false) String path) {
        File file = new File(path == null ? repository : path);
        if (file.isDirectory() && file.getPath().startsWith(repository)) {
            List<FileInfo> list = readList(file);
            log.info("读取文件列表：path = {}", file.getPath());
            Map<String, Object> data = new HashMap<>();
            data.put("path", file.getPath());
            data.put("list", list);
            return new ApiResponse(1, "success", data);
        }
        return new ApiResponse(-1, "error", null);
    }

    /**
     * 上传文件
     *
     * @param file
     * @param path
     * @return
     */
    @PostMapping("/upload")
    public ApiResponse upload(@RequestParam("file") MultipartFile file, @RequestParam String path) {
        String filename = file.getOriginalFilename();
        File filePath = new File(StringUtils.isBlank(path) ? repository : path);
        if (filePath.exists()) {
            try {
                File dest = new File(filePath.getPath() + File.separator + filename);
                if (!dest.exists()) {
                    dest.createNewFile();
                }
                file.transferTo(dest);
                return new ApiResponse(1, "success", "上传成功");
            } catch (IOException e) {
                log.error("上传失败:{}", e.getMessage());
                return new ApiResponse(1, "error", "上传失败");
            }
        } else {
            throw new IllegalArgumentException("参数不合法:" + path);
        }
    }

    /**
     * 下载
     *
     * @param response
     * @param path
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, @RequestParam String path) throws UnsupportedEncodingException {
        File file = new File(path);
        if (file.isFile() && file.getPath().startsWith(repository)) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(file.getName(), "UTF-8"));// 设置文件名

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
                log.error("下载异常：{}", e.getMessage());
            } finally {
                IOUtils.closeQuietly(fis);
                IOUtils.closeQuietly(bis);
            }
        } else {
            throw new RuntimeException("文件读取异常:" + path);
        }
    }

    /**
     * 返回上一层
     *
     * @param path
     * @return
     */
    @GetMapping("/back")
    public ApiResponse back(@RequestParam String path) {
        File file = new File(path);
        if (file.exists() && file.getPath().startsWith(repository)) {
            List<FileInfo> list = readList(file.getPath().equals(repository) ? file : file.getParentFile());
            Map<String, Object> data = new HashMap<>();
            data.put("path", file.getPath().equals(repository) ? file.getPath() : file.getParent());
            data.put("list", list);
            return new ApiResponse(1, "success", data);
        }
        return new ApiResponse(-1, "error", null);
    }

    private List<FileInfo> readList(File file) {
        File[] files = file.listFiles();
        List<FileInfo> list = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            FileInfo f = new FileInfo();
            f.setName(files[i].getName());
            f.setPath(files[i].getPath());
            f.setIsDirectory(files[i].isDirectory());
            f.setLength(files[i].length());
            f.setLastModified(files[i].lastModified());
            list.add(f);
        }
        // 排序
        list.sort(Comparator.comparing(FileInfo::getName));
        return list;
    }
}
