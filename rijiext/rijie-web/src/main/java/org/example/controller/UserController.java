package org.example.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.pojo.LoginInfo;
import org.example.pojo.Result;
import org.example.pojo.User;
import org.example.service.UserService;
import org.example.utils.AuthUtils;
import org.example.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制层
 * 处理用户注册、登录、信息管理等请求，适配三类角色的操作需求
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @jakarta.annotation.Resource
    private HttpServletRequest request;

    // ------------------------------ 通用接口（所有角色） ------------------------------
    /**
     * 用户登录（修改后：生成JWT令牌，封装LoginInfo返回）
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（含LoginInfo：用户信息+token）
     */
    @PostMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userService.login(username, password);
            if (user != null) {
                // 1. 构建JWT的Claims（存储用户核心信息，避免敏感字段）
                Map<String, Object> claims = new HashMap<>();
                claims.put("id", user.getId());
                claims.put("username", user.getUsername());
                // 可选：也可放入realName，方便后续解析
                claims.put("realName", user.getRealName());

                // 2. 生成JWT令牌
                String token = JwtUtils.generateToken(claims);

                // 3. 封装登录结果到LoginInfo（仅返回必要信息，无敏感字段）
                LoginInfo loginInfo = new LoginInfo(
                        user.getId(),
                        user.getUsername(),
                        user.getRealName(),
                        token,
                        user.getRoleType() // 关键：把User的roleType传给LoginInfo
                );

                // 4. 返回封装后的结果
                return Result.success(loginInfo);
            } else {
                return Result.fail("用户名或密码错误");
            }
        } catch (Exception e) {
            return Result.fail("登录异常：" + e.getMessage());
        }
    }

    /**
     * 查询当前登录用户信息（个人中心，从JWT获取userId）
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result getUserInfo() {
        try {
            Long id = AuthUtils.getCurrentUserId(request);
            User user = userService.getUserById(id);
            if (user != null) {
                // 构建包含信誉信息的响应
                var userInfo = new java.util.HashMap<String, Object>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("realName", user.getRealName());
                userInfo.put("phone", user.getPhone());
                userInfo.put("email", user.getEmail());
                userInfo.put("roleType", user.getRoleType());
                userInfo.put("status", user.getStatus());
                userInfo.put("reputationScore", user.getReputationScore());
                userInfo.put("totalRatings", user.getTotalRatings());

                return Result.success(userInfo);
            } else {
                return Result.fail("用户不存在");
            }
        } catch (Exception e) {
            return Result.fail("查询用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 修改个人信息（如手机号、邮箱、真实姓名）
     * @param user 用户实体（含ID和要修改的字段）
     * @return 操作结果
     */
    @PutMapping("/info/update")
    public Result updateUserInfo(@RequestBody User user) {
        try {
            // 覆盖userId为JWT中的用户ID，防止用户修改他人信息
            user.setId(AuthUtils.getCurrentUserId(request));
            boolean success = userService.updateUserInfo(user);
            if (success) {
                return Result.success("个人信息修改成功");
            } else {
                return Result.fail("个人信息修改失败");
            }
        }catch (RuntimeException e) {
            // 这里会自动捕获手机号/邮箱重复的异常，返回对应的提示信息
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("修改个人信息异常：" + e.getMessage());
        }
    }

    // ========== 新增：手机号唯一性实时校验 (前端失焦调用) ==========
    @GetMapping("/check/phone")
    public Result checkPhoneUnique(@RequestParam String phone) {
        try {
            boolean exists = userService.isPhoneExists(phone);
            if (exists) {
                return Result.fail("该手机号已被其他用户注册，请更换");
            } else {
                return Result.success("手机号可用");
            }
        } catch (Exception e) {
            return Result.fail("手机号校验异常");
        }
    }

    // ========== 新增：邮箱唯一性实时校验 (前端失焦调用) ==========
    @GetMapping("/check/email")
    public Result checkEmailUnique(@RequestParam String email) {
        try {
            boolean exists = userService.isEmailExists(email);
            if (exists) {
                return Result.fail("该邮箱已被其他用户注册，请更换");
            } else {
                return Result.success("邮箱可用");
            }
        } catch (Exception e) {
            return Result.fail("邮箱校验异常");
        }
    }
    // ------------------------------ 求职者/雇主接口 ------------------------------
    /**
     * 用户注册（求职者/雇主）
     * @param user 用户实体（含用户名、密码、角色类型）
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            // 限制注册角色：仅允许求职者（2）、雇主（1），禁止注册管理员（0）
            if (user.getRoleType() == 0) {
                return Result.fail("不允许注册管理员账号");
            }
            boolean success = userService.register(user);
            if (success) {
                return Result.success("注册成功");
            } else {
                return Result.fail("用户名已存在或参数错误");
            }
        } catch (Exception e) {
            return Result.fail("注册异常：" + e.getMessage());
        }
    }

    // ------------------------------ 管理员接口 ------------------------------
    /**
     * 管理员查询所有用户
     * @return 所有用户列表
     */
    /**
     * 管理员查询所有用户（带分页）
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页条数
     * @return 分页结果 {list: 用户列表, total: 总数}
     */
    @GetMapping("/admin/all")
    public Result getAllUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        try {
            PageInfo<User> userPageInfo = userService.getAllUsers(pageNum, pageSize);
            return Result.success(userPageInfo); // 返回分页数据（list + total）
        } catch (Exception e) {
            return Result.fail("查询所有用户失败：" + e.getMessage());
        }
    }

    /**
     * 管理员根据角色类型查询用户（如查询所有雇主）
     * @param roleType 角色类型：1-雇主，2-求职者
     * @return 对应角色的用户列表
     */
    /**
     * 管理员根据角色类型查询用户（带分页）
     * @param roleType 角色类型：0-管理员，1-雇主，2-求职者
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @GetMapping("/admin/role/{roleType}")
    public Result getUsersByRoleType(
            @PathVariable Integer roleType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        try {
            PageInfo<User> userPageInfo = userService.getUsersByRoleType(roleType, pageNum, pageSize);
            return Result.success(userPageInfo);
        } catch (Exception e) {
            return Result.fail("查询角色用户失败：" + e.getMessage());
        }
    }
    /**
     * 管理员修改用户状态（禁用/启用）
     * @param id 用户ID
     * @param status 目标状态：0-禁用，1-启用
     * @return 操作结果
     */
    @PutMapping("/admin/status/update")
    public Result updateUserStatus(@RequestParam Long id, @RequestParam Integer status) {
        try {
            boolean success = userService.updateUserStatus(id, status);
            if (success) {
                String statusMsg = status == 1 ? "启用" : "禁用";
                return Result.success("用户已" + statusMsg);
            } else {
                return Result.fail("修改用户状态失败");
            }
        } catch (Exception e) {
            return Result.fail("修改用户状态异常：" + e.getMessage());
        }
    }
}