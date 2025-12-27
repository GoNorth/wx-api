package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizPlanItem;
import com.github.niefy.modules.biz.service.BizPlanItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 计划项目表-管理后台
 *
 * @author niefy
 * @date 2024-12-27
 */
@RestController
@RequestMapping("/manage/bizPlanItem")
@Api(tags = {"计划项目表-管理后台"})
public class BizPlanItemManageController {
    @Autowired
    private BizPlanItemService bizPlanItemService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizplanitem:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizPlanItemService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{itemId}")
    // @RequiresPermissions("biz:bizplanitem:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("itemId") String itemId) {
        BizPlanItem bizPlanItem = bizPlanItemService.getById(itemId);
        return R.ok().put("bizPlanItem", bizPlanItem);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizplanitem:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizPlanItem bizPlanItem) {
        bizPlanItemService.save(bizPlanItem);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizplanitem:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizPlanItem bizPlanItem) {
        bizPlanItemService.updateById(bizPlanItem);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizplanitem:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] itemIds) {
        bizPlanItemService.removeByIds(Arrays.asList(itemIds));
        return R.ok();
    }
}

