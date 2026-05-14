package com.spike.spikeaicodemother.genresult.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import com.spike.spikeaicodemother.genresult.entity.App;
import com.spike.spikeaicodemother.genresult.entity.User;
import com.spike.spikeaicodemother.genresult.mapper.AppMapper;
import com.spike.spikeaicodemother.genresult.service.AppService;
import com.spike.spikeaicodemother.genresult.service.UserService;
import com.spike.spikeaicodemother.model.dto.app.AppQueryRequest;
import com.spike.spikeaicodemother.model.vo.AppVO;
import com.spike.spikeaicodemother.model.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author spike
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{
    @Resource
    private UserService userService;

    @Override
    public AppVO getAppVO(App app) {
        //校验
        if (app == null) {
            return null;
        }
        //关联用户信息
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        //
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (appList == null) {
            return new ArrayList<>();
        }
        Set<Long> userIds = appList.stream().map(app -> app.getUserId()).collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.
                listByIds(userIds).stream().
                collect(Collectors.toMap(User::getId, userService::getUserVO));

        return appList.stream().map(app-> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());

    }


}
