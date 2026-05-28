package org.example.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.mapper.UserMapper;
import org.example.pojo.User;
import org.example.service.UserService;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

/**
 * 用户业务层实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectUserByUsername(username);
        if (user != null && encoder.matches(password, user.getPassword())) {
            user.setPassword(null);
            return user;
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null || user.getRoleType() == null) {
            return false;
        }
        User existUser = userMapper.selectUserByUsername(user.getUsername());
        if (existUser != null) {
            return false;
        }
        user.setStatus(1);
        user.setPassword(encoder.encode(user.getPassword()));
        int rows = userMapper.insertUser(user);
        return rows > 0;
    }

    /**
     * 查询所有用户
     */
    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAllUsers();
    }

    /**
     * 根据角色类型查询用户
     */
    @Override
    public List<User> getUsersByRoleType(Integer roleType) {
        if (roleType == null || roleType < 0 || roleType > 2) {
            return null; // 角色类型不合法
        }
        return userMapper.selectUsersByRoleType(roleType);
    }

    /**
     * 修改用户状态
     */
    @Override
    public boolean updateUserStatus(Long id, Integer status) {
        if (id == null || id <= 0 || status == null || status < 0 || status > 1) {
            return false; // 入参不合法
        }
        int rows = userMapper.updateUserStatus(id, status);
        return rows > 0;
    }

    /**
     * 根据ID查询用户
     */
    @Override
    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return userMapper.selectUserById(id);
    }

    /**
     * 修改用户个人信息
     */
    @Override
    public boolean updateUserInfo(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        // 核心：校验手机号是否已被其他用户占用
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            // 查数据库：是否存在 「手机号=user.getPhone」且「id≠当前用户id」的用户
            User existingUser = userMapper.selectByPhoneAndNotId(user.getPhone(), user.getId());
            if (existingUser != null) {
                // 手机号已被其他用户占用，抛出异常提示
                throw new RuntimeException("该手机号已被注册，请更换其他手机号");
            }
        }
        // ========== 新增：邮箱唯一性校验 (和手机号逻辑完全一致，复制粘贴即可) ==========
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            // 查询：是否存在【邮箱=当前填写】且【用户ID≠当前登录用户ID】的用户
            User emailExistUser = userMapper.selectByEmailAndNotId(user.getEmail(), user.getId());
            if (emailExistUser != null) {
                // 抛出邮箱重复的异常提示
                throw new RuntimeException("该邮箱已被其他用户注册，请更换邮箱");
            }
        }
        int rows = userMapper.updateUserInfo(user);
        return rows > 0;
    }

    // UserServiceImpl.java 新增这2个方法 (直接加在原有方法下面即可)
    @Override
    public boolean isPhoneExists(String phone) {
        // 复用 selectByPhoneAndNotId，传id=-1L → 等价于查询所有用户是否有该手机号
        User user = userMapper.selectByPhoneAndNotId(phone.trim(), -1L);
        // 有值返回true（手机号已存在），无值返回false（手机号可用）
        return user != null;
    }

    @Override
    public boolean isEmailExists(String email) {
        // 复用 selectByEmailAndNotId，传userId=-1L → 等价于查询所有用户是否有该邮箱
        User user = userMapper.selectByEmailAndNotId(email.trim(), -1L);
        // 有值返回true（邮箱已存在），无值返回false（邮箱可用）
        return user != null;
    }

    @Override
    public PageInfo<User> getAllUsers(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); // 开启分页
        List<User> userList = userMapper.selectAllUsers(); // 原有查询逻辑
        return new PageInfo<>(userList);
    }

    @Override
    public PageInfo<User> getUsersByRoleType(Integer roleType, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectUsersByRoleType(roleType);
        return new PageInfo<>(userList);
    }
}