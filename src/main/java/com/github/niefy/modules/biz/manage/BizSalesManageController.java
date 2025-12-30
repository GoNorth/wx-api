package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizSales;
import com.github.niefy.modules.biz.service.BizSalesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 销售员表-管理后台
 *
 * @author niefy
 * @date 2025-01-01
 */
@RestController
@RequestMapping("/manage/bizSales")
@Api(tags = {"销售员表-管理后台"})
public class BizSalesManageController {
    
    @Autowired
    private BizSalesService bizSalesService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizsales:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizSalesService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{salesId}")
    // @RequiresPermissions("biz:bizsales:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("salesId") String salesId) {
        BizSales bizSales = bizSalesService.getById(salesId);
        return R.ok().put("bizSales", bizSales);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizsales:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizSales bizSales) {
        // 如果salesId为空，生成新的ID
        if (bizSales.getSalesId() == null || bizSales.getSalesId().isEmpty()) {
            bizSales.setSalesId(UUID.randomUUID().toString().replace("-", ""));
        }
        
        // 设置默认值
        if (bizSales.getDeleted() == null) {
            bizSales.setDeleted(0);
        }
        if (bizSales.getCreateTime() == null) {
            bizSales.setCreateTime(new Date());
        }
        bizSales.setUpdateTime(new Date());
        
        bizSalesService.save(bizSales);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizsales:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizSales bizSales) {
        bizSales.setUpdateTime(new Date());
        bizSalesService.updateById(bizSales);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizsales:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] salesIds) {
        bizSalesService.removeByIds(Arrays.asList(salesIds));
        return R.ok();
    }
}

