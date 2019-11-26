package com.wxy.sfp.controller;

import com.wxy.sfp.entity.ApiResponse;
import com.wxy.sfp.entity.FileInfoVo;
import com.wxy.sfp.util.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author wxy
 * @Date 19-7-17 上午10:55
 * @Description TODO 文件操作控制器
 **/
@RestController
@Slf4j
public class FileController {
    @Value("${repository:repository}")
    private String repository;

    /**
     * 修改目录或文件
     *
     * @param path
     * @param oldName
     * @param newName
     * @return
     */
    @GetMapping("/rename")
    public ApiResponse rename(@RequestParam String path, @RequestParam String oldName, @RequestParam String newName) {
        File folder = new File(path);
        File file = new File(folder.getPath() + File.separator + oldName);
        File file1 = new File(folder.getPath() + File.separator + newName);
        if (folder.exists() && file.exists() && !file1.exists()) {
            boolean b = file.renameTo(file1);
            if (b) {
                log.info("修改目录或文件：[{}]renameTo[{}]，时间：{},IP：{}", file.getPath(), file1.getPath(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), IPUtils.getRemoteIp());
                return new ApiResponse(1, "success", "修改成功");
            }
        }
        return new ApiResponse(-1, "error", "修改失败");
    }

    /**
     * 删除目录或文件
     *
     * @param path
     * @return
     */
    @GetMapping("/delete")
    public ApiResponse delete(@RequestParam String path) {
        if (StringUtils.isNotBlank(path) && !path.equals(repository)) {
            File file = new File(path);
            if (file.exists() && deleteFile(file)) {
                log.info("删除文件：{}，时间：{},IP：{}", path, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), IPUtils.getRemoteIp());
                return new ApiResponse(1, "success", "删除成功");
            }
        }
        return new ApiResponse(-1, "error", "非法操作");
    }

    /**
     * 读取文件列表
     *
     * @param path
     * @return
     */
    @GetMapping("/list")
    public ApiResponse list(@RequestParam(required = false) String path, @RequestParam(required = false) String name) {
        String filepath = StringUtils.isBlank(path) ? repository : path;
        File file = new File(filepath);
        if (file.exists() && file.isDirectory() && file.getPath().startsWith(repository)) {
            List<FileInfoVo> list = readList(file);
            // 搜索(忽略大小写)
            if (StringUtils.isNotBlank(name)) {
                list = list.stream().filter(f -> f.getName().toLowerCase().contains(name)).collect(Collectors.toList());
            }
            Map<String, Object> data = new HashMap<>();
            data.put("path", file.getPath());
            data.put("list", list);
            return new ApiResponse(1, "success", data);
        }
        return new ApiResponse(-1, "error", "路径错误");
    }

    /**
     * 新建文件夹
     *
     * @param path
     * @return
     */
    @GetMapping("/new")
    public ApiResponse newFolder(@RequestParam String path, @RequestParam String name) {
        File parent = new File(path);
        if (parent.exists() && StringUtils.isNotBlank(name)) {
            File file = new File(parent.getPath() + File.separator + name);
            if (!file.exists()) {
                file.mkdir();
                log.info("新建文件夹：{}，时间：{},IP：{}", file.getPath(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), IPUtils.getRemoteIp());
                return new ApiResponse(1, "success", "新建成功");
            }
        }
        return new ApiResponse(-1, "error", "新建失败");
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
        if (!file.isEmpty()) {
            String filename = file.getOriginalFilename();
            File filepath = new File(StringUtils.isBlank(path) ? repository : path);
            if (filepath.exists()) {
                File dest = new File(filepath.getPath() + File.separator + filename);
                InputStream is = null;
                BufferedInputStream bis = null;
                OutputStream os = null;
                try {
                    is = file.getInputStream();
                    bis = new BufferedInputStream(is);
                    os = new FileOutputStream(dest);
                    byte[] buffer = new byte[1024 * 1024 * 10];
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    log.info("上传文件：{}，时间：{},IP：{}", dest.getPath(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), IPUtils.getRemoteIp());
                    return new ApiResponse(1, "success", "上传成功");
                } catch (IOException e) {
                    log.error("上传失败:{}，IP地址：{}", e.getMessage(), IPUtils.getRemoteIp());
                    return new ApiResponse(1, "error", "上传失败");
                } finally {
                    IOUtils.closeQuietly(is);
                    IOUtils.closeQuietly(bis);
                    IOUtils.closeQuietly(os);
                }
            } else {
                throw new IllegalArgumentException("参数不合法:" + path);
            }
        } else {
            return new ApiResponse(-1, "error", "上传失败，请选择文件");
        }
    }

    /**
     * 下载文件
     *
     * @param response
     * @param path
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, @RequestParam String path) throws UnsupportedEncodingException {
        File file = new File(path);
        if (file.exists() && file.isFile() && file.getPath().startsWith(repository)) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(file.getName(), "UTF-8"));// 设置文件名

            byte[] buffer = new byte[1024 * 1024 * 10];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                log.info("下载文件：{}，时间：{},IP：{}", file.getPath(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), IPUtils.getRemoteIp());
            } catch (IOException e) {
                log.error("下载异常：{}，IP地址：{}", e.getMessage(), IPUtils.getRemoteIp());
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
            List<FileInfoVo> list = readList(file.getPath().equals(repository) ? file : file.getParentFile());
            Map<String, Object> data = new HashMap<>();
            data.put("path", file.getPath().equals(repository) ? file.getPath() : file.getParent());
            data.put("list", list);
            return new ApiResponse(1, "success", data);
        }
        return new ApiResponse(-1, "error", null);
    }

    private List<FileInfoVo> readList(File file) {
        File[] files = file.listFiles();
        List<FileInfoVo> list = new ArrayList<>();
        for (File value : files) {
            FileInfoVo info = FileInfoVo.builder()
                    .name(value.getName())
                    .path(value.getPath())
                    .isDirectory(value.isDirectory())
                    .length(value.length())
                    .lastModified(value.lastModified())
                    .build();
            list.add(info);
        }
        // 排序
        list.sort(Comparator.comparing(FileInfoVo::getName));
        return list;
    }

    private boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
        }
        return file.delete();
    }
}
