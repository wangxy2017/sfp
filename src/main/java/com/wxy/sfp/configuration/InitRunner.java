package com.wxy.sfp.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @Author wxy
 * @Date 19-7-17 下午4:36
 * @Description TODO
 **/

@Component
@Slf4j
public class InitRunner implements CommandLineRunner {

    @Value("${file.repository}")
    private String repository;

    @Override
    public void run(String... args) {
        File file = new File(repository);
        if (!file.exists()) {
            boolean mkdir = file.mkdirs();
            log.info("创建文件仓库：{}", file.getAbsolutePath());
        }
    }
}
