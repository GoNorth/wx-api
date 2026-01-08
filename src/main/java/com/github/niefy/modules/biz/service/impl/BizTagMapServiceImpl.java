package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizTagMapMapper;
import com.github.niefy.modules.biz.entity.BizTagMap;
import com.github.niefy.modules.biz.service.BizTagMapService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 标签映射表（标签与模板的关联表）
 *
 * @author niefy
 * @date 2026-01-07
 */
@Service
public class BizTagMapServiceImpl extends ServiceImpl<BizTagMapMapper, BizTagMap> implements BizTagMapService {

    @Override
    public IPage<BizTagMap> queryPage(Map<String, Object> params) {
        String mapId = (String) params.get("mapId");
        String tagId = (String) params.get("tagId");
        String templateId = (String) params.get("templateId");
        
        QueryWrapper<BizTagMap> queryWrapper = new QueryWrapper<BizTagMap>()
                .eq(StringUtils.hasText(mapId), "map_id", mapId)
                .eq(StringUtils.hasText(tagId), "tag_id", tagId)
                .eq(StringUtils.hasText(templateId), "template_id", templateId)
                .eq("deleted", 0);

        queryWrapper.orderByDesc("create_time");

        return this.page(new Query<BizTagMap>().getPage(params), queryWrapper);
    }
}

