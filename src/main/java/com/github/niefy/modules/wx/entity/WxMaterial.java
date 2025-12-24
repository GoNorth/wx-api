package com.github.niefy.modules.wx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 微信素材
 * 
 * @author niefy
 * @date 2024-12-24
 */
@Data
@TableName("wx_material")
public class WxMaterial implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * appid
	 */
	private String appid;

	/**
	 * 企业ID
	 */
	private String enterpriseId;

	/**
	 * 图片视频的需求日期
	 */
	private Date forDate;

	/**
	 * 素材存储类型：TEMP-临时的；Perm-永久的
	 */
	private String mediaStore;

	/**
	 * 微信素材ID
	 */
	private String mediaId;

	/**
	 * 素材类型(image/video/voice/thumb)
	 */
	private String mediaType;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 素材URL(仅图片和视频有)
	 */
	private String url;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;
}

