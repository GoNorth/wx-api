package com.github.niefy.modules.biz.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizScenarioDictType;
import com.github.niefy.modules.biz.service.BizScenarioDictTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 表单场景-字典类型绑定表-管理后台
 *
 * @author niefy
 * @date 2025-01-09
 */
@RestController
@RequestMapping("/manage/bizScenarioDictType")
@Api(tags = {"表单场景-字典类型绑定表-管理后台"})
public class BizScenarioDictTypeManageController {
    private static final Logger logger = LoggerFactory.getLogger(BizScenarioDictTypeManageController.class);

    @Autowired
    private BizScenarioDictTypeService bizScenarioDictTypeService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取绑定关系列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizScenarioDictTypeService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 根据场景ID获取绑定的字典类型列表
     */
    @GetMapping("/listByScenario")
    @ApiOperation(value = "根据场景ID获取绑定的字典类型列表")
    public R listByScenario(@RequestParam String scenarioId) {
        if (!StringUtils.hasText(scenarioId)) {
            return R.error("场景ID不能为空");
        }
        
        List<BizScenarioDictType> list = bizScenarioDictTypeService.list(
            new QueryWrapper<BizScenarioDictType>()
                .eq("scenario_id", scenarioId)
                .eq("status", "ACTIVE")
                .eq("deleted", 0)
                .orderByAsc("sort_order")
        );
        
        return R.ok().put("list", list);
    }

    /**
     * 根据字典类型编码获取绑定的场景列表
     */
    @GetMapping("/listByDictType")
    @ApiOperation(value = "根据字典类型编码获取绑定的场景列表")
    public R listByDictType(@RequestParam String dictTypeCode) {
        if (!StringUtils.hasText(dictTypeCode)) {
            return R.error("字典类型编码不能为空");
        }
        
        List<BizScenarioDictType> list = bizScenarioDictTypeService.list(
            new QueryWrapper<BizScenarioDictType>()
                .eq("dict_type_code", dictTypeCode)
                .eq("status", "ACTIVE")
                .eq("deleted", 0)
                .orderByAsc("sort_order")
        );
        
        return R.ok().put("list", list);
    }

    /**
     * 检查绑定关系
     */
    @GetMapping("/check")
    @ApiOperation(value = "检查场景和字典类型是否绑定")
    public R check(@RequestParam String scenarioId, @RequestParam String dictTypeCode) {
        boolean isBound = bizScenarioDictTypeService.isBound(scenarioId, dictTypeCode);
        return R.ok().put("isBound", isBound);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存绑定关系")
    public R save(@RequestBody BizScenarioDictType bizScenarioDictType) {
        // 检查是否已存在
        if (StringUtils.hasText(bizScenarioDictType.getScenarioId()) 
            && StringUtils.hasText(bizScenarioDictType.getDictTypeCode())) {
            BizScenarioDictType existing = bizScenarioDictTypeService.getOne(
                new QueryWrapper<BizScenarioDictType>()
                    .eq("scenario_id", bizScenarioDictType.getScenarioId())
                    .eq("dict_type_code", bizScenarioDictType.getDictTypeCode())
                    .eq("deleted", 0)
            );
            if (existing != null && !existing.getRelationId().equals(bizScenarioDictType.getRelationId())) {
                return R.error("该绑定关系已存在");
            }
        }
        
        // 设置默认值
        if (bizScenarioDictType.getRelationId() == null || bizScenarioDictType.getRelationId().isEmpty()) {
            bizScenarioDictType.setRelationId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (bizScenarioDictType.getSortOrder() == null) {
            bizScenarioDictType.setSortOrder(0);
        }
        if (bizScenarioDictType.getStatus() == null || bizScenarioDictType.getStatus().isEmpty()) {
            bizScenarioDictType.setStatus("ACTIVE");
        }
        if (bizScenarioDictType.getDeleted() == null) {
            bizScenarioDictType.setDeleted(0);
        }
        if (bizScenarioDictType.getCreateTime() == null) {
            bizScenarioDictType.setCreateTime(new Date());
        }
        bizScenarioDictType.setUpdateTime(new Date());
        
        bizScenarioDictTypeService.save(bizScenarioDictType);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新绑定关系")
    public R update(@RequestBody BizScenarioDictType bizScenarioDictType) {
        // 检查是否与其他记录冲突
        if (StringUtils.hasText(bizScenarioDictType.getScenarioId()) 
            && StringUtils.hasText(bizScenarioDictType.getDictTypeCode())) {
            BizScenarioDictType existing = bizScenarioDictTypeService.getOne(
                new QueryWrapper<BizScenarioDictType>()
                    .eq("scenario_id", bizScenarioDictType.getScenarioId())
                    .eq("dict_type_code", bizScenarioDictType.getDictTypeCode())
                    .ne("relation_id", bizScenarioDictType.getRelationId())
                    .eq("deleted", 0)
            );
            if (existing != null) {
                return R.error("该绑定关系已存在");
            }
        }
        
        bizScenarioDictType.setUpdateTime(new Date());
        bizScenarioDictTypeService.updateById(bizScenarioDictType);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除绑定关系")
    public R delete(@RequestBody String[] relationIds) {
        for (String relationId : relationIds) {
            BizScenarioDictType bizScenarioDictType = bizScenarioDictTypeService.getById(relationId);
            if (bizScenarioDictType != null) {
                bizScenarioDictType.setDeleted(1);
                bizScenarioDictType.setUpdateTime(new Date());
                bizScenarioDictTypeService.updateById(bizScenarioDictType);
            }
        }
        return R.ok();
    }
}

