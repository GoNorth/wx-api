package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizDictItemMapper;
import com.github.niefy.modules.biz.entity.BizDictItem;
import com.github.niefy.modules.biz.service.BizDictItemService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 字典项表
 *
 * @author niefy
 * @date 2025-01-08
 */
@Service
public class BizDictItemServiceImpl extends ServiceImpl<BizDictItemMapper, BizDictItem> implements BizDictItemService {

    @Override
    public IPage<BizDictItem> queryPage(Map<String, Object> params) {
        String dictItemId = (String) params.get("dictItemId");
        String dictTypeCode = (String) params.get("dictTypeCode");
        String dictItemValue = (String) params.get("dictItemValue");
        String status = (String) params.get("status");
        
        QueryWrapper<BizDictItem> queryWrapper = new QueryWrapper<BizDictItem>()
                .eq(StringUtils.hasText(dictItemId), "dict_item_id", dictItemId)
                .eq(StringUtils.hasText(dictTypeCode), "dict_type_code", dictTypeCode)
                .like(StringUtils.hasText(dictItemValue), "dict_item_value", dictItemValue)
                .eq(StringUtils.hasText(status), "status", status)
                .eq("deleted", 0);

        queryWrapper.orderByAsc("sort_order").orderByDesc("create_time");

        return this.page(new Query<BizDictItem>().getPage(params), queryWrapper);
    }
}

