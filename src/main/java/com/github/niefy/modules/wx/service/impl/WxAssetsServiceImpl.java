package com.github.niefy.modules.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.niefy.modules.wx.dao.WxMaterialMapper;
import com.github.niefy.modules.wx.dto.PageSizeConstant;
import com.github.niefy.modules.wx.entity.WxMaterial;
import com.github.niefy.modules.wx.service.WxAssetsService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import com.github.niefy.modules.wx.dto.TempMediaUploadResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.mp.bean.draft.*;
import me.chanjar.weixin.mp.bean.material.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@CacheConfig(cacheNames = {"wxAssetsServiceCache"})
@Slf4j
public class WxAssetsServiceImpl implements WxAssetsService {
    @Autowired
    WxMpService wxMpService;
    
    @Autowired
    WxMaterialMapper wxMaterialMapper;
    
    @Autowired(required = false)
    RestTemplate restTemplate;

    @Override
    @Cacheable(key="methodName+ #appid")
    public WxMpMaterialCountResult materialCount(String appid) throws WxErrorException {
        log.info("从API获取素材总量");
        wxMpService.switchoverTo(appid);
        return wxMpService.getMaterialService().materialCount();
    }

    @Override
    @Cacheable(key="methodName + #appid + #mediaId")
    public WxMpMaterialNews materialNewsInfo(String appid, String mediaId) throws WxErrorException {
        log.info("从API获取图文素材详情,mediaId={}",mediaId);
        wxMpService.switchoverTo(appid);
        return wxMpService.getMaterialService().materialNewsInfo(mediaId);
    }

    @Override
    @Cacheable(key="methodName + #appid + #type + #page")
    public WxMpMaterialFileBatchGetResult materialFileBatchGet(String appid, String type, int page) throws WxErrorException {
        log.info("从API获取媒体素材列表,type={},page={}",type,page);
        wxMpService.switchoverTo(appid);
        final int pageSize = PageSizeConstant.PAGE_SIZE_SMALL;
        int offset=(page-1)* pageSize;
        return wxMpService.getMaterialService().materialFileBatchGet(type,offset, pageSize);
    }

    @Cacheable(key="methodName + #appid + #page")
    @Override
    public WxMpMaterialNewsBatchGetResult materialNewsBatchGet(String appid, int page) throws WxErrorException {
        log.info("从API获取媒体素材列表,page={}",page);
        wxMpService.switchoverTo(appid);
        final int pageSize = PageSizeConstant.PAGE_SIZE_SMALL;
        int offset=(page-1)*pageSize;
        return wxMpService.getMaterialService().materialNewsBatchGet(offset, pageSize);
    }

    @Override
    @CacheEvict(allEntries = true)
    public WxMpMaterialUploadResult materialNewsUpload(String appid, List<WxMpDraftArticles> articles) throws WxErrorException {
        Assert.notEmpty(articles,"图文列表不得为空");
        log.info("上传图文素材...");
        wxMpService.switchoverTo(appid);
	WxMpAddDraft news = new WxMpAddDraft();
        news.setArticles(articles);
        String draftMediaId = wxMpService.getDraftService().addDraft(news);
        WxMpMaterialUploadResult result = new WxMpMaterialUploadResult();
        result.setMediaId(draftMediaId);
        result.setErrCode(0);
	return result;
    }

    /**
     * 更新图文素材中的某篇文章
     * @param appid
     * @param form
     */
    @Override
    @CacheEvict(allEntries = true)
    public void materialArticleUpdate(String appid, WxMpUpdateDraft form)  throws WxErrorException{
        log.info("更新图文素材...");
        wxMpService.switchoverTo(appid);
        wxMpService.getDraftService().updateDraft(form);
    }
    @Override
    @CacheEvict(allEntries = true)
    public WxMpMaterialUploadResult materialFileUpload(String appid, String mediaType, String fileName, MultipartFile file) throws WxErrorException, IOException {
        log.info("上传媒体素材：{}",fileName);
        wxMpService.switchoverTo(appid);
        String originalFilename=file.getOriginalFilename();
        File tempFile = File.createTempFile(fileName+"--", Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".")));
        file.transferTo(tempFile);
        WxMpMaterial wxMaterial = new WxMpMaterial();
        wxMaterial.setFile(tempFile);
        wxMaterial.setName(fileName);
        if(WxConsts.MediaFileType.VIDEO.equals(mediaType)){
            wxMaterial.setVideoTitle(fileName);
        }
        WxMpMaterialUploadResult res = wxMpService.getMaterialService().materialFileUpload(mediaType,wxMaterial);
        tempFile.deleteOnExit();
        
        // 同步保存到本地数据库
        if (res != null && res.getMediaId() != null) {
            try {
                WxMaterial material = new WxMaterial();
                material.setAppid(appid);
                material.setEnterpriseId(null); // 接口中为空
                material.setForDate(null); // 接口中为空
                material.setMediaStore("PERM"); // 根据接口决定：materialFileUpload是永久素材接口
                material.setMediaId(res.getMediaId());
                material.setMediaType(mediaType);
                material.setFileName(fileName);
                material.setUrl(res.getUrl()); // 图片和视频会有URL
                material.setCreateTime(new Date());
                
                // 检查是否已存在，避免重复插入
                LambdaQueryWrapper<WxMaterial> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(WxMaterial::getAppid, appid)
                       .eq(WxMaterial::getMediaId, res.getMediaId());
                WxMaterial existMaterial = wxMaterialMapper.selectOne(wrapper);
                
                if (existMaterial == null) {
                    wxMaterialMapper.insert(material);
                    log.info("素材已保存到本地数据库，mediaId={}", res.getMediaId());
                } else {
                    // 如果已存在，更新信息
                    material.setId(existMaterial.getId());
                    material.setUpdateTime(new Date());
                    wxMaterialMapper.updateById(material);
                    log.info("素材信息已更新到本地数据库，mediaId={}", res.getMediaId());
                }
            } catch (Exception e) {
                log.error("保存素材到本地数据库失败，mediaId={}", res.getMediaId(), e);
                // 不影响主流程，只记录错误日志
            }
        }
        
        return res;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean materialDelete(String appid, String mediaId) throws WxErrorException {
        log.info("删除素材，mediaId={}",mediaId);
        wxMpService.switchoverTo(appid);
        boolean result = wxMpService.getMaterialService().materialDelete(mediaId);
        
        // 同步删除本地数据库记录
        if (result) {
            try {
                LambdaQueryWrapper<WxMaterial> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(WxMaterial::getAppid, appid)
                       .eq(WxMaterial::getMediaId, mediaId);
                int deleteCount = wxMaterialMapper.delete(wrapper);
                if (deleteCount > 0) {
                    log.info("素材已从本地数据库删除，mediaId={}", mediaId);
                } else {
                    log.warn("本地数据库未找到对应素材记录，mediaId={}", mediaId);
                }
            } catch (Exception e) {
                log.error("从本地数据库删除素材失败，mediaId={}", mediaId, e);
                // 不影响主流程，只记录错误日志
            }
        }
        
        return result;
    }

    @Override
    @CacheEvict(allEntries = true)
    public TempMediaUploadResult tempMediaUpload(String appid, String mediaType, String fileName, MultipartFile file) throws WxErrorException, IOException {
        log.info("上传临时媒体素材：{}", fileName);
        wxMpService.switchoverTo(appid);
        String originalFilename = file.getOriginalFilename();
        File tempFile = File.createTempFile(fileName + "--", Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".")));
        file.transferTo(tempFile);
        
        // 获取access_token并构建上传URL
        String accessToken = wxMpService.getAccessToken();
        String url = String.format("https://api.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=%s", 
            accessToken, mediaType);
        
        // 使用RestTemplate上传文件
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("media", new org.springframework.core.io.FileSystemResource(tempFile));
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        
        String responseBody = response.getBody();
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        
        // 检查是否有错误
        if (jsonObject.has("errcode") && jsonObject.get("errcode").getAsInt() != 0) {
            throw new WxErrorException(WxError.fromJson(responseBody));
        }
        
        // 转换为自定义结果类型
        TempMediaUploadResult res = new TempMediaUploadResult();
        res.setType(jsonObject.has("type") ? jsonObject.get("type").getAsString() : mediaType);
        res.setMediaId(jsonObject.get("media_id").getAsString());
        res.setCreatedAt(jsonObject.has("created_at") ? jsonObject.get("created_at").getAsLong() : System.currentTimeMillis() / 1000);
        
        // 同步保存到本地数据库
        if (res != null && res.getMediaId() != null) {
            try {
                WxMaterial material = new WxMaterial();
                material.setAppid(appid);
                material.setEnterpriseId(null); // 接口中为空
                material.setForDate(null); // 接口中为空
                material.setMediaStore("TEMP"); // 临时素材
                material.setMediaId(res.getMediaId());
                material.setMediaType(mediaType);
                material.setFileName(fileName);
                material.setUrl(null); // 临时素材没有URL
                material.setCreateTime(new Date());
                
                // 检查是否已存在，避免重复插入
                LambdaQueryWrapper<WxMaterial> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(WxMaterial::getAppid, appid)
                       .eq(WxMaterial::getMediaId, res.getMediaId());
                WxMaterial existMaterial = wxMaterialMapper.selectOne(wrapper);
                
                if (existMaterial == null) {
                    wxMaterialMapper.insert(material);
                    log.info("临时素材已保存到本地数据库，mediaId={}", res.getMediaId());
                } else {
                    // 如果已存在，更新信息
                    material.setId(existMaterial.getId());
                    material.setUpdateTime(new Date());
                    wxMaterialMapper.updateById(material);
                    log.info("临时素材信息已更新到本地数据库，mediaId={}", res.getMediaId());
                }
            } catch (Exception e) {
                log.error("保存临时素材到本地数据库失败，mediaId={}", res.getMediaId(), e);
                // 不影响主流程，只记录错误日志
            }
        }
        
        tempFile.deleteOnExit();
        return res;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean tempMediaDelete(String appid, String mediaId) {
        log.info("删除临时素材（仅删除本地数据库记录），mediaId={}", mediaId);
        
        // 临时素材在微信端3天后自动失效，不需要调用微信API删除
        // 只删除本地数据库记录
        try {
            LambdaQueryWrapper<WxMaterial> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WxMaterial::getAppid, appid)
                   .eq(WxMaterial::getMediaId, mediaId)
                   .eq(WxMaterial::getMediaStore, "TEMP"); // 只删除临时素材记录
            int deleteCount = wxMaterialMapper.delete(wrapper);
            if (deleteCount > 0) {
                log.info("临时素材已从本地数据库删除，mediaId={}", mediaId);
                return true;
            } else {
                log.warn("本地数据库未找到对应临时素材记录，mediaId={}", mediaId);
                return false;
            }
        } catch (Exception e) {
            log.error("从本地数据库删除临时素材失败，mediaId={}", mediaId, e);
            return false;
        }
    }
}
