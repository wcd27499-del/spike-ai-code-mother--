package com.spike.spikeaicodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("com.spike.spikeaicodemother.genresult.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class SpikeAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpikeAiCodeMotherApplication.class, args);
    }

}
