package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizImageProduct;
import com.github.niefy.modules.biz.service.BizImageProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 产品图片表-管理后台
 *
 * @author niefy
 * @date 2025-01-06
 */
@RestController
@RequestMapping("/manage/bizImageProduct")
@Api(tags = {"产品图片表-管理后台"})
public class BizImageProductManageController {
    private static final Logger logger = LoggerFactory.getLogger(BizImageProductManageController.class);

    @Autowired
    private BizImageProductService bizImageProductService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizimageproduct:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizImageProductService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{productId}")
    // @RequiresPermissions("biz:bizimageproduct:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("productId") String productId) {
        BizImageProduct bizImageProduct = bizImageProductService.getById(productId);
        return R.ok().put("bizImageProduct", bizImageProduct);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizimageproduct:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizImageProduct bizImageProduct) {
        // 设置默认值
        if (bizImageProduct.getProductId() == null || bizImageProduct.getProductId().isEmpty()) {
            bizImageProduct.setProductId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (bizImageProduct.getDeleted() == null) {
            bizImageProduct.setDeleted(0);
        }
        if (bizImageProduct.getCreateTime() == null) {
            bizImageProduct.setCreateTime(new Date());
        }
        bizImageProduct.setUpdateTime(new Date());
        
        bizImageProductService.save(bizImageProduct);
        return R.ok();
    }

    /**
     * 保存产品图片（包括文件上传）
     * 
     * 请求示例（multipart/form-data）:
     * 
     * 产品基本信息字段：
     * - templateId: "tpl_001_20240115103000" (必填，关联模板ID)
     * - dishName: "宫保鸡丁" (必填)
     * - dishCategory: "炒菜" (可选)
     * - priceDisplay: "有价格" (可选)
     * - productType: "单产品" (可选)
     * - price: "38.00" (可选)
     * - marketingTheme: "新春特惠" (可选)
     * 
     * 产品图片文件（可选）：
     * - productImageFile: 产品图片文件 (JPG/PNG)
     */
    @PostMapping("/saveWithFile")
    // @RequiresPermissions("biz:bizimageproduct:save")
    @ApiOperation(value = "保存产品图片（含文件上传）")
    public R saveWithFile(
            @ModelAttribute BizImageProduct bizImageProduct,
            @RequestParam(required = false) MultipartFile productImageFile
    ) {
        try {
            bizImageProductService.saveWithFile(bizImageProduct, productImageFile);
            return R.ok();
        } catch (Exception e) {
            logger.error("保存产品图片失败", e);
            return R.error("保存产品图片失败: " + e.getMessage());
        }
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizimageproduct:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizImageProduct bizImageProduct) {
        bizImageProduct.setUpdateTime(new Date());
        bizImageProductService.updateById(bizImageProduct);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizimageproduct:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] productIds) {
        bizImageProductService.removeByIds(Arrays.asList(productIds));
        return R.ok();
    }
}

