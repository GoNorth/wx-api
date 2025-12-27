package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizStoreMapper;
import com.github.niefy.modules.biz.entity.BizStore;
import com.github.niefy.modules.biz.service.BizStoreService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 门店表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Service
public class BizStoreServiceImpl extends ServiceImpl<BizStoreMapper, BizStore> implements BizStoreService {

    @Override
    public IPage<BizStore> queryPage(Map<String, Object> params) {
        String storeId = (String) params.get("storeId");
        String ownerOpenid = (String) params.get("ownerOpenid");
        String ownerName = (String) params.get("ownerName");
        String ownerPhone = (String) params.get("ownerPhone");
        String storeName = (String) params.get("storeName");
        String cateringType = (String) params.get("cateringType");
        String customerGroup = (String) params.get("customerGroup");
        String auditStatus = (String) params.get("auditStatus");

        QueryWrapper<BizStore> queryWrapper = new QueryWrapper<BizStore>()
                .eq(StringUtils.hasText(storeId), "store_id", storeId)
                .eq(StringUtils.hasText(ownerOpenid), "owner_openid", ownerOpenid)
                .like(StringUtils.hasText(ownerName), "owner_name", ownerName)
                .eq(StringUtils.hasText(ownerPhone), "owner_phone", ownerPhone)
                .like(StringUtils.hasText(storeName), "store_name", storeName)
                .eq(StringUtils.hasText(cateringType), "catering_type", cateringType)
                .eq(StringUtils.hasText(customerGroup), "customer_group", customerGroup)
                .eq(StringUtils.hasText(auditStatus), "audit_status", auditStatus)
                .orderByDesc("create_time");
        
        // 临时注释掉 deleted 条件，用于调试
        // .eq("deleted", 0)
        
        return this.page(new Query<BizStore>().getPage(params), queryWrapper);
    }
}

