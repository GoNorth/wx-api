package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizStore;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 门店表
 *
 * @author niefy
 * @date 2024-12-27
 */
public interface BizStoreService extends IService<BizStore> {
    /**
     * 分页查询门店数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizStore> queryPage(Map<String, Object> params);

    /**
     * 保存门店信息（包括门店、VI、人物形象及文件上传）
     * @param bizStore 门店信息
     * @param logoFile 门店LOGO文件
     * @param workUniformFile 工作服文件
     * @param ipImageFile IP形象文件
     * @param characterPhotoFile 人物照片文件
     * @param characterVoiceFile 人物声音文件
     * @param characterRole 人物角色
     */
    void saveWithFiles(BizStore bizStore, MultipartFile logoFile, MultipartFile workUniformFile, 
                      MultipartFile ipImageFile, MultipartFile characterPhotoFile, 
                      MultipartFile characterVoiceFile, String characterRole) throws Exception;
}

