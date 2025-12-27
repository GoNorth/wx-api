package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizStoreCharacter;
import com.github.niefy.modules.biz.service.BizStoreCharacterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 门店人物形象表-管理后台
 *
 * @author niefy
 * @date 2024-12-27
 */
@RestController
@RequestMapping("/manage/bizStoreCharacter")
@Api(tags = {"门店人物形象表-管理后台"})
public class BizStoreCharacterManageController {
    @Autowired
    private BizStoreCharacterService bizStoreCharacterService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizstorecharacter:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizStoreCharacterService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{characterId}")
    // @RequiresPermissions("biz:bizstorecharacter:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("characterId") String characterId) {
        BizStoreCharacter bizStoreCharacter = bizStoreCharacterService.getById(characterId);
        return R.ok().put("bizStoreCharacter", bizStoreCharacter);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizstorecharacter:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizStoreCharacter bizStoreCharacter) {
        bizStoreCharacterService.save(bizStoreCharacter);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizstorecharacter:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizStoreCharacter bizStoreCharacter) {
        bizStoreCharacterService.updateById(bizStoreCharacter);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizstorecharacter:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] characterIds) {
        bizStoreCharacterService.removeByIds(Arrays.asList(characterIds));
        return R.ok();
    }
}

