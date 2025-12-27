package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizStoreViMapper;
import com.github.niefy.modules.biz.entity.BizStoreVi;
import com.github.niefy.modules.biz.service.BizStoreViService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 门店VI表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Service
public class BizStoreViServiceImpl extends ServiceImpl<BizStoreViMapper, BizStoreVi> implements BizStoreViService {

    @Override
    public IPage<BizStoreVi> queryPage(Map<String, Object> params) {
        String viId = (String) params.get("viId");
        String storeId = (String) params.get("storeId");

        return this.page(
            new Query<BizStoreVi>().getPage(params),
            new QueryWrapper<BizStoreVi>()
                .eq(StringUtils.hasText(viId), "vi_id", viId)
                .eq(StringUtils.hasText(storeId), "store_id", storeId)
                .eq("deleted", 0)
                .orderByDesc("create_time")
        );
    }
}

