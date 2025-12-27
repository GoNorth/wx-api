package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizStoreMapper;
import com.github.niefy.modules.biz.entity.BizStore;
import com.github.niefy.modules.biz.entity.BizStoreCharacter;
import com.github.niefy.modules.biz.entity.BizStoreVi;
import com.github.niefy.modules.biz.service.BizStoreCharacterService;
import com.github.niefy.modules.biz.service.BizStoreService;
import com.github.niefy.modules.biz.service.BizStoreViService;
import com.github.niefy.modules.oss.cloud.OSSFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 门店表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Service
public class BizStoreServiceImpl extends ServiceImpl<BizStoreMapper, BizStore> implements BizStoreService {

    @Autowired
    private BizStoreViService bizStoreViService;

    @Autowired
    private BizStoreCharacterService bizStoreCharacterService;

    @Override
    public IPage<BizStore> queryPage(Map<String, Object> params) {
        String storeId = (String) params.get("storeId");
        String ownerOpenid = (String) params.get("ownerOpenid");
        String ownerName = (String) params.get("ownerName");
        String ownerPhone = (String) params.get("ownerPhone");
        String storeName = (String) params.get("storeName");
        String cateringType = (String) params.get("cateringType");
        String customerGroup = (String) params.get("customerGroup");
        String auditStatus = (String) params.get("auditStatus");

        QueryWrapper<BizStore> queryWrapper = new QueryWrapper<BizStore>()
                .eq(StringUtils.hasText(storeId), "store_id", storeId)
                .eq(StringUtils.hasText(ownerOpenid), "owner_openid", ownerOpenid)
                .like(StringUtils.hasText(ownerName), "owner_name", ownerName)
                .eq(StringUtils.hasText(ownerPhone), "owner_phone", ownerPhone)
                .like(StringUtils.hasText(storeName), "store_name", storeName)
                .eq(StringUtils.hasText(cateringType), "catering_type", cateringType)
                .eq(StringUtils.hasText(customerGroup), "customer_group", customerGroup)
                .eq(StringUtils.hasText(auditStatus), "audit_status", auditStatus)
                .orderByDesc("create_time");
        
        // 临时注释掉 deleted 条件，用于调试
        // .eq("deleted", 0)
        
        return this.page(new Query<BizStore>().getPage(params), queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFiles(BizStore bizStore, MultipartFile logoFile, MultipartFile workUniformFile,
                              MultipartFile ipImageFile, MultipartFile characterPhotoFile,
                              MultipartFile characterVoiceFile, String characterRole) throws Exception {
        // 1. 保存或更新门店基本信息
        String storeId = saveOrUpdateStore(bizStore);
        
        // 2. 保存或更新门店VI信息
        saveOrUpdateStoreVi(storeId, logoFile, workUniformFile, ipImageFile);
        
        // 3. 保存或更新人物形象信息
        saveOrUpdateStoreCharacter(storeId, characterPhotoFile, characterVoiceFile, characterRole);
    }

    /**
     * 保存或更新门店基本信息
     * @param bizStore 门店信息
     * @return 门店ID
     */
    private String saveOrUpdateStore(BizStore bizStore) {
        // 如果传入了storeId，先查询数据库
        if (bizStore.getStoreId() != null && !bizStore.getStoreId().isEmpty()) {
            String storeId = bizStore.getStoreId();
            BizStore existingStore = this.getById(storeId);
            if (existingStore != null) {
                // 存在记录，用传入参数覆盖，但保留创建时间
                Date originalCreateTime = existingStore.getCreateTime();
                BeanUtils.copyProperties(bizStore, existingStore);
                existingStore.setCreateTime(originalCreateTime);
                existingStore.setUpdateTime(new Date());
                bizStore = existingStore;
            } else {
                // 不存在记录，设置默认值后保存
                setStoreDefaultValues(bizStore);
            }
        } else {
            // 生成门店ID（如果为空）
            bizStore.setStoreId(UUID.randomUUID().toString().replace("-", ""));
            setStoreDefaultValues(bizStore);
        }
        
        // 保存或更新门店信息
        this.saveOrUpdate(bizStore);
        return bizStore.getStoreId();
    }

    /**
     * 设置门店默认值
     * @param bizStore 门店信息
     */
    private void setStoreDefaultValues(BizStore bizStore) {
        if (bizStore.getDeleted() == null) {
            bizStore.setDeleted(0);
        }
        if (bizStore.getCreateTime() == null) {
            bizStore.setCreateTime(new Date());
        }
        bizStore.setUpdateTime(new Date());
    }

    /**
     * 保存或更新门店VI信息
     * @param storeId 门店ID
     * @param logoFile LOGO文件
     * @param workUniformFile 工作服文件
     * @param ipImageFile IP形象文件
     */
    private void saveOrUpdateStoreVi(String storeId, MultipartFile logoFile, 
                                     MultipartFile workUniformFile, MultipartFile ipImageFile) throws Exception {
        // 查询是否存在VI记录
        BizStoreVi existingVi = bizStoreViService.getOne(
            new LambdaQueryWrapper<BizStoreVi>()
                .eq(BizStoreVi::getStoreId, storeId)
                .eq(BizStoreVi::getDeleted, 0)
        );
        
        BizStoreVi bizStoreVi;
        if (existingVi != null) {
            // 存在VI记录，更新
            bizStoreVi = existingVi;
            bizStoreVi.setUpdateTime(new Date());
        } else {
            // 不存在VI记录，创建
            bizStoreVi = new BizStoreVi();
            bizStoreVi.setViId(UUID.randomUUID().toString().replace("-", ""));
            bizStoreVi.setStoreId(storeId);
            bizStoreVi.setFromType("UPLOAD");
            bizStoreVi.setDeleted(0);
            bizStoreVi.setCreateTime(new Date());
            bizStoreVi.setUpdateTime(new Date());
        }

        // 上传文件（只有上传了文件才更新URL）
        if (logoFile != null && !logoFile.isEmpty()) {
            bizStoreVi.setLogoUrl(uploadFile(logoFile));
        }
        if (workUniformFile != null && !workUniformFile.isEmpty()) {
            bizStoreVi.setWorkUniformUrl(uploadFile(workUniformFile));
        }
        if (ipImageFile != null && !ipImageFile.isEmpty()) {
            bizStoreVi.setIpImageUrl(uploadFile(ipImageFile));
        }

        bizStoreViService.saveOrUpdate(bizStoreVi);
    }

    /**
     * 保存或更新人物形象信息
     * @param storeId 门店ID
     * @param characterPhotoFile 人物照片文件
     * @param characterVoiceFile 人物声音文件
     * @param characterRole 人物角色
     */
    private void saveOrUpdateStoreCharacter(String storeId, MultipartFile characterPhotoFile,
                                           MultipartFile characterVoiceFile, String characterRole) throws Exception {
        // 如果没有上传文件且没有角色信息，则跳过
        if ((characterPhotoFile == null || characterPhotoFile.isEmpty()) && 
            (characterVoiceFile == null || characterVoiceFile.isEmpty()) &&
            (characterRole == null || characterRole.isEmpty())) {
            return;
        }
        
        // 根据storeId和characterRole查询是否存在
        BizStoreCharacter existingCharacter = null;
        if (characterRole != null && !characterRole.isEmpty()) {
            existingCharacter = bizStoreCharacterService.getOne(
                new LambdaQueryWrapper<BizStoreCharacter>()
                    .eq(BizStoreCharacter::getStoreId, storeId)
                    .eq(BizStoreCharacter::getCharacterRole, characterRole)
                    .eq(BizStoreCharacter::getDeleted, 0)
            );
        }
        
        BizStoreCharacter bizStoreCharacter;
        if (existingCharacter != null) {
            // 存在记录，更新
            bizStoreCharacter = existingCharacter;
            bizStoreCharacter.setUpdateTime(new Date());
        } else {
            // 不存在记录，创建
            bizStoreCharacter = new BizStoreCharacter();
            bizStoreCharacter.setCharacterId(UUID.randomUUID().toString().replace("-", ""));
            bizStoreCharacter.setStoreId(storeId);
            bizStoreCharacter.setCharacterRole(characterRole);
            bizStoreCharacter.setFromType("UPLOAD");
            bizStoreCharacter.setDeleted(0);
            bizStoreCharacter.setCreateTime(new Date());
            bizStoreCharacter.setUpdateTime(new Date());
        }

        // 上传文件（只有上传了文件才更新URL）
        if (characterPhotoFile != null && !characterPhotoFile.isEmpty()) {
            bizStoreCharacter.setCharacterPhotoUrl(uploadFile(characterPhotoFile));
        }
        if (characterVoiceFile != null && !characterVoiceFile.isEmpty()) {
            bizStoreCharacter.setCharacterVoiceUrl(uploadFile(characterVoiceFile));
        }

        bizStoreCharacterService.saveOrUpdate(bizStoreCharacter);
    }

    /**
     * 上传文件到OSS并返回URL
     * @param file 文件
     * @return 文件URL
     */
    private String uploadFile(MultipartFile file) throws Exception {
        String suffix = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf("."));
        return Objects.requireNonNull(OSSFactory.build()).uploadSuffix(file.getBytes(), suffix);
    }
}

