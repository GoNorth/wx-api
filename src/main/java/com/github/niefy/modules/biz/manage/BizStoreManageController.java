package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizStore;
import com.github.niefy.modules.biz.service.BizStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 门店表-管理后台
 *
 * @author niefy
 * @date 2024-12-27
 */
@RestController
@RequestMapping("/manage/bizStore")
@Api(tags = {"门店表-管理后台"})
public class BizStoreManageController {
    @Autowired
    private BizStoreService bizStoreService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizstore:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizStoreService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{storeId}")
    // @RequiresPermissions("biz:bizstore:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("storeId") String storeId) {
        BizStore bizStore = bizStoreService.getById(storeId);
        return R.ok().put("bizStore", bizStore);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizstore:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizStore bizStore) {
        bizStoreService.save(bizStore);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizstore:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizStore bizStore) {
        bizStoreService.updateById(bizStore);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizstore:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] storeIds) {
        bizStoreService.removeByIds(Arrays.asList(storeIds));
        return R.ok();
    }
}

