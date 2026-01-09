package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 场景组件关联表
 *
 * @author niefy
 * @date 2026-01-09
 */
@Data
@TableName("biz_form_scenario_component")
public class BizFormScenarioComponent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String relationId;

    /**
     * 场景ID，关联biz_form_scenario表
     */
    private String scenarioId;

    /**
     * 组件ID，关联biz_form_component表
     */
    private String componentId;

    /**
     * 场景内组件配置覆盖JSON，可覆盖组件默认配置
     */
    private String componentConfigOverride;

    /**
     * 场景内组件排序顺序，数字越小越靠前
     */
    private Integer sortOrder;

    /**
     * 状态字典CODE：ACTIVE-启用，INACTIVE-禁用
     */
    private String status;

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

