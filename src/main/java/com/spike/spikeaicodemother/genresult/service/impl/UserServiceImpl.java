package com.spike.spikeaicodemother.genresult.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.spike.spikeaicodemother.constant.UserConstant;
import com.spike.spikeaicodemother.exception.BusinessException;
import com.spike.spikeaicodemother.exception.ErrorCode;
import com.spike.spikeaicodemother.genresult.entity.User;
import com.spike.spikeaicodemother.genresult.mapper.UserMapper;
import com.spike.spikeaicodemother.genresult.service.UserService;
import com.spike.spikeaicodemother.model.dto.user.UserQueryRequest;
import com.spike.spikeaicodemother.model.enums.UserRoleEnum;
import com.spike.spikeaicodemother.model.vo.LoginUserVO;
import com.spike.spikeaicodemother.model.vo.UserVO;
import com.spike.spikeaicodemother.utils.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 服务层实现。
 *
 * @author spike
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

    @Override
    public long UserRegister(String userAccount, String userPassword, String checkPassword) {
        //校验
        if (StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名过短");
        }
        if(userPassword.length()<8 && checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"前后密码不一致");
        }
        //检查是否重复
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }
        //加密
        String encryptPassword = getEncryptPassword(userPassword);
        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("nothing");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean result = this.save(user);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据库异常，注册失败");
        }
        return user.getId();

    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if(user==null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user,loginUserVO);
        return loginUserVO;

    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
    //校验
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号错误");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
        }
        //加密
        String encryptPassword = getEncryptPassword(userPassword);
        //查询用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        //用户不存在
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在或者密码错误");
        }
        //记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);

        return this.getLoginUserVO(user);

    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        //先判断是否登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId()==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"未登录");
        }
//        //查询用户信息
//        Long userid = currentUser.getId();
//         currentUser= this.getById(userid);
//         if (currentUser==null){
//             throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//         }
         return currentUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(userObj==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"未登录");
        }
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;

    }

    @Override
    public UserVO getUserVO(User user) {
        if(user==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user,userVO);
        return userVO;

    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if(CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    public String getEncryptPassword(String password){
        //盐值混淆密码
        String SALT="spike";
        return DigestUtils.md5DigestAsHex((SALT+password).getBytes());
    }

    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest){
        if (userQueryRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");

        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id)
                .eq("userRole",userRole)
                .like("userAccount",userAccount)
                .like("userName",userName)
                .like("userProfile",userProfile)
                .orderBy(sortField,"ascend".equals(sortOrder));
        return queryWrapper;

    }
}
