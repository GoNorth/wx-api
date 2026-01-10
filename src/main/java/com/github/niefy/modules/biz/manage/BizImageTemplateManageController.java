package com.github.niefy.modules.biz.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizImageTemplate;
import com.github.niefy.modules.biz.enums.ImageTemplateStatusEnum;
import com.github.niefy.modules.biz.service.BizImageTemplateService;
import com.github.niefy.modules.biz.utils.TemplateNoGenerator;
import com.github.niefy.modules.biz.vector.VectorSearchService;
import com.github.niefy.modules.biz.vector.VectorSyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 图片模板表-管理后台
 *
 * @author niefy
 * @date 2025-01-06
 */
@RestController
@RequestMapping("/manage/bizImageTemplate")
@Api(tags = {"图片模板表-管理后台"})
public class BizImageTemplateManageController {
    private static final Logger logger = LoggerFactory.getLogger(BizImageTemplateManageController.class);

    @Autowired
    private BizImageTemplateService bizImageTemplateService;

    @Autowired
    private VectorSyncService vectorSyncService;

    @Autowired
    private VectorSearchService vectorSearchService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizimagetemplate:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizImageTemplateService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 向量搜索接口（返回格式与list接口一致）
     * 例：GET /manage/bizImageTemplate/search?keyword=苹果&posterType=爆款招牌&page=1&limit=1
     */
    @GetMapping("/search")
    // @RequiresPermissions("biz:bizimagetemplate:list")
    @ApiOperation(value = "向量搜索列表")
    public R search(@RequestParam Map<String, Object> params) {
        try {
            // 获取搜索参数
            String keyword = (String) params.get("keyword");
            String posterType = (String) params.get("posterType");
            
            // 获取分页参数
            int page = 1;
            int limit = 1;
            if (params.get("page") != null) {
                page = Integer.parseInt(params.get("page").toString());
            }
            if (params.get("limit") != null) {
                limit = Integer.parseInt(params.get("limit").toString());
            }
            
            // 参数校验
            if (keyword == null || keyword.trim().isEmpty()) {
                return R.error("keyword参数不能为空");
            }
            
            // 调用向量搜索服务，获取更多结果以便分页（最多1000条）
            int searchLimit = Math.max(limit * page, 1000); // 至少获取当前页所需的数据量
            List<Map<String, Object>> searchResults = vectorSearchService.search(keyword.trim(), searchLimit, posterType);
            
            if (searchResults == null || searchResults.isEmpty()) {
                // 没有搜索结果，返回空的分页结果
                PageUtils pageUtils = new PageUtils(Collections.emptyList(), 0, limit, page);
                return R.ok().put("page", pageUtils);
            }
            
            // 创建template_id到score的映射，用于后续添加相似度分数
            Map<String, Double> scoreMap = searchResults.stream()
                    .filter(result -> result.get("template_id") != null && result.get("score") != null)
                    .collect(Collectors.toMap(
                            result -> (String) result.get("template_id"),
                            result -> {
                                Object score = result.get("score");
                                if (score instanceof Double) {
                                    return (Double) score;
                                } else if (score instanceof Number) {
                                    return ((Number) score).doubleValue();
                                }
                                return 0.0;
                            },
                            (v1, v2) -> v1 // 如果有重复的template_id，保留第一个score
                    ));
            
            // 提取template_id列表
            List<String> templateIds = searchResults.stream()
                    .map(result -> (String) result.get("template_id"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            
            // 手动分页：计算起始索引和结束索引
            int totalCount = templateIds.size();
            int startIndex = (page - 1) * limit;
            int endIndex = Math.min(startIndex + limit, totalCount);
            
            // 获取当前页的template_id列表
            List<String> pageTemplateIds;
            if (startIndex >= totalCount) {
                pageTemplateIds = Collections.emptyList();
            } else {
                pageTemplateIds = templateIds.subList(startIndex, endIndex);
            }
            
            // 根据template_id列表批量查询完整的BizImageTemplate对象
            List<Map<String, Object>> resultList;
            if (pageTemplateIds.isEmpty()) {
                resultList = Collections.emptyList();
            } else {
                List<BizImageTemplate> templateList = bizImageTemplateService.listByIds(pageTemplateIds);
                // 保持搜索结果的顺序（按score排序）
                Map<String, BizImageTemplate> templateMap = templateList.stream()
                        .collect(Collectors.toMap(BizImageTemplate::getTemplateId, t -> t));
                
                // 将BizImageTemplate对象转换为Map，并添加相似度分数
                resultList = pageTemplateIds.stream()
                        .map(templateId -> {
                            BizImageTemplate template = templateMap.get(templateId);
                            if (template == null) {
                                return null;
                            }
                            // 将BizImageTemplate转换为Map
                            JSONObject resultMap = JSON.parseObject(JSON.toJSONString(template));
                            // 添加相似度分数（保留2位小数）
                            Double score = scoreMap.get(templateId);
                            if (score != null) {
                                // 保留2位小数
                                double roundedScore = Math.round(score * 100.0) / 100.0;
                                resultMap.put("score", roundedScore);
                            }
                            return resultMap;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            
            // 封装成PageUtils格式返回
            PageUtils pageUtils = new PageUtils(resultList, totalCount, limit, page);
            return R.ok().put("page", pageUtils);
        } catch (Exception e) {
            logger.error("向量搜索失败", e);
            return R.error("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 信息
     */
    @GetMapping("/info/{templateId}")
    // @RequiresPermissions("biz:bizimagetemplate:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("templateId") String templateId) {
        BizImageTemplate bizImageTemplate = bizImageTemplateService.getById(templateId);
        return R.ok().put("bizImageTemplate", bizImageTemplate);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizimagetemplate:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizImageTemplate bizImageTemplate) {
        // 设置默认值
        if (bizImageTemplate.getTemplateId() == null || bizImageTemplate.getTemplateId().isEmpty()) {
            bizImageTemplate.setTemplateId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (bizImageTemplate.getStatus() == null || bizImageTemplate.getStatus().isEmpty()) {
            bizImageTemplate.setStatus(ImageTemplateStatusEnum.INIT.getValue());
        }
        if (bizImageTemplate.getDeleted() == null) {
            bizImageTemplate.setDeleted(0);
        }
        if (bizImageTemplate.getCreateTime() == null) {
            bizImageTemplate.setCreateTime(new Date());
        }
        bizImageTemplate.setUpdateTime(new Date());
        
        // 生成模板编号（如果未设置）
        if (bizImageTemplate.getTemplateNo() == null || bizImageTemplate.getTemplateNo().isEmpty()) {
            bizImageTemplate.setTemplateNo(TemplateNoGenerator.generateTemplateNo(
                    bizImageTemplate.getPosterType(),
                    bizImageTemplate.getCreateTime()
            ));
        }
        
        // 使用 saveOrUpdate 方法，自动判断是新增还是更新
        bizImageTemplateService.saveOrUpdate(bizImageTemplate);
        
        // 保存成功后，更新向量数据
        updateEmbeddingAsync(bizImageTemplate.getTemplateId());
        
        return R.ok();
    }

    /**
     * 保存图片模板（包括文件上传）
     * 
     * 请求示例（multipart/form-data）:
     * 
     * 模板基本信息字段：
     * - posterType: "爆款招牌" (可选)
     * - templateImageDesc: "模板图片描述" (可选)
     * - recognitionPrompt: "识别提示词" (可选)
     * 
     * 模板图片文件（可选）：
     * - templateImageFile: 模板图片文件 (JPG/PNG)
     */
    @PostMapping("/saveWithFile")
    // @RequiresPermissions("biz:bizimagetemplate:save")
    @ApiOperation(value = "保存图片模板（含文件上传）")
    public R saveWithFile(
            @ModelAttribute BizImageTemplate bizImageTemplate,
            @RequestParam(required = false) MultipartFile templateImageFile
    ) {
        try {
            bizImageTemplateService.saveWithFile(bizImageTemplate, templateImageFile);
            
            // 保存成功后，更新向量数据
            updateEmbeddingAsync(bizImageTemplate.getTemplateId());
            
            return R.ok();
        } catch (Exception e) {
            logger.error("保存图片模板失败", e);
            return R.error("保存图片模板失败: " + e.getMessage());
        }
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizimagetemplate:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizImageTemplate bizImageTemplate) {
        bizImageTemplate.setUpdateTime(new Date());
        bizImageTemplateService.updateById(bizImageTemplate);
        
        // 更新成功后，更新向量数据
        updateEmbeddingAsync(bizImageTemplate.getTemplateId());
        
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizimagetemplate:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] templateIds) {
        bizImageTemplateService.removeByIds(Arrays.asList(templateIds));
        
        // 删除成功后，清空向量数据（逻辑删除，清空向量以便搜索时排除）
        for (String templateId : templateIds) {
            clearEmbeddingAsync(templateId);
        }
        
        return R.ok();
    }

    /**
     * 异步更新向量数据（不阻塞主流程）
     * @param templateId 模板ID
     */
    private void updateEmbeddingAsync(String templateId) {
        if (templateId == null || templateId.isEmpty()) {
            return;
        }
        
        // 使用新线程异步更新向量，避免阻塞主流程
        new Thread(() -> {
            try {
                vectorSyncService.refreshEmbedding(templateId);
                logger.info("模板向量更新成功，templateId: {}", templateId);
            } catch (Exception e) {
                logger.error("模板向量更新失败，templateId: " + templateId, e);
            }
        }).start();
    }

    /**
     * 异步清空向量数据（不阻塞主流程）
     * @param templateId 模板ID
     */
    private void clearEmbeddingAsync(String templateId) {
        if (templateId == null || templateId.isEmpty()) {
            return;
        }
        
        // 使用新线程异步清空向量，避免阻塞主流程
        new Thread(() -> {
            try {
                vectorSyncService.clearEmbedding(templateId);
                logger.info("模板向量清空成功，templateId: {}", templateId);
            } catch (Exception e) {
                logger.error("模板向量清空失败，templateId: " + templateId, e);
            }
        }).start();
    }
}

