package com.spike.spikeaicodemother.ai;



import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.spike.spikeaicodemother.genresult.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
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
    private StreamingChatModel streamingChatModel;
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
    private final Cache<Long,AiCodeGenerateService> serviceCache= Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI服务实例被移除，appId:{},原因:{}",key,cause);
            }).build();

    /**
     * 根据appId获取服务（带缓存）
     * @param appId
     * @return
     */
    public AiCodeGenerateService getAiCodeGenerateService(Long appId) {
        return serviceCache.get(appId,this::createAiCodeGenerateService);

    }


    public AiCodeGenerateService createAiCodeGenerateService(Long appId) {
    log.info("为appId：{}创建新的AI服务实例",appId);
    //根据appId构建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        //从数据库加载历史对话到记忆中
        chatHistoryService.loadChatHistoryToMemory(appId,chatMemory,10);
        return AiServices.builder(AiCodeGenerateService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }
    /**
     * 默认提供一个 Bean
     */
    @Bean
    public AiCodeGenerateService aiCodeGeneratorService() {
        return getAiCodeGenerateService(0L);
    }

}
