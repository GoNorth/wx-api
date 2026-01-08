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
        logger.info("获取产品图片详情，productId: {}", productId);
        BizImageProduct bizImageProduct = bizImageProductService.getById(productId);
        if (bizImageProduct == null) {
            logger.warn("产品图片不存在，productId: {}", productId);
            return R.error("产品图片不存在");
        }
        logger.info("成功获取产品图片详情，productId: {}", productId);
        return R.ok().put("bizImageProduct", bizImageProduct);
    }

    /**
     * 保存（新增或更新）
     * 如果 productId 存在则更新，不存在则新增
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizimageproduct:save")
    @ApiOperation(value = "保存（新增或更新）")
    public R save(@RequestBody BizImageProduct bizImageProduct) {
        // 判断是新增还是更新
        boolean isNew = (bizImageProduct.getProductId() == null || bizImageProduct.getProductId().isEmpty());
        
        // 设置默认值
        if (isNew) {
            bizImageProduct.setProductId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (bizImageProduct.getDeleted() == null) {
            bizImageProduct.setDeleted(0);
        }
        if (isNew && bizImageProduct.getCreateTime() == null) {
            bizImageProduct.setCreateTime(new Date());
        }
        bizImageProduct.setUpdateTime(new Date());
        
        // 使用 saveOrUpdate 方法，自动判断是新增还是更新
        bizImageProductService.saveOrUpdate(bizImageProduct);
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
     * 删除（逻辑删除）
     * 支持两种格式：
     * 1. 数组格式：["id1", "id2", ...] - 批量删除
     * 2. 对象格式：{"productId": "id"} 或 {"t":..., "productId": "id"} - 单个删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizimageproduct:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody Object request) {
        try {
            // 如果是数组格式（批量删除）
            if (request instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<String> productIdsList = (java.util.List<String>) request;
                logger.info("批量删除产品图片，productIds: {}", productIdsList);
                for (String productId : productIdsList) {
                    BizImageProduct product = bizImageProductService.getById(productId);
                    if (product != null && (product.getDeleted() == null || product.getDeleted() == 0)) {
                        product.setDeleted(1);
                        product.setUpdateTime(new Date());
                        bizImageProductService.updateById(product);
                        logger.info("成功删除产品图片，productId: {}", productId);
                    } else {
                        logger.warn("产品图片不存在或已删除，productId: {}", productId);
                    }
                }
            } 
            // 如果是对象格式（单个删除）
            else if (request instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> requestMap = (Map<String, Object>) request;
                if (requestMap.containsKey("productId")) {
                    String productId = String.valueOf(requestMap.get("productId"));
                    logger.info("删除产品图片，productId: {}", productId);
                    
                    BizImageProduct product = bizImageProductService.getById(productId);
                    if (product == null) {
                        logger.warn("产品图片不存在，productId: {}", productId);
                        return R.error("产品图片不存在");
                    }
                    
                    if (product.getDeleted() != null && product.getDeleted() == 1) {
                        logger.warn("产品图片已删除，productId: {}", productId);
                        return R.error("产品图片已删除");
                    }
                    
                    // 逻辑删除：设置 deleted = 1
                    product.setDeleted(1);
                    product.setUpdateTime(new Date());
                    boolean success = bizImageProductService.updateById(product);
                    
                    if (success) {
                        logger.info("成功删除产品图片，productId: {}", productId);
                        return R.ok();
                    } else {
                        logger.error("删除产品图片失败，productId: {}", productId);
                        return R.error("删除失败");
                    }
                } else {
                    logger.error("删除失败，请求对象中未找到 productId 字段，request: {}", requestMap);
                    return R.error("删除失败：请求格式不正确，缺少 productId 字段");
                }
            } else {
                logger.error("删除失败，不支持的请求格式: {}", request.getClass().getName());
                return R.error("删除失败：不支持的请求格式");
            }
            return R.ok();
        } catch (Exception e) {
            logger.error("删除失败", e);
            return R.error("删除失败: " + e.getMessage());
        }
    }
}

