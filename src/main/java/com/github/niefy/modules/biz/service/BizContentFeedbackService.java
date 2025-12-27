package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizContentFeedback;

import java.util.Map;

/**
 * 内容反馈表
 *
 * @author niefy
 * @date 2024-12-27
 */
public interface BizContentFeedbackService extends IService<BizContentFeedback> {
    /**
     * 分页查询内容反馈数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizContentFeedback> queryPage(Map<String, Object> params);
}

