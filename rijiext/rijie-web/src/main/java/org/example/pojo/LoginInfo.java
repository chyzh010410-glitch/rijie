package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录结果封装类
 */
@Data // 自动生成getter、setter、toString等方法
@NoArgsConstructor // 无参构造器
@AllArgsConstructor // 全参构造器
public class LoginInfo {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * JWT令牌
     */
    private String token;

    // 新增：补充角色类型字段（和User类的roleType对应）
    private Integer roleType;
}
