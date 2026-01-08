package com.github.niefy.modules.biz.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizImageTemplate;
import com.github.niefy.modules.biz.entity.BizTag;
import com.github.niefy.modules.biz.entity.BizTagMap;
import com.github.niefy.modules.biz.service.BizImageTemplateService;
import com.github.niefy.modules.biz.service.BizTagMapService;
import com.github.niefy.modules.biz.service.BizTagService;
import com.github.niefy.modules.biz.vector.VectorSearchService;
import com.github.niefy.modules.biz.vector.VectorSyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签映射表-管理后台
 *
 * @author niefy
 * @date 2026-01-07
 */
@RestController
@RequestMapping("/manage/bizTagMap")
@Api(tags = {"标签映射表-管理后台"})
public class BizTagMapManageController {
    private static final Logger logger = LoggerFactory.getLogger(BizTagMapManageController.class);

    @Autowired
    private BizTagMapService bizTagMapService;

    @Autowired
    private BizTagService bizTagService;

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
    // @RequiresPermissions("biz:biztagmap:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizTagMapService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{mapId}")
    // @RequiresPermissions("biz:biztagmap:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("mapId") String mapId) {
        BizTagMap bizTagMap = bizTagMapService.getById(mapId);
        return R.ok().put("bizTagMap", bizTagMap);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:biztagmap:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizTagMap bizTagMap) {
        // 设置默认值
        if (bizTagMap.getMapId() == null || bizTagMap.getMapId().isEmpty()) {
            bizTagMap.setMapId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (bizTagMap.getDeleted() == null) {
            bizTagMap.setDeleted(0);
        }
        if (bizTagMap.getCreateTime() == null) {
            bizTagMap.setCreateTime(new Date());
        }
        bizTagMap.setUpdateTime(new Date());
        
        bizTagMapService.save(bizTagMap);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:biztagmap:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizTagMap bizTagMap) {
        bizTagMap.setUpdateTime(new Date());
        bizTagMapService.updateById(bizTagMap);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:biztagmap:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] mapIds) {
        bizTagMapService.removeByIds(Arrays.asList(mapIds));
        return R.ok();
    }

    /**
     * 根据模板ID获取已绑定的标签列表
     */
    @GetMapping("/tagsByTemplate/{templateId}")
    @ApiOperation(value = "根据模板ID获取已绑定的标签列表")
    public R getTagsByTemplate(@PathVariable("templateId") String templateId) {
        if (!StringUtils.hasText(templateId)) {
            return R.error("模板ID不能为空");
        }
        
        // 查询该模板的所有标签映射
        QueryWrapper<BizTagMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("template_id", templateId).eq("deleted", 0);
        List<BizTagMap> tagMaps = bizTagMapService.list(queryWrapper);
        
        if (tagMaps.isEmpty()) {
            return R.ok().put("list", Collections.emptyList());
        }
        
        // 获取所有标签ID
        List<String> tagIds = tagMaps.stream()
                .map(BizTagMap::getTagId)
                .collect(Collectors.toList());
        
        // 查询标签详情
        List<BizTag> tags = bizTagService.listByIds(tagIds);
        
        return R.ok().put("list", tags);
    }

    /**
     * 批量绑定标签到模板（如果标签不存在则自动创建）
     */
    @PostMapping("/bindTagsToTemplate")
    @ApiOperation(value = "批量绑定标签到模板")
    public R bindTagsToTemplate(@RequestBody Map<String, Object> params) {
        String templateId = (String) params.get("templateId");
        @SuppressWarnings("unchecked")
        List<String> tagNames = (List<String>) params.get("tagNames");
        
        if (!StringUtils.hasText(templateId)) {
            return R.error("模板ID不能为空");
        }
        if (tagNames == null || tagNames.isEmpty()) {
            return R.error("标签名称列表不能为空");
        }
        
        Date now = new Date();
        List<BizTagMap> tagMapsToSave = new ArrayList<>();
        
        for (String tagName : tagNames) {
            if (!StringUtils.hasText(tagName)) {
                continue;
            }
            
            tagName = tagName.trim();
            
            // 查找或创建标签
            BizTag tag = null;
            QueryWrapper<BizTag> tagQuery = new QueryWrapper<>();
            tagQuery.eq("tag_name", tagName).eq("deleted", 0);
            List<BizTag> existingTags = bizTagService.list(tagQuery);
            
            if (!existingTags.isEmpty()) {
                tag = existingTags.get(0);
            } else {
                // 创建新标签
                tag = new BizTag();
                tag.setTagId(UUID.randomUUID().toString().replace("-", ""));
                tag.setTagName(tagName);
                tag.setTagCount(0);
                tag.setDeleted(0);
                tag.setCreateTime(now);
                tag.setUpdateTime(now);
                bizTagService.save(tag);
            }
            
            // 检查是否已经绑定
            QueryWrapper<BizTagMap> mapQuery = new QueryWrapper<>();
            mapQuery.eq("tag_id", tag.getTagId())
                    .eq("template_id", templateId)
                    .eq("deleted", 0);
            BizTagMap existingMap = bizTagMapService.getOne(mapQuery);
            
            if (existingMap == null) {
                // 创建新的绑定关系
                BizTagMap tagMap = new BizTagMap();
                tagMap.setMapId(UUID.randomUUID().toString().replace("-", ""));
                tagMap.setTagId(tag.getTagId());
                tagMap.setTemplateId(templateId);
                tagMap.setDeleted(0);
                tagMap.setCreateTime(now);
                tagMap.setUpdateTime(now);
                tagMapsToSave.add(tagMap);
                
                // 更新标签的引用计数
                tag.setTagCount((tag.getTagCount() == null ? 0 : tag.getTagCount()) + 1);
                tag.setUpdateTime(now);
                bizTagService.updateById(tag);
            }
        }
        
        // 批量保存标签映射
        if (!tagMapsToSave.isEmpty()) {
            bizTagMapService.saveBatch(tagMapsToSave);
        }
        
        // 更新模板的tags字段
        updateTemplateTags(templateId);
        
        return R.ok();
    }

    /**
     * 解绑标签（从模板中移除标签）
     */
    @PostMapping("/unbindTagFromTemplate")
    @ApiOperation(value = "解绑标签")
    public R unbindTagFromTemplate(@RequestBody Map<String, Object> params) {
        String templateId = (String) params.get("templateId");
        String tagId = (String) params.get("tagId");
        
        if (!StringUtils.hasText(templateId)) {
            return R.error("模板ID不能为空");
        }
        if (!StringUtils.hasText(tagId)) {
            return R.error("标签ID不能为空");
        }
        
        // 查找绑定关系（只查找未删除的记录）
        QueryWrapper<BizTagMap> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tag_id", tagId)
                    .eq("template_id", templateId)
                    .eq("deleted", 0);
        BizTagMap tagMap = bizTagMapService.getOne(queryWrapper);
        
        if (tagMap != null) {
            // 在更新之前，先检查并删除已存在的 deleted=1 的记录（避免唯一约束冲突）
            QueryWrapper<BizTagMap> deletedQueryWrapper = new QueryWrapper<>();
            deletedQueryWrapper.eq("tag_id", tagId)
                              .eq("template_id", templateId)
                              .eq("deleted", 1);
            List<BizTagMap> deletedTagMaps = bizTagMapService.list(deletedQueryWrapper);
            if (!deletedTagMaps.isEmpty()) {
                // 物理删除已存在的 deleted=1 的记录（因为唯一约束，只能存在一条）
                List<String> deletedMapIds = deletedTagMaps.stream()
                        .map(BizTagMap::getMapId)
                        .collect(Collectors.toList());
                bizTagMapService.removeByIds(deletedMapIds);
                logger.info("删除已存在的deleted=1记录，mapIds: {}", deletedMapIds);
            }
            
            // 使用 UpdateWrapper 只更新 deleted 和 update_time 字段
            UpdateWrapper<BizTagMap> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("map_id", tagMap.getMapId())
                        .set("deleted", 1)
                        .set("update_time", new Date());
            bizTagMapService.update(updateWrapper);
            
            // 更新标签的引用计数
            BizTag tag = bizTagService.getById(tagId);
            if (tag != null && tag.getTagCount() != null && tag.getTagCount() > 0) {
                tag.setTagCount(tag.getTagCount() - 1);
                tag.setUpdateTime(new Date());
                bizTagService.updateById(tag);
            }
        } else {
            // 如果未找到未删除的记录，检查是否已经删除过
            QueryWrapper<BizTagMap> deletedQueryWrapper = new QueryWrapper<>();
            deletedQueryWrapper.eq("tag_id", tagId)
                              .eq("template_id", templateId)
                              .eq("deleted", 1);
            BizTagMap deletedTagMap = bizTagMapService.getOne(deletedQueryWrapper);
            if (deletedTagMap != null) {
                // 已经删除过了，直接返回成功
                logger.info("标签已解绑，tagId: {}, templateId: {}", tagId, templateId);
            } else {
                // 记录不存在，返回错误
                return R.error("未找到该标签绑定关系");
            }
        }
        
        // 更新模板的tags字段
        updateTemplateTags(templateId);
        
        return R.ok();
    }

    /**
     * 更新模板的tags字段（根据绑定的标签列表拼接）
     * @param templateId 模板ID
     */
    private void updateTemplateTags(String templateId) {
        try {
            // 查询该模板的所有标签映射
            QueryWrapper<BizTagMap> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("template_id", templateId).eq("deleted", 0);
            List<BizTagMap> tagMaps = bizTagMapService.list(queryWrapper);
            
            String tagsStr = "";
            if (!tagMaps.isEmpty()) {
                // 获取所有标签ID
                List<String> tagIds = tagMaps.stream()
                        .map(BizTagMap::getTagId)
                        .collect(Collectors.toList());
                
                // 查询标签详情
                List<BizTag> tags = bizTagService.listByIds(tagIds);
                
                // 拼接标签名称，用顿号分隔
                tagsStr = tags.stream()
                        .map(BizTag::getTagName)
                        .filter(name -> name != null && !name.trim().isEmpty())
                        .collect(Collectors.joining("、"));
            }
            
            // 更新模板的tags字段
            BizImageTemplate template = bizImageTemplateService.getById(templateId);
            if (template != null) {
                template.setTags(tagsStr);
                template.setUpdateTime(new Date());
                bizImageTemplateService.updateById(template);
                
                // 异步更新embedding_data和刷新缓存（tags字段变化会影响embedding）
                updateEmbeddingAndRefreshCacheAsync(templateId);
            }
        } catch (Exception e) {
            logger.error("更新模板tags字段失败，templateId: {}", templateId, e);
        }
    }

    /**
     * 异步更新embedding_data和刷新缓存（不阻塞主流程）
     * @param templateId 模板ID
     */
    private void updateEmbeddingAndRefreshCacheAsync(String templateId) {
        if (templateId == null || templateId.isEmpty()) {
            return;
        }
        
        // 使用新线程异步更新向量和刷新缓存，避免阻塞主流程
        new Thread(() -> {
            try {
                // 1. 更新embedding_data（因为tags字段变化会影响embedding）
                vectorSyncService.refreshEmbedding(templateId);
                logger.info("模板向量更新成功，templateId: {}", templateId);
                
                // 2. 刷新缓存，使搜索功能使用最新的向量数据
                vectorSearchService.reloadCache();
                logger.info("向量缓存刷新成功");
            } catch (Exception e) {
                logger.error("更新模板向量或刷新缓存失败，templateId: " + templateId, e);
            }
        }).start();
    }
}

