package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizStoreCharacter;

import java.util.Map;

/**
 * 门店人物形象表
 *
 * @author niefy
 * @date 2024-12-27
 */
public interface BizStoreCharacterService extends IService<BizStoreCharacter> {
    /**
     * 分页查询门店人物形象数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizStoreCharacter> queryPage(Map<String, Object> params);
}

