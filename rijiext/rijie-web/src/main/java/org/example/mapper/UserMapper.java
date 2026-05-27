package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.User;

import java.util.List;

/**
 * 用户数据操作Mapper接口（对应sys_user表）
 * 提供用户的CRUD核心操作
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户（登录、注册查重用）
     * @param username 登录用户名
     * @return 用户对象（无数据则返回null）
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User selectUserByUsername(String username);

    /**
     * 新增用户（求职者/雇主注册）
     * @param user 用户实体对象
     * @return 受影响的行数（1=新增成功，0=失败）
     */
    @Insert("INSERT INTO sys_user (" +
            "username, password, real_name, phone, email, role_type, status, create_time, update_time" +
            ") VALUES (" +
            "#{username}, #{password}, #{realName}, #{phone}, #{email}, #{roleType}, #{status}, NOW(), NOW()" +
            ")")
    int insertUser(User user);

    /**
     * 查询所有用户（管理员端使用）
     * @return 所有用户列表
     */
    @Select("SELECT * FROM sys_user ORDER BY create_time DESC")
    List<User> selectAllUsers();

    /**
     * 根据角色类型查询用户（如查询所有雇主/求职者）
     * @param roleType 角色类型：0-管理员，1-雇主，2-求职者
     * @return 对应角色的用户列表
     */
    @Select("SELECT * FROM sys_user WHERE role_type = #{roleType} ORDER BY create_time DESC")
    List<User> selectUsersByRoleType(Integer roleType);

    /**
     * 根据用户ID修改账号状态（管理员禁用/启用用户）
     * @param id 用户ID
     * @param status 目标状态：0-禁用，1-启用
     * @return 受影响的行数（1=更新成功，0=失败）
     */
    @Update("UPDATE sys_user SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateUserStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 根据用户ID查询用户（通用）
     * @param id 用户ID
     * @return 用户对象
     */
    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User selectUserById(Long id);

    /**
     * 修改用户个人信息（如手机号、邮箱、真实姓名）
     * @param user 用户实体（含ID和要修改的字段）
     * @return 受影响的行数（1=更新成功，0=失败）
     */
    @Update("UPDATE sys_user SET " +
            "real_name = #{realName}, phone = #{phone}, email = #{email}, update_time = NOW() " +
            "WHERE id = #{id}")
    int updateUserInfo(User user);

    // ========== 新增：手机号查询方法
    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND id != #{id}")
    User selectByPhoneAndNotId(@Param("phone") String phone, @Param("id") Long id);

    // ========== 新增：邮箱查询方法 (复制粘贴，只改字段名phone→email) ==========
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND id != #{userId}")
    User selectByEmailAndNotId(@Param("email") String email, @Param("id") Long id);

}
