package com.github.niefy.modules.biz.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizImageProduct;
import com.github.niefy.modules.biz.service.BizImageProductService;
import com.github.niefy.modules.oss.cloud.AbstractCloudStorageService;
import com.github.niefy.modules.oss.cloud.OSSFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        
        // 保存成功后，重新查询记录以确保返回最新数据
        BizImageProduct savedProduct = bizImageProductService.getById(bizImageProduct.getProductId());
        return R.ok().put("bizImageProduct", savedProduct);
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
        // 参数校验
        if (bizImageProduct.getProductId() == null || bizImageProduct.getProductId().isEmpty()) {
            logger.error("更新失败，productId不能为空");
            return R.error("productId不能为空");
        }
        
        // 检查记录是否存在
        BizImageProduct existingProduct = bizImageProductService.getById(bizImageProduct.getProductId());
        if (existingProduct == null) {
            logger.warn("产品图片不存在，无法更新，productId: {}", bizImageProduct.getProductId());
            return R.error("产品图片不存在，无法更新");
        }
        
        // 检查记录是否已删除
        if (existingProduct.getDeleted() != null && existingProduct.getDeleted() == 1) {
            logger.warn("产品图片已删除，无法更新，productId: {}", bizImageProduct.getProductId());
            return R.error("产品图片已删除，无法更新");
        }
        
        // 保存 generatedImages 的原始值（如果存在），稍后单独处理
        String originalGeneratedImages = bizImageProduct.getGeneratedImages();
        // 临时清空 generatedImages，先更新其他字段
        bizImageProduct.setGeneratedImages(null);
        
        // 先执行更新（更新除 generatedImages 外的其他字段）
        bizImageProduct.setUpdateTime(new Date());
        boolean success = bizImageProductService.updateById(bizImageProduct);
        
        if (!success) {
            logger.error("更新产品图片失败，productId: {}", bizImageProduct.getProductId());
            return R.error("更新失败");
        }
        
        logger.info("成功更新产品图片基本信息，productId: {}", bizImageProduct.getProductId());
        
        // 如果传入了 generatedImages，需要将 dreamina URL 转换为 OSS URL
        if (originalGeneratedImages != null && !originalGeneratedImages.isEmpty()) {
            try {
                // generatedImages 已经被 JsonArrayToStringDeserializer 转换为 JSON 字符串
                JSONArray imagesArray = JSON.parseArray(originalGeneratedImages);
                if (imagesArray != null && imagesArray.size() > 0) {
                    List<String> ossUrls = new ArrayList<>();
                    
                    // 遍历每个 URL，下载并上传到 OSS
                    for (int i = 0; i < imagesArray.size(); i++) {
                        String dreaminaUrl = imagesArray.getString(i);
                        if (dreaminaUrl != null && !dreaminaUrl.isEmpty()) {
                            try {
                                String ossUrl = downloadAndUploadToOSS(dreaminaUrl, bizImageProduct.getProductId(), i);
                                if (ossUrl != null) {
                                    ossUrls.add(ossUrl);
                                    logger.info("成功转换图片 URL {} -> {}", dreaminaUrl, ossUrl);
                                } else {
                                    logger.warn("转换图片 URL 失败，保留原始 URL: {}", dreaminaUrl);
                                    ossUrls.add(dreaminaUrl); // 转换失败时保留原始 URL
                                }
                            } catch (Exception e) {
                                logger.error("转换图片 URL 时发生异常: {}", dreaminaUrl, e);
                                ossUrls.add(dreaminaUrl); // 异常时保留原始 URL
                            }
                        }
                    }
                    
                    // 更新 generatedImages 字段为 OSS URL 列表
                    String ossUrlsJson = JSON.toJSONString(ossUrls);
                    BizImageProduct updateProduct = new BizImageProduct();
                    updateProduct.setProductId(bizImageProduct.getProductId());
                    updateProduct.setGeneratedImages(ossUrlsJson);
                    updateProduct.setGeneratedImageCount(ossUrls.size());
                    updateProduct.setUpdateTime(new Date());
                    
                    boolean updateSuccess = bizImageProductService.updateById(updateProduct);
                    if (updateSuccess) {
                        logger.info("成功更新 generatedImages，共 {} 个图片，productId: {}", ossUrls.size(), bizImageProduct.getProductId());
                    } else {
                        logger.error("更新 generatedImages 失败，productId: {}", bizImageProduct.getProductId());
                        return R.error("更新 generatedImages 失败");
                    }
                }
            } catch (Exception e) {
                logger.error("处理 generatedImages 时发生异常，productId: {}", bizImageProduct.getProductId(), e);
                return R.error("处理 generatedImages 失败: " + e.getMessage());
            }
        }
        
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

    /**
     * 提交图生图反馈
     * 
     * 请求参数：
     * - productId: 产品ID（必填）
     * - feedbackType: 反馈类型（必填），positive-正反馈，negative-负反馈
     */
    @PostMapping("/feedback")
    // @RequiresPermissions("biz:bizimageproduct:feedback")
    @ApiOperation(value = "提交图生图反馈")
    public R feedback(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String productId = (String) params.get("productId");
            String feedbackType = (String) params.get("feedbackType");
            
            // 参数校验
            if (productId == null || productId.isEmpty()) {
                logger.error("提交反馈失败，productId不能为空");
                return R.error("productId不能为空");
            }
            
            if (feedbackType == null || feedbackType.isEmpty()) {
                logger.error("提交反馈失败，feedbackType不能为空");
                return R.error("feedbackType不能为空");
            }
            
            // 验证反馈类型
            if (!"positive".equals(feedbackType) && !"negative".equals(feedbackType)) {
                logger.error("提交反馈失败，feedbackType值无效: {}", feedbackType);
                return R.error("feedbackType值无效，必须是positive或negative");
            }
            
            // 查询产品是否存在
            BizImageProduct product = bizImageProductService.getById(productId);
            if (product == null) {
                logger.warn("产品不存在，productId: {}", productId);
                return R.error("产品不存在");
            }
            
            // 检查产品是否已删除
            if (product.getDeleted() != null && product.getDeleted() == 1) {
                logger.warn("产品已删除，无法提交反馈，productId: {}", productId);
                return R.error("产品已删除，无法提交反馈");
            }
            
            // 更新反馈字段
            product.setGenerateFeedback(feedbackType);
            product.setUpdateTime(new Date());
            
            boolean success = bizImageProductService.updateById(product);
            
            if (success) {
                logger.info("成功提交反馈，productId: {}, feedbackType: {}", productId, feedbackType);
                return R.ok();
            } else {
                logger.error("提交反馈失败，productId: {}, feedbackType: {}", productId, feedbackType);
                return R.error("提交反馈失败");
            }
        } catch (Exception e) {
            logger.error("提交反馈失败", e);
            return R.error("提交反馈失败: " + e.getMessage());
        }
    }

    /**
     * 从 URL 下载图片并上传到 OSS
     * 
     * @param imageUrl 图片 URL
     * @param productId 产品 ID（用于生成文件名）
     * @param index 图片索引（用于区分多张图片）
     * @return OSS URL，失败返回 null
     */
    private String downloadAndUploadToOSS(String imageUrl, String productId, int index) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        HttpURLConnection connection = null;
        
        try {
            // 下载图片
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000); // 10秒连接超时
            connection.setReadTimeout(30000); // 30秒读取超时
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                logger.warn("下载图片失败，HTTP状态码: {}, URL: {}", responseCode, imageUrl);
                return null;
            }
            
            // 读取图片数据
            inputStream = connection.getInputStream();
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageData = outputStream.toByteArray();
            
            // 确定文件扩展名
            String suffix = ".png"; // 默认 PNG
            String contentType = connection.getContentType();
            if (contentType != null) {
                if (contentType.contains("jpeg") || contentType.contains("jpg")) {
                    suffix = ".jpg";
                } else if (contentType.contains("png")) {
                    suffix = ".png";
                } else if (contentType.contains("gif")) {
                    suffix = ".gif";
                } else if (contentType.contains("webp")) {
                    suffix = ".webp";
                }
            } else {
                // 从 URL 中提取扩展名
                int lastDotIndex = imageUrl.lastIndexOf('.');
                int lastSlashIndex = imageUrl.lastIndexOf('/');
                if (lastDotIndex > lastSlashIndex && lastDotIndex > 0) {
                    String urlSuffix = imageUrl.substring(lastDotIndex);
                    if (urlSuffix.length() <= 5) { // 扩展名通常不超过5个字符
                        suffix = urlSuffix.split("\\?")[0]; // 去除查询参数
                    }
                }
            }
            
            // 上传到 OSS（使用 uploadSuffix 方法，会自动生成路径）
            AbstractCloudStorageService ossService = OSSFactory.build();
            if (ossService == null) {
                logger.error("OSS 服务未配置");
                return null;
            }
            
            String ossUrl = ossService.uploadSuffix(imageData, suffix);
            logger.info("成功上传图片到 OSS: {}", ossUrl);
            
            return ossUrl;
            
        } catch (Exception e) {
            logger.error("下载并上传图片到 OSS 失败，URL: {}", imageUrl, e);
            return null;
        } finally {
            // 关闭资源
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                logger.warn("关闭资源时发生异常", e);
            }
        }
    }
}

