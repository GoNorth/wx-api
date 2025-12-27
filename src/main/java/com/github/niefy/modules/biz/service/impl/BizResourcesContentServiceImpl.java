package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizResourcesContentMapper;
import com.github.niefy.modules.biz.entity.BizResourcesContent;
import com.github.niefy.modules.biz.service.BizResourcesContentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 资源(图片视频)内容表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Service
public class BizResourcesContentServiceImpl extends ServiceImpl<BizResourcesContentMapper, BizResourcesContent> implements BizResourcesContentService {

    @Override
    public IPage<BizResourcesContent> queryPage(Map<String, Object> params) {
        String contentId = (String) params.get("contentId");
        String storeId = (String) params.get("storeId");
        String contentType = (String) params.get("contentType");
        String planItemId = (String) params.get("planItemId");
        String title = (String) params.get("title");
        String publishDate = (String) params.get("publishDate");

        return this.page(
            new Query<BizResourcesContent>().getPage(params),
            new QueryWrapper<BizResourcesContent>()
                .eq(StringUtils.hasText(contentId), "content_id", contentId)
                .eq(StringUtils.hasText(storeId), "store_id", storeId)
                .eq(StringUtils.hasText(contentType), "content_type", contentType)
                .eq(StringUtils.hasText(planItemId), "plan_item_id", planItemId)
                .like(StringUtils.hasText(title), "title", title)
                .eq(StringUtils.hasText(publishDate), "publish_date", publishDate)
                .eq("deleted", 0)
                .orderByDesc("create_time")
        );
    }
}

