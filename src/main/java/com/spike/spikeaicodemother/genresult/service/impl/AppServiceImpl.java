package com.spike.spikeaicodemother.genresult.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.spike.spikeaicodemother.constant.AppConstant;
import com.spike.spikeaicodemother.core.AiCodeGeneratorFacade;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import com.spike.spikeaicodemother.genresult.entity.App;
import com.spike.spikeaicodemother.genresult.entity.User;
import com.spike.spikeaicodemother.genresult.mapper.AppMapper;
import com.spike.spikeaicodemother.genresult.service.AppService;
import com.spike.spikeaicodemother.genresult.service.UserService;
import com.spike.spikeaicodemother.model.dto.app.AppQueryRequest;
import com.spike.spikeaicodemother.model.enums.CodeGenTypeEnum;
import com.spike.spikeaicodemother.model.vo.AppVO;
import com.spike.spikeaicodemother.model.vo.UserVO;
import com.spike.spikeaicodemother.utils.ThrowUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
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
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

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

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限访问该应用，仅本人可以生成代码
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }
        // 4. 获取应用的代码生成类型
        String codeGenTypeStr = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
        }
        // 5. 调用 AI 生成代码
        return aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
    }

    @Override
    public String deployApp(Long appId, User loginUser) {

        //参数验证
        ThrowUtils.throwIf(appId==null,ErrorCode.PARAMS_ERROR,"应用id不能为空");
        ThrowUtils.throwIf(loginUser==null,ErrorCode.NOT_LOGIN_ERROR,"用户未登录");

        //查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app==null,ErrorCode.NOT_FOUND_ERROR,"用户不存在");
        //验证用户是否有权限部署应用，经本人可以部署
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权限部署该应用");
        }
        //检查是否有deployKey
        String deployKey = app.getDeployKey();
        //没有就生成
        if (StrUtil.isBlank(deployKey)) {
            deployKey= RandomUtil.randomString(6);
        }
        //获取代码生成类型，构建目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName=codeGenType+"_"+appId;
        String sourceDirPath= AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator+sourceDirName;

        //检查源目录是否存在
        File file = new File(sourceDirPath);
        if (!file.exists() || !file.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"应用代码不存在，请先生成代码");
        }
        //复制文件到部署文件
        String deployDirPath=AppConstant.CODE_DEPLOY_ROOT_DIR+File.separator+deployKey;
        try {
            FileUtil.copyContent(file,new File(deployDirPath),true);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,e.getMessage());
        }
        //更新应用的deployKey和部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        ThrowUtils.throwIf(!updateResult,ErrorCode.OPERATION_ERROR,"更新应用部署信息失败");

        //返回可访问的url
        return String.format("%s/%s",AppConstant.CODE_DEPLOY_HOST,deployKey);

    }


}
