package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizFormComponentMapper;
import com.github.niefy.modules.biz.entity.BizFormComponent;
import com.github.niefy.modules.biz.service.BizFormComponentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 表单组件表
 *
 * @author niefy
 * @date 2026-01-09
 */
@Service
public class BizFormComponentServiceImpl extends ServiceImpl<BizFormComponentMapper, BizFormComponent> implements BizFormComponentService {

    @Override
    public IPage<BizFormComponent> queryPage(Map<String, Object> params) {
        String componentId = (String) params.get("componentId");
        String componentType = (String) params.get("componentType");
        String componentCode = (String) params.get("componentCode");
        String componentName = (String) params.get("componentName");
        String status = (String) params.get("status");
        
        QueryWrapper<BizFormComponent> queryWrapper = new QueryWrapper<BizFormComponent>()
                .eq(StringUtils.hasText(componentId), "component_id", componentId)
                .eq(StringUtils.hasText(componentType), "component_type", componentType)
                .eq(StringUtils.hasText(componentCode), "component_code", componentCode)
                .like(StringUtils.hasText(componentName), "component_name", componentName)
                .eq(StringUtils.hasText(status), "status", status)
                .eq("deleted", 0);

        queryWrapper.orderByAsc("sort_order").orderByDesc("create_time");

        return this.page(new Query<BizFormComponent>().getPage(params), queryWrapper);
    }
}

