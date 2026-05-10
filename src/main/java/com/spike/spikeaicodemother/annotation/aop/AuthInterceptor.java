package com.spike.spikeaicodemother.annotation.aop;

import com.spike.spikeaicodemother.annotation.AuthCheck;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import com.spike.spikeaicodemother.genresult.entity.User;
import com.spike.spikeaicodemother.genresult.service.UserService;
import com.spike.spikeaicodemother.model.enums.UserRoleEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInterceptor {
    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        //获取用户信息
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        //没有设置管理员权限，放行
        if (mustRoleEnum == null) {
            joinPoint.proceed();
        }
        //以下为：必须有权限才能通过
        //获取当前用户具有的权限
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        //没有权限拒绝
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //没有管理员权限，拒绝
        if(UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //权限通过，放行
        return joinPoint.proceed();

    }
}
