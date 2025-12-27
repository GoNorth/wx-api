package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizStoreCharacterMapper;
import com.github.niefy.modules.biz.entity.BizStoreCharacter;
import com.github.niefy.modules.biz.service.BizStoreCharacterService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 门店人物形象表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Service
public class BizStoreCharacterServiceImpl extends ServiceImpl<BizStoreCharacterMapper, BizStoreCharacter> implements BizStoreCharacterService {

    @Override
    public IPage<BizStoreCharacter> queryPage(Map<String, Object> params) {
        String characterId = (String) params.get("characterId");
        String storeId = (String) params.get("storeId");
        String characterRole = (String) params.get("characterRole");

        return this.page(
            new Query<BizStoreCharacter>().getPage(params),
            new QueryWrapper<BizStoreCharacter>()
                .eq(StringUtils.hasText(characterId), "character_id", characterId)
                .eq(StringUtils.hasText(storeId), "store_id", storeId)
                .eq(StringUtils.hasText(characterRole), "character_role", characterRole)
                .eq("deleted", 0)
                .orderByDesc("create_time")
        );
    }
}

