package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizScenarioDictType;

import java.util.Map;

/**
 * 表单场景-字典类型绑定表
 *
 * @author niefy
 * @date 2025-01-09
 */
public interface BizScenarioDictTypeService extends IService<BizScenarioDictType> {
    /**
     * 分页查询绑定关系数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizScenarioDictType> queryPage(Map<String, Object> params);
    
    /**
     * 检查场景和字典类型是否绑定
     * @param scenarioId 场景ID
     * @param dictTypeCode 字典类型编码
     * @return true-已绑定，false-未绑定
     */
    boolean isBound(String scenarioId, String dictTypeCode);
}

