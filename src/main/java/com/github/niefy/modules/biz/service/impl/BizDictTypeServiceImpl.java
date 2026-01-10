package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizDictTypeMapper;
import com.github.niefy.modules.biz.entity.BizDictType;
import com.github.niefy.modules.biz.service.BizDictTypeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 字典类型表
 *
 * @author niefy
 * @date 2025-01-08
 */
@Service
public class BizDictTypeServiceImpl extends ServiceImpl<BizDictTypeMapper, BizDictType> implements BizDictTypeService {

    @Override
    public IPage<BizDictType> queryPage(Map<String, Object> params) {
        String dictTypeId = (String) params.get("dictTypeId");
        String dictTypeCode = (String) params.get("dictTypeCode");
        String dictTypeName = (String) params.get("dictTypeName");
        String category = (String) params.get("category");
        String status = (String) params.get("status");
        
        QueryWrapper<BizDictType> queryWrapper = new QueryWrapper<BizDictType>()
                .eq(StringUtils.hasText(dictTypeId), "dict_type_id", dictTypeId)
                .eq(StringUtils.hasText(dictTypeCode), "dict_type_code", dictTypeCode)
                .like(StringUtils.hasText(dictTypeName), "dict_type_name", dictTypeName)
                .eq(StringUtils.hasText(category), "category", category)
                .eq(StringUtils.hasText(status), "status", status)
                .eq("deleted", 0);

        queryWrapper.orderByAsc("sort_order").orderByDesc("create_time");

        return this.page(new Query<BizDictType>().getPage(params), queryWrapper);
    }
}

