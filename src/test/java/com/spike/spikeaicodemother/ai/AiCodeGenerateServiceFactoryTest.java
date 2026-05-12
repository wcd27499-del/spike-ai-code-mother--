package com.spike.spikeaicodemother.ai;

import com.spike.spikeaicodemother.ai.model.HtmlCodeResult;
import com.spike.spikeaicodemother.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeGenerateServiceFactoryTest {
    @Resource
    private AiCodeGenerateService aiCodeGenerateService;

    @Test
    void aiCodeGenerateHtmlTest() {
        HtmlCodeResult result = aiCodeGenerateService.generateHtmlCode("生成一个博客网页，不超过20行");
        Assertions.assertNotNull(result);

    }
    @Test
    void aiCodeGenerateMultiHtmlTest() {
        MultiFileCodeResult result = aiCodeGenerateService.generateMultiFileCode("生成一个博客网页，不超过50行");
        Assertions.assertNotNull(result);
    }

}