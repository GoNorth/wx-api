package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizImageTemplate;
import com.github.niefy.modules.biz.enums.ImageTemplateStatusEnum;
import com.github.niefy.modules.biz.service.BizImageTemplateService;
import com.github.niefy.modules.biz.vector.VectorSyncService;
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
        
        bizImageTemplateService.save(bizImageTemplate);
        
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

