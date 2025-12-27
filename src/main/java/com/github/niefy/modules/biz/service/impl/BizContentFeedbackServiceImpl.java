package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizContentFeedbackMapper;
import com.github.niefy.modules.biz.entity.BizContentFeedback;
import com.github.niefy.modules.biz.service.BizContentFeedbackService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 内容反馈表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Service
public class BizContentFeedbackServiceImpl extends ServiceImpl<BizContentFeedbackMapper, BizContentFeedback> implements BizContentFeedbackService {

    @Override
    public IPage<BizContentFeedback> queryPage(Map<String, Object> params) {
        String feedbackId = (String) params.get("feedbackId");
        String contentId = (String) params.get("contentId");
        String feedbackType = (String) params.get("feedbackType");
        String status = (String) params.get("status");
        String userOpenid = (String) params.get("userOpenid");

        return this.page(
            new Query<BizContentFeedback>().getPage(params),
            new QueryWrapper<BizContentFeedback>()
                .eq(StringUtils.hasText(feedbackId), "feedback_id", feedbackId)
                .eq(StringUtils.hasText(contentId), "content_id", contentId)
                .eq(StringUtils.hasText(feedbackType), "feedback_type", feedbackType)
                .eq(StringUtils.hasText(status), "status", status)
                .eq(StringUtils.hasText(userOpenid), "user_openid", userOpenid)
                .eq("deleted", 0)
                .orderByDesc("create_time")
        );
    }
}

