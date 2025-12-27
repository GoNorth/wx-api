package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizContentFeedback;
import com.github.niefy.modules.biz.service.BizContentFeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 内容反馈表-管理后台
 *
 * @author niefy
 * @date 2024-12-27
 */
@RestController
@RequestMapping("/manage/bizContentFeedback")
@Api(tags = {"内容反馈表-管理后台"})
public class BizContentFeedbackManageController {
    @Autowired
    private BizContentFeedbackService bizContentFeedbackService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizcontentfeedback:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizContentFeedbackService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{feedbackId}")
    // @RequiresPermissions("biz:bizcontentfeedback:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("feedbackId") String feedbackId) {
        BizContentFeedback bizContentFeedback = bizContentFeedbackService.getById(feedbackId);
        return R.ok().put("bizContentFeedback", bizContentFeedback);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizcontentfeedback:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizContentFeedback bizContentFeedback) {
        bizContentFeedbackService.save(bizContentFeedback);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizcontentfeedback:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizContentFeedback bizContentFeedback) {
        bizContentFeedbackService.updateById(bizContentFeedback);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizcontentfeedback:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] feedbackIds) {
        bizContentFeedbackService.removeByIds(Arrays.asList(feedbackIds));
        return R.ok();
    }
}

