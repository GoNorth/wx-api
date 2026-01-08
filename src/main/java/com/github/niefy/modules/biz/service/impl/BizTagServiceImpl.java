package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizTagMapper;
import com.github.niefy.modules.biz.entity.BizTag;
import com.github.niefy.modules.biz.service.BizTagService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 标签表
 *
 * @author niefy
 * @date 2026-01-07
 */
@Service
public class BizTagServiceImpl extends ServiceImpl<BizTagMapper, BizTag> implements BizTagService {

    @Override
    public IPage<BizTag> queryPage(Map<String, Object> params) {
        String tagId = (String) params.get("tagId");
        String tagName = (String) params.get("tagName");
        String tagDesc = (String) params.get("tagDesc");
        
        QueryWrapper<BizTag> queryWrapper = new QueryWrapper<BizTag>()
                .eq(StringUtils.hasText(tagId), "tag_id", tagId)
                .like(StringUtils.hasText(tagName), "tag_name", tagName)
                .like(StringUtils.hasText(tagDesc), "tag_desc", tagDesc)
                .eq("deleted", 0);

        queryWrapper.orderByDesc("create_time");

        return this.page(new Query<BizTag>().getPage(params), queryWrapper);
    }
}

