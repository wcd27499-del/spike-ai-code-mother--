package com.spike.spikeaicodemother.ai;



import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.spike.spikeaicodemother.core.tool.FileWriteTool;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import com.spike.spikeaicodemother.genresult.service.ChatHistoryService;
import com.spike.spikeaicodemother.model.enums.CodeGenTypeEnum;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 创建工厂来初始ai服务
 */
@Configuration
@Slf4j
public class AiCodeGenerateServiceFactory {

    @Resource
    private ChatModel chatModel;
    @Resource
    private StreamingChatModel openAiStreamingChatModel;
    @Resource
    private StreamingChatModel reasoningStreamingChatModel;
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;
    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * Ai服务缓存实例
     * 缓存策略：
     * -最大缓存1000个实例
     * -写入后30分钟过期
     * -访问后10分钟过期
     */
    private final Cache<String,AiCodeGenerateService> serviceCache= Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI服务实例被移除，appId:{},原因:{}",key,cause);
            }).build();

    /**
     * 根据appId获取服务（带缓冲）这个方法是为了兼容历史逻辑
     * @param appId
     * @return
     */
    public AiCodeGenerateService getAiCodeGenerateService(Long appId) {
        return getAiCodeGenerateService(appId,CodeGenTypeEnum.HTML);
    }

    /**
     * 根据appId获取服务（带缓存）
     * @param appId
     * @return
     */
    public AiCodeGenerateService getAiCodeGenerateService(Long appId,CodeGenTypeEnum codeGenType) {
        String cacheKey=buildCacheKey(appId,codeGenType);
        return serviceCache.get(cacheKey, Key->createAiCodeGenerateService(appId,codeGenType));

    }


    public AiCodeGenerateService createAiCodeGenerateService(Long appId, CodeGenTypeEnum codeGenType) {
    log.info("为appId：{}创建新的AI服务实例",appId);
    //根据appId构建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        //从数据库加载历史对话到记忆中
        chatHistoryService.loadChatHistoryToMemory(appId,chatMemory,10);
        //根据代码生成类型选择不同的模型配置
        return switch (codeGenType){
            case VUE_PROJECT -> AiServices.builder(AiCodeGenerateService.class)
                    .streamingChatModel(reasoningStreamingChatModel)
                    .chatMemoryProvider(memoryId-> chatMemory)
                    .tools(new FileWriteTool())
                    .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage
                            .from(toolExecutionRequest,"Error: there is no tool called"+toolExecutionRequest.name()))
                    .build();
            //HTML和多文件生成使用默认模型
            case HTML,MULTI_FILE ->AiServices.builder(AiCodeGenerateService.class)
                    .chatModel(chatModel)
                    .streamingChatModel(openAiStreamingChatModel)
                    .chatMemory(chatMemory)
                    .build();
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,"不支持代码生成类型："+codeGenType.getValue());

        };
    }

    /**
     * 构建缓存键
     * @param appId
     * @param codeGenType
     * @return
     */
    private String buildCacheKey(Long appId, CodeGenTypeEnum codeGenType) {
        return appId + "-" + codeGenType.getValue();
    }
    /**
     * 默认提供一个 Bean
     */
    @Bean
    public AiCodeGenerateService aiCodeGeneratorService() {
        return getAiCodeGenerateService(0L);
    }

}
