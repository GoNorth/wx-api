package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizPlanItem;

import java.util.Map;

/**
 * 计划项目表
 *
 * @author niefy
 * @date 2024-12-27
 */
public interface BizPlanItemService extends IService<BizPlanItem> {
    /**
     * 分页查询计划项目数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizPlanItem> queryPage(Map<String, Object> params);

    /**
     * 保存或更新计划项目（根据itemId是否存在判断是新增还是修改）
     * @param bizPlanItem 计划项目信息
     */
    void saveOrUpdateItem(BizPlanItem bizPlanItem);
}

