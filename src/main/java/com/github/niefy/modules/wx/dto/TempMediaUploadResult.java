package com.github.niefy.modules.wx.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 临时素材上传结果
 * 
 * @author niefy
 * @date 2024-12-24
 */
@Data
public class TempMediaUploadResult implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 媒体文件类型
	 */
	private String type;

	/**
	 * 媒体文件上传后，获取时的唯一标识
	 */
	private String mediaId;

	/**
	 * 媒体文件上传时间戳
	 */
	private Long createdAt;
}

