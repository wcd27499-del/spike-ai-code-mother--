package com.spike.spikeaicodemother.genresult.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.spike.spikeaicodemother.genresult.entity.User;
import com.spike.spikeaicodemother.model.dto.user.UserLoginRequest;
import com.spike.spikeaicodemother.model.dto.user.UserQueryRequest;
import com.spike.spikeaicodemother.model.vo.LoginUserVO;
import com.spike.spikeaicodemother.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author spike
 */
public interface UserService extends IService<User> {


    /**
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return 用户id
     */
    long UserRegister(String userAccount, String userPassword,String checkPassword);

    /**
     * 获取已脱敏的已登录用户信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);
    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);
    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏后的用户信息
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户列表
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 密码加密
     * @param password
     * @return
     */
    String getEncryptPassword(String password);

}
