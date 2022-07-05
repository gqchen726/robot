package com.tanyuge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * tsapp主启动类
 *
 * @author chunlin.qi@hand-china.com
 * @version 1.0
 * @description
 * @date 2022/1/4
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class RobotMain {

    public static void main(String[] args) {
        SpringApplication.run(RobotMain.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
