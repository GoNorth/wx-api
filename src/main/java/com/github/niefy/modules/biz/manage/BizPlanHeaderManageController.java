package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizPlanHeader;
import com.github.niefy.modules.biz.service.BizPlanHeaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 计划头部表-管理后台
 *
 * @author niefy
 * @date 2024-12-27
 */
@RestController
@RequestMapping("/manage/bizPlanHeader")
@Api(tags = {"计划头部表-管理后台"})
public class BizPlanHeaderManageController {
    @Autowired
    private BizPlanHeaderService bizPlanHeaderService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizplanheader:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizPlanHeaderService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{planId}")
    // @RequiresPermissions("biz:bizplanheader:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("planId") String planId) {
        BizPlanHeader bizPlanHeader = bizPlanHeaderService.getById(planId);
        return R.ok().put("bizPlanHeader", bizPlanHeader);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizplanheader:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizPlanHeader bizPlanHeader) {
        bizPlanHeaderService.save(bizPlanHeader);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizplanheader:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizPlanHeader bizPlanHeader) {
        bizPlanHeaderService.updateById(bizPlanHeader);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizplanheader:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] planIds) {
        bizPlanHeaderService.removeByIds(Arrays.asList(planIds));
        return R.ok();
    }
}

