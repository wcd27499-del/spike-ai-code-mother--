package com.spike.spikeaicodemother.genresult.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.spike.spikeaicodemother.genresult.entity.ChatHistory;
import com.spike.spikeaicodemother.genresult.entity.User;
import com.spike.spikeaicodemother.model.dto.chatHistory.ChatHistoryQueryRequest;
import dev.langchain4j.memory.ChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author spike
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加消息（用户消息或者ai回复消息）到对话历史中
     * @param appId
     * @param message
     * @param messageType
     * @param userId
     * @return
     */
    boolean addMessage(Long appId,String message,String messageType,Long userId);

    /**
     * 通过应用id删除对话历史内容
     * @param appId
     * @return
     */
    boolean removeById(Long appId);

    /**
     * 构造查询条件
     * @param chatHistoryQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    /**
     * 分页查询
     * @param appId
     * @param pageSize
     * @param lastCreateTime
     * @param loginUser
     * @return
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);

    int loadChatHistoryToMemory(Long appId, ChatMemory chatMemory,int maxCount);
}
