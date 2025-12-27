package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 门店VI表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Data
@TableName("biz_store_vi")
public class BizStoreVi implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String viId;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 门店LOGO URL
     */
    private String logoUrl;

    /**
     * 工作服照片URL
     */
    private String workUniformUrl;

    /**
     * IP形象设计图URL
     */
    private String ipImageUrl;

    /**
     * 资源来源字典CODE：UPLOAD-用户上传，GENERATE-程序生成
     */
    private String fromType;

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

