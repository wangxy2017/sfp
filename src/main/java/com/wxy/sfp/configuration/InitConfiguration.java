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
public class InitConfiguration implements CommandLineRunner {

    @Value("${basedir}")
    private String basedir;

    @Override
    public void run(String... args) {
        File file = new File(basedir);
        if (!file.exists()) {
            boolean mkdir = file.mkdir();
            log.info("初始化文件夹：mkdir = {}", mkdir);
        }
    }
}
