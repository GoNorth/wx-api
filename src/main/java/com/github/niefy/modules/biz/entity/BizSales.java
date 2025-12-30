package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 销售员表
 *
 * @author niefy
 * @date 2025-01-01
 */
@Data
@TableName("biz_sales")
public class BizSales implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String salesId;

    /**
     * 销售账号
     */
    private String salesAccount;

    /**
     * 销售姓名
     */
    private String salesName;

    /**
     * 性别字典CODE：MALE-男，FEMALE-女
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 住址
     */
    private String address;

    /**
     * 逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }
}

