package com.spike.spikeaicodemother.genresult.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.spike.spikeaicodemother.genresult.entity.App;
import com.spike.spikeaicodemother.genresult.entity.User;
import com.spike.spikeaicodemother.model.dto.app.AppQueryRequest;
import com.spike.spikeaicodemother.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author spike
 */
public interface AppService extends IService<App> {


    /**
     * 获取App脱敏类
     * @param app
     * @return
     */
    AppVO getAppVO(App app);

    /**
     * 构造分页查询querywrapper
     * @param appQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 将App类封装成AppVO脱敏类
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 通过参数生成流式代码
     * @param appId
     * @param message
     * @param loginUser
     * @return
     */
    Flux<String> chatToGenCode(Long appId, String message,User loginUser);

    /**
     * 部署应用
     * @param appId
     * @param loginUser
     * @return
     */
    String deployApp(Long appId,User loginUser);



}
