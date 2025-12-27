package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 门店表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Data
@TableName("biz_store")
public class BizStore implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String storeId;

    /**
     * 店主微信OPENID，关联WX_USER表的OPENID
     */
    private String ownerOpenid;

    /**
     * 店主姓名
     */
    private String ownerName;

    /**
     * 店主手机号
     */
    private String ownerPhone;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 餐饮种类字典CODE：CHINESE-中餐，WESTERN-西餐，JAPANESE-日料，HOTPOT-火锅，BARBECUE-烧烤，FASTFOOD-快餐，SNACK-小吃，OTHER-其他
     */
    private String cateringType;

    /**
     * 门店详细地址
     */
    private String storeAddress;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 客户人群字典CODE：STUDENT-学生，OFFICE_WORKER-上班族，FAMILY-家庭，BUSINESS-商务人士，ELDERLY-老年人，OTHER-其他
     */
    private String customerGroup;

    /**
     * 审核状态字典CODE：0-PENDING待审核，1-APPROVED已通过，2-REJECTED已拒绝
     */
    private Integer auditStatus;

    /**
     * 审核备注
     */
    private String auditRemark;

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

