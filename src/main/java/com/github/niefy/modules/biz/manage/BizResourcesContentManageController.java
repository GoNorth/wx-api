package com.github.niefy.modules.biz.manage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizResourcesContent;
import com.github.niefy.modules.biz.entity.BizStore;
import com.github.niefy.modules.biz.service.BizResourcesContentService;
import com.github.niefy.modules.biz.service.BizStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * 资源(图片视频)内容表-管理后台
 *
 * @author niefy
 * @date 2024-12-27
 */
@RestController
@RequestMapping("/manage/bizResourcesContent")
@Api(tags = {"资源(图片视频)内容表-管理后台"})
public class BizResourcesContentManageController {
    @Autowired
    private BizResourcesContentService bizResourcesContentService;
    
    @Autowired
    private BizStoreService bizStoreService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizresourcescontent:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        // 从Header中获取wx_openid参数
        String wxOpenid = request.getHeader("wx_openid");
        
        // 如果提供了wx_openid，则通过它查询对应的门店，然后过滤资源内容
        if (StringUtils.hasText(wxOpenid)) {
            // 通过ownerOpenid查询门店
            BizStore bizStore = bizStoreService.getOne(
                new LambdaQueryWrapper<BizStore>()
                    .eq(BizStore::getOwnerOpenid, wxOpenid)
                    .eq(BizStore::getDeleted, 0)
            );
            
            // 如果找到了门店，将storeId添加到查询参数中
            if (bizStore != null && StringUtils.hasText(bizStore.getStoreId())) {
                params.put("storeId", bizStore.getStoreId());
            } else {
                // 如果没有找到门店，返回空结果（通过设置一个不存在的storeId来确保查询不到数据）
                params.put("storeId", "__NO_STORE_FOUND__");
            }
        }
        
        PageUtils page = new PageUtils(bizResourcesContentService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{contentId}")
    // @RequiresPermissions("biz:bizresourcescontent:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("contentId") String contentId) {
        BizResourcesContent bizResourcesContent = bizResourcesContentService.getById(contentId);
        return R.ok().put("bizResourcesContent", bizResourcesContent);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizresourcescontent:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizResourcesContent bizResourcesContent) {
        bizResourcesContentService.save(bizResourcesContent);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizresourcescontent:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizResourcesContent bizResourcesContent) {
        bizResourcesContentService.updateById(bizResourcesContent);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizresourcescontent:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] contentIds) {
        bizResourcesContentService.removeByIds(Arrays.asList(contentIds));
        return R.ok();
    }
}

