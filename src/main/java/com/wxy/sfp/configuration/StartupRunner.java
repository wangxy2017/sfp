package com.wxy.sfp.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @Author wxy
 * @Date 19-7-17 下午4:36
 * @Description TODO 程序启动执行：初始化仓库
 **/

@Component
@Slf4j
public class StartupRunner implements CommandLineRunner {

    @Value("${repository:repository}")
    private String repository;

    @Override
    public void run(String... args) {
        File file = new File(repository);
        if (!file.exists()) {
            boolean mkdir = file.mkdirs();
            log.info("初始化文件夹：{}", mkdir);
        }
    }
}
