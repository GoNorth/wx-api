package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizStoreVi;
import com.github.niefy.modules.biz.service.BizStoreViService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 门店VI表-管理后台
 *
 * @author niefy
 * @date 2024-12-27
 */
@RestController
@RequestMapping("/manage/bizStoreVi")
@Api(tags = {"门店VI表-管理后台"})
public class BizStoreViManageController {
    @Autowired
    private BizStoreViService bizStoreViService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizstorevi:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizStoreViService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{viId}")
    // @RequiresPermissions("biz:bizstorevi:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("viId") String viId) {
        BizStoreVi bizStoreVi = bizStoreViService.getById(viId);
        return R.ok().put("bizStoreVi", bizStoreVi);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizstorevi:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizStoreVi bizStoreVi) {
        bizStoreViService.save(bizStoreVi);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizstorevi:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizStoreVi bizStoreVi) {
        bizStoreViService.updateById(bizStoreVi);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizstorevi:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] viIds) {
        bizStoreViService.removeByIds(Arrays.asList(viIds));
        return R.ok();
    }
}

