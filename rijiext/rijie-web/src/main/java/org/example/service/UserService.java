package org.example.service;

import com.github.pagehelper.PageInfo;
import org.example.pojo.User;

import java.util.List;

/**
 * 用户业务层接口
 * 定义用户相关的核心业务操作
 */
public interface UserService {

    /**
     * 用户登录（校验用户名和密码）
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回用户对象，失败返回null
     */
    User login(String username, String password);

    /**
     * 用户注册（求职者/雇主）
     * @param user 用户实体对象
     * @return 操作结果：true=成功，false=失败
     */
    boolean register(User user);

    /**
     * 查询所有用户（管理员端）
     * @return 所有用户列表
     */
    List<User> getAllUsers();

    /**
     * 根据角色类型查询用户（如查询所有雇主）
     * @param roleType 角色类型：0-管理员，1-雇主，2-求职者
     * @return 对应角色的用户列表
     */
    List<User> getUsersByRoleType(Integer roleType);

    /**
     * 管理员修改用户状态（禁用/启用）
     * @param id 用户ID
     * @param status 目标状态：0-禁用，1-启用
     * @return 操作结果：true=成功，false=失败
     */
    boolean updateUserStatus(Long id, Integer status);

    /**
     * 根据用户ID查询用户信息（通用）
     * @param id 用户ID
     * @return 用户对象
     */
    User getUserById(Long id);

    /**
     * 修改用户个人信息（如手机号、邮箱）
     * @param user 用户实体（含ID和要修改的字段）
     * @return 操作结果：true=成功，false=失败
     */
    boolean updateUserInfo(User user);

    // 新增：校验手机号是否存在（被任何人注册）
    boolean isPhoneExists(String phone);
    // 新增：校验邮箱是否存在（被任何人注册）
    boolean isEmailExists(String email);

    // 分页查询所有用户
    PageInfo<User> getAllUsers(Integer pageNum, Integer pageSize);

    // 分页按角色查询用户
    PageInfo<User> getUsersByRoleType(Integer roleType, Integer pageNum, Integer pageSize);

}