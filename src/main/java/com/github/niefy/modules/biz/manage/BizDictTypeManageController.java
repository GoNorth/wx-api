package com.github.niefy.modules.biz.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizDictType;
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
import java.util.Map;
import java.util.UUID;

/**
 * 字典类型表-管理后台
 *
 * @author niefy
 * @date 2025-01-08
 */
@RestController
@RequestMapping("/biz/dict/type")
@Api(tags = {"字典类型表-管理后台"})
public class BizDictTypeManageController {
    private static final Logger logger = LoggerFactory.getLogger(BizDictTypeManageController.class);

    @Autowired
    private BizDictTypeService bizDictTypeService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取字典类型列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizDictTypeService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info")
    @ApiOperation(value = "获取字典类型详情")
    public R info(@RequestParam(required = false) String dictTypeCode, 
                   @RequestParam(required = false) String dictTypeId) {
        BizDictType bizDictType = null;
        if (StringUtils.hasText(dictTypeCode)) {
            bizDictType = bizDictTypeService.getOne(
                new QueryWrapper<BizDictType>()
                    .eq("dict_type_code", dictTypeCode)
                    .eq("deleted", 0)
            );
        } else if (StringUtils.hasText(dictTypeId)) {
            bizDictType = bizDictTypeService.getById(dictTypeId);
        }
        
        if (bizDictType == null || bizDictType.getDeleted() != null && bizDictType.getDeleted() == 1) {
            return R.error("字典类型不存在");
        }
        
        return R.ok().put("info", bizDictType);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "创建字典类型")
    public R save(@RequestBody BizDictType bizDictType) {
        // 检查字典类型编码是否已存在
        BizDictType existing = bizDictTypeService.getOne(
            new QueryWrapper<BizDictType>()
                .eq("dict_type_code", bizDictType.getDictTypeCode())
                .eq("deleted", 0)
        );
        if (existing != null) {
            return R.error("字典类型编码已存在");
        }
        
        // 设置默认值
        if (bizDictType.getDictTypeId() == null || bizDictType.getDictTypeId().isEmpty()) {
            bizDictType.setDictTypeId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (bizDictType.getSortOrder() == null) {
            bizDictType.setSortOrder(0);
        }
        if (bizDictType.getStatus() == null || bizDictType.getStatus().isEmpty()) {
            bizDictType.setStatus("ACTIVE");
        }
        if (bizDictType.getAllowCreate() == null) {
            bizDictType.setAllowCreate(1);
        }
        if (bizDictType.getDeleted() == null) {
            bizDictType.setDeleted(0);
        }
        if (bizDictType.getCreateTime() == null) {
            bizDictType.setCreateTime(new Date());
        }
        bizDictType.setUpdateTime(new Date());
        
        bizDictTypeService.save(bizDictType);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新字典类型")
    public R update(@RequestBody BizDictType bizDictType) {
        // 检查字典类型编码是否与其他记录冲突
        if (StringUtils.hasText(bizDictType.getDictTypeCode())) {
            BizDictType existing = bizDictTypeService.getOne(
                new QueryWrapper<BizDictType>()
                    .eq("dict_type_code", bizDictType.getDictTypeCode())
                    .ne("dict_type_id", bizDictType.getDictTypeId())
                    .eq("deleted", 0)
            );
            if (existing != null) {
                return R.error("字典类型编码已存在");
            }
        }
        
        bizDictType.setUpdateTime(new Date());
        bizDictTypeService.updateById(bizDictType);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除字典类型")
    public R delete(@RequestBody String[] dictTypeIds) {
        for (String dictTypeId : dictTypeIds) {
            BizDictType bizDictType = bizDictTypeService.getById(dictTypeId);
            if (bizDictType != null) {
                bizDictType.setDeleted(1);
                bizDictType.setUpdateTime(new Date());
                bizDictTypeService.updateById(bizDictType);
            }
        }
        return R.ok();
    }
}

