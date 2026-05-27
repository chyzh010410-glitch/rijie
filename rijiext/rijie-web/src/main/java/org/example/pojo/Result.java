package org.example.pojo;

import lombok.Data;

/**
 * 统一响应结果实体类
 * 用于前后端接口通信的标准化返回格式
 */
@Data
public class Result {
    /**
     * 响应状态码：200=成功，500=失败（可扩展其他业务状态码：400=参数错误，401=未登录，403=无权限等）
     */
    private Integer code;

    /**
     * 响应提示消息（如“操作成功”“参数错误”“查询失败”）
     */
    private String message;

    /**
     * 响应数据（任意数据类型，无数据时为null）
     */
    private Object data;


    // ------------------------------ 静态工具方法：快捷构建响应结果 ------------------------------
    /**
     * 成功响应（无数据）
     */
    public static Result success() {
        Result result = new Result();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }

    /**
     * 成功响应（带数据）
     * @param data 要返回的业务数据（任意类型）
     */
    public static Result success(Object data) {
        Result result = new Result();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 失败响应（默认状态码500）
     * @param message 失败提示消息
     */
    public static Result fail(String message) {
        Result result = new Result();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /**
     * 自定义响应（指定状态码和消息，无数据）
     * @param code 状态码
     * @param message 提示消息
     */
    public static Result custom(Integer code, String message) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 自定义响应（指定状态码、消息和数据）
     * @param code 状态码
     * @param message 提示消息
     * @param data 响应数据
     */
    public static Result custom(Integer code, String message, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}