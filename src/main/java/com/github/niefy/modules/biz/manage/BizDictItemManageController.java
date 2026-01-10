package com.github.niefy.modules.biz.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizDictItem;
import com.github.niefy.modules.biz.entity.BizDictType;
import com.github.niefy.modules.biz.service.BizDictItemService;
import com.github.niefy.modules.biz.service.BizDictTypeService;
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
 * 字典项表-管理后台
 *
 * @author niefy
 * @date 2025-01-08
 */
@RestController
@RequestMapping("/biz/dict/item")
@Api(tags = {"字典项表-管理后台"})
public class BizDictItemManageController {
    private static final Logger logger = LoggerFactory.getLogger(BizDictItemManageController.class);

    @Autowired
    private BizDictItemService bizDictItemService;
    
    @Autowired
    private BizDictTypeService bizDictTypeService;
    
    @Autowired
    private com.github.niefy.modules.biz.service.BizScenarioDictTypeService bizScenarioDictTypeService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取字典项列表")
    public R list(@RequestParam Map<String, Object> params) {
        // 如果指定了dictTypeCode，优先返回list而不是page
        String dictTypeCode = (String) params.get("dictTypeCode");
        String scenarioId = (String) params.get("scenarioId");
        
        // 如果指定了场景ID，需要检查该场景是否绑定了该字典类型
        if (StringUtils.hasText(scenarioId) && StringUtils.hasText(dictTypeCode)) {
            boolean isBound = bizScenarioDictTypeService.isBound(scenarioId, dictTypeCode);
            if (!isBound) {
                // 如果未绑定，返回空列表
                return R.ok().put("list", new java.util.ArrayList<>());
            }
        }
        
        if (StringUtils.hasText(dictTypeCode)) {
            List<BizDictItem> list = bizDictItemService.list(
                new QueryWrapper<BizDictItem>()
                    .eq("dict_type_code", dictTypeCode)
                    .eq(StringUtils.hasText((String) params.get("status")), "status", params.get("status"))
                    .eq("deleted", 0)
                    .orderByAsc("sort_order")
            );
            return R.ok().put("list", list);
        }
        
        PageUtils page = new PageUtils(bizDictItemService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 创建字典项（即时添加）
     */
    @PostMapping("/create")
    @ApiOperation(value = "创建字典项（即时添加）")
    public R create(@RequestBody BizDictItem bizDictItem) {
        // 检查字典类型是否存在且允许创建
        if (!StringUtils.hasText(bizDictItem.getDictTypeCode())) {
            return R.error("字典类型编码不能为空");
        }
        
        BizDictType dictType = bizDictTypeService.getOne(
            new QueryWrapper<BizDictType>()
                .eq("dict_type_code", bizDictItem.getDictTypeCode())
                .eq("deleted", 0)
        );
        
        if (dictType == null) {
            return R.error("字典类型不存在");
        }
        
        if (dictType.getAllowCreate() != null && dictType.getAllowCreate() == 0) {
            return R.error("该字典类型不允许创建新项");
        }
        
        // 检查字典项值是否已存在
        BizDictItem existing = bizDictItemService.getOne(
            new QueryWrapper<BizDictItem>()
                .eq("dict_type_code", bizDictItem.getDictTypeCode())
                .eq("dict_item_value", bizDictItem.getDictItemValue())
                .eq("deleted", 0)
        );
        if (existing != null) {
            return R.error("该字典项已存在");
        }
        
        // 设置默认值
        if (bizDictItem.getDictItemId() == null || bizDictItem.getDictItemId().isEmpty()) {
            bizDictItem.setDictItemId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (!StringUtils.hasText(bizDictItem.getDictItemLabel())) {
            bizDictItem.setDictItemLabel(bizDictItem.getDictItemValue());
        }
        if (bizDictItem.getSortOrder() == null) {
            bizDictItem.setSortOrder(0);
        }
        if (bizDictItem.getStatus() == null || bizDictItem.getStatus().isEmpty()) {
            bizDictItem.setStatus("ACTIVE");
        }
        if (bizDictItem.getDeleted() == null) {
            bizDictItem.setDeleted(0);
        }
        if (bizDictItem.getCreateTime() == null) {
            bizDictItem.setCreateTime(new Date());
        }
        bizDictItem.setUpdateTime(new Date());
        
        bizDictItemService.save(bizDictItem);
        return R.ok().put("data", bizDictItem);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "保存字典项")
    public R save(@RequestBody BizDictItem bizDictItem) {
        // 检查字典类型是否存在
        if (StringUtils.hasText(bizDictItem.getDictTypeCode())) {
            BizDictType dictType = bizDictTypeService.getOne(
                new QueryWrapper<BizDictType>()
                    .eq("dict_type_code", bizDictItem.getDictTypeCode())
                    .eq("deleted", 0)
            );
            if (dictType == null) {
                return R.error("字典类型不存在");
            }
        }
        
        // 检查字典项值是否与其他记录冲突
        if (StringUtils.hasText(bizDictItem.getDictItemValue())) {
            QueryWrapper<BizDictItem> queryWrapper = new QueryWrapper<BizDictItem>()
                .eq("dict_type_code", bizDictItem.getDictTypeCode())
                .eq("dict_item_value", bizDictItem.getDictItemValue())
                .eq("deleted", 0);
            
            if (StringUtils.hasText(bizDictItem.getDictItemId())) {
                queryWrapper.ne("dict_item_id", bizDictItem.getDictItemId());
            }
            
            BizDictItem existing = bizDictItemService.getOne(queryWrapper);
            if (existing != null) {
                return R.error("该字典项值已存在");
            }
        }
        
        // 设置默认值
        if (bizDictItem.getDictItemId() == null || bizDictItem.getDictItemId().isEmpty()) {
            bizDictItem.setDictItemId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (!StringUtils.hasText(bizDictItem.getDictItemLabel())) {
            bizDictItem.setDictItemLabel(bizDictItem.getDictItemValue());
        }
        if (bizDictItem.getSortOrder() == null) {
            bizDictItem.setSortOrder(0);
        }
        if (bizDictItem.getStatus() == null || bizDictItem.getStatus().isEmpty()) {
            bizDictItem.setStatus("ACTIVE");
        }
        if (bizDictItem.getDeleted() == null) {
            bizDictItem.setDeleted(0);
        }
        if (bizDictItem.getCreateTime() == null) {
            bizDictItem.setCreateTime(new Date());
        }
        bizDictItem.setUpdateTime(new Date());
        
        bizDictItemService.save(bizDictItem);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新字典项")
    public R update(@RequestBody BizDictItem bizDictItem) {
        // 检查字典项值是否与其他记录冲突
        if (StringUtils.hasText(bizDictItem.getDictItemValue())) {
            BizDictItem existing = bizDictItemService.getOne(
                new QueryWrapper<BizDictItem>()
                    .eq("dict_type_code", bizDictItem.getDictTypeCode())
                    .eq("dict_item_value", bizDictItem.getDictItemValue())
                    .ne("dict_item_id", bizDictItem.getDictItemId())
                    .eq("deleted", 0)
            );
            if (existing != null) {
                return R.error("该字典项值已存在");
            }
        }
        
        bizDictItem.setUpdateTime(new Date());
        bizDictItemService.updateById(bizDictItem);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除字典项")
    public R delete(@RequestBody String[] dictItemIds) {
        for (String dictItemId : dictItemIds) {
            BizDictItem bizDictItem = bizDictItemService.getById(dictItemId);
            if (bizDictItem != null) {
                bizDictItem.setDeleted(1);
                bizDictItem.setUpdateTime(new Date());
                bizDictItemService.updateById(bizDictItem);
            }
        }
        return R.ok();
    }
}

