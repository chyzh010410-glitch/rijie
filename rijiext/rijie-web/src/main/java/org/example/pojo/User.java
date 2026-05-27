package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统用户实体类（对应sys_user表）
 */
@Data // Lombok：自动生成getter、setter、toString、equals、hashCode等方法
public class User {
    /**
     * 用户唯一ID（自增）
     */
    private Long id;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 密码（测试用明文，生产环境需加密）
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色类型：0-管理员，1-雇主，2-求职者
     */
    private Integer roleType;

    /**
     * 账号状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // 补充以下字段
    /**
     * 技能标签（逗号分隔）
     */
    private String skillTags;

    /**
     * 常住地址
     */
    private String residentAddress;

    // 新增信誉分相关字段
    /**
     * 信誉分(0-5分)
     */
    private Double reputationScore=5.0;

    /**
     * 总评价数
     */
    private Integer totalRatings=0;

    /**
     * 好评数(4-5星)
     */
    private Integer positiveRatings=0;

    /**
     * 差评数(1-2星)
     */
    private Integer negativeRatings = 0;
}