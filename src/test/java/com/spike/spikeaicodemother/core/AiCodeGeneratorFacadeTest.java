package com.spike.spikeaicodemother.core;

import com.spike.spikeaicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeGeneratorFacadeTest {
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void test() {
        File result = aiCodeGeneratorFacade.generateAndSaveCode("博客网站不超过50行", CodeGenTypeEnum.MULTI_FILE,1L);
        Assertions.assertNotNull(result);
    }
    @Test
    void generateAndSaveCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream("登录网页不超过60行", CodeGenTypeEnum.MULTI_FILE,1L);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }


}