package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 表单场景-字典类型绑定表
 *
 * @author niefy
 * @date 2025-01-09
 */
@Data
@TableName("biz_scenario_dict_type")
public class BizScenarioDictType implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String relationId;

    /**
     * 表单场景ID，关联biz_form_scenario表
     */
    private String scenarioId;

    /**
     * 字典类型编码，关联biz_dict_type表
     */
    private String dictTypeCode;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：ACTIVE-启用，INACTIVE-禁用
     */
    private String status;

    /**
     * 备注说明
     */
    private String remark;

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

