package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.PlatformUtils;
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
        if (bizPlanItem != null) {
            bizPlanItem.setPlatformDesc(PlatformUtils.getPlatformDesc(bizPlanItem.getPlatform()));
            bizPlanItem.setContentTagDesc(PlatformUtils.getContentTagDesc(bizPlanItem.getContentTag()));
        }
        return R.ok().put("bizPlanItem", bizPlanItem);
    }

    /**
     * 保存或更新计划项目
     * 根据是否有itemId判断是新增还是修改：
     * - 如果itemId为空或不存在，则新增
     * - 如果itemId存在且数据库中有记录，则更新（保留创建时间）
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizplanitem:save")
    @ApiOperation(value = "保存或更新计划项目")
    public R save(@RequestBody BizPlanItem bizPlanItem) {
        bizPlanItemService.saveOrUpdateItem(bizPlanItem);
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

