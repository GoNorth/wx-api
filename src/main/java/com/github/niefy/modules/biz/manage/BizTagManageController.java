package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizTag;
import com.github.niefy.modules.biz.service.BizTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 标签表-管理后台
 *
 * @author niefy
 * @date 2026-01-07
 */
@RestController
@RequestMapping("/manage/bizTag")
@Api(tags = {"标签表-管理后台"})
public class BizTagManageController {
    private static final Logger logger = LoggerFactory.getLogger(BizTagManageController.class);

    @Autowired
    private BizTagService bizTagService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:biztag:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizTagService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{tagId}")
    // @RequiresPermissions("biz:biztag:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("tagId") String tagId) {
        BizTag bizTag = bizTagService.getById(tagId);
        return R.ok().put("bizTag", bizTag);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:biztag:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizTag bizTag) {
        // 设置默认值
        if (bizTag.getTagId() == null || bizTag.getTagId().isEmpty()) {
            bizTag.setTagId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (bizTag.getTagCount() == null) {
            bizTag.setTagCount(0);
        }
        if (bizTag.getDeleted() == null) {
            bizTag.setDeleted(0);
        }
        if (bizTag.getCreateTime() == null) {
            bizTag.setCreateTime(new Date());
        }
        bizTag.setUpdateTime(new Date());
        
        bizTagService.save(bizTag);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:biztag:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizTag bizTag) {
        bizTag.setUpdateTime(new Date());
        bizTagService.updateById(bizTag);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:biztag:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] tagIds) {
        bizTagService.removeByIds(Arrays.asList(tagIds));
        return R.ok();
    }

    /**
     * 模糊搜索标签
     */
    @GetMapping("/search")
    @ApiOperation(value = "模糊搜索标签")
    public R search(@RequestParam(required = false) String tagName, @RequestParam(defaultValue = "10") Integer limit) {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("tagName", tagName);
        params.put("page", "1");
        params.put("limit", String.valueOf(limit));
        PageUtils page = new PageUtils(bizTagService.queryPage(params));
        return R.ok().put("list", page.getList());
    }
}

