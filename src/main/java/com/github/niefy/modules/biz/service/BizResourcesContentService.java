package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizResourcesContent;

import java.util.Map;

/**
 * 资源(图片视频)内容表
 *
 * @author niefy
 * @date 2024-12-27
 */
public interface BizResourcesContentService extends IService<BizResourcesContent> {
    /**
     * 分页查询资源内容数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizResourcesContent> queryPage(Map<String, Object> params);
}

