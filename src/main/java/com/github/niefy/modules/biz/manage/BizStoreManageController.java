package com.github.niefy.modules.biz.manage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizStore;
import com.github.niefy.modules.biz.entity.BizStoreCharacter;
import com.github.niefy.modules.biz.entity.BizStoreVi;
import com.github.niefy.modules.biz.service.BizStoreCharacterService;
import com.github.niefy.modules.biz.service.BizStoreService;
import com.github.niefy.modules.biz.service.BizStoreViService;
import com.github.niefy.modules.biz.utils.BizStoreUtils;
import com.github.niefy.modules.oss.service.TosStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 门店表-管理后台
 *
 * @author niefy
 * @date 2024-12-27
 */
@RestController
@RequestMapping("/manage/bizStore")
@Api(tags = {"门店表-管理后台"})
public class BizStoreManageController {
    private static final Logger logger = LoggerFactory.getLogger(BizStoreManageController.class);
    
    @Autowired
    private BizStoreService bizStoreService;

    @Autowired
    private BizStoreViService bizStoreViService;

    @Autowired
    private BizStoreCharacterService bizStoreCharacterService;

    @Autowired
    private TosStorageService storageService;

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("biz:bizstore:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = new PageUtils(bizStoreService.queryPage(params));
        return R.ok().put("page", page);
    }

    /**
     * 信息（通过路径参数storeId或header的wx_openid）
     * 支持两种访问方式：
     * 1. /info/{storeId} - 通过路径参数传递storeId
     * 2. /info - 不传路径参数，从header的wx_openid查询对应的storeId
     */
    @GetMapping({"/info/{storeId}", "/info"})
    // @RequiresPermissions("biz:bizstore:info")
    @ApiOperation(value = "详情（通过storeId路径参数或wx_openid header）")
    public R info(@PathVariable(value = "storeId", required = false) String storeId, HttpServletRequest request) {
        // 如果storeId为空，尝试从header的wx_openid查询对应的storeId
        if (!StringUtils.hasText(storeId)) {
            String storeIdByOpenid = BizStoreUtils.getStoreIdByWxOpenid(request);
            if (StringUtils.hasText(storeIdByOpenid)) {
                storeId = storeIdByOpenid;
            } else {
                return R.error("storeId参数或wx_openid header不能同时为空，或未找到对应的门店信息");
            }
        }
        
        return getStoreInfo(storeId);
    }

    /**
     * 获取门店信息的通用方法
     */
    private R getStoreInfo(String storeId) {
        // 查询门店基本信息
        BizStore bizStore = bizStoreService.getById(storeId);
        if (bizStore == null) {
            return R.error("门店不存在");
        }

        // 查询门店VI信息（1:1关系）
        BizStoreVi bizStoreVi = bizStoreViService.getOne(
                new LambdaQueryWrapper<BizStoreVi>()
                        .eq(BizStoreVi::getStoreId, storeId)
                        .eq(BizStoreVi::getDeleted, 0)
        );

        // 查询门店人物形象列表（1:N关系）
        List<BizStoreCharacter> bizStoreCharacterList = bizStoreCharacterService.list(
                new LambdaQueryWrapper<BizStoreCharacter>()
                        .eq(BizStoreCharacter::getStoreId, storeId)
                        .eq(BizStoreCharacter::getDeleted, 0)
                        .orderByAsc(BizStoreCharacter::getCreateTime)
        );

        return R.ok()
                .put("bizStore", bizStore)
                .put("bizStoreVi", bizStoreVi)
                .put("bizStoreCharacterList", bizStoreCharacterList);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("biz:bizstore:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody BizStore bizStore) {
        bizStoreService.save(bizStore);
        return R.ok();
    }

    /**
     * 保存门店信息（包括门店、VI、人物形象及文件上传）
     * 
     * 请求示例（multipart/form-data）:
     * 
     * 门店基本信息字段：
     * - ownerOpenid: "wx_openid_001" (必填)
     * - ownerName: "张三" (必填)
     * - ownerPhone: "13800138000" (必填)
     * - storeName: "测试餐厅" (必填)
     * - cateringType: "CHINESE" (必填，可选值：CHINESE/WESTERN/JAPANESE/HOTPOT/BARBECUE/FASTFOOD/SNACK/OTHER)
     * - storeAddress: "北京市朝阳区测试街道123号" (必填)
     * - longitude: "116.1234567" (可选)
     * - latitude: "39.1234567" (可选)
     * - customerGroup: "OFFICE_WORKER" (必填，可选值：STUDENT/OFFICE_WORKER/FAMILY/BUSINESS/ELDERLY/OTHER)
     * - auditStatus: "0" (可选，默认0：待审核)
     * - auditRemark: "审核备注" (可选)
     * 
     * 门店VI文件（可选）：
     * - logoFile: 门店LOGO文件 (JPG/PNG)
     * - workUniformFile: 工作服照片文件 (JPG/PNG)
     * - ipImageFile: IP形象设计图文件 (JPG/PNG)
     * 
     * 人物形象文件（可选）：
     * - characterPhotoFile: 人物照片文件 (JPG/PNG)
     * - characterVoiceFile: 人物声音文件 (MP3/WAV)
     * - characterRole: "OWNER" (可选，可选值：OWNER/CHEF/WAITER/MANAGER/OTHER)
     * 
     * cURL示例：
     * curl -X POST "http://localhost:8080/manage/bizStore/saveWithFiles" \
     *   -F "ownerOpenid=wx_openid_001" \
     *   -F "ownerName=张三" \
     *   -F "ownerPhone=13800138000" \
     *   -F "storeName=测试餐厅" \
     *   -F "cateringType=CHINESE" \
     *   -F "storeAddress=北京市朝阳区测试街道123号" \
     *   -F "customerGroup=OFFICE_WORKER" \
     *   -F "logoFile=@logo.jpg" \
     *   -F "characterPhotoFile=@photo.jpg" \
     *   -F "characterRole=OWNER"
     */
    @PostMapping("/saveWithFiles")
    // @RequiresPermissions("biz:bizstore:save")
    @ApiOperation(value = "保存门店信息（含文件上传）")
    public R saveWithFiles(
            // 门店基本信息
            @ModelAttribute BizStore bizStore,
            // 人物角色 + 人物形象文件
            @RequestParam(required = false) String characterRole,
            @RequestParam(required = false) MultipartFile characterPhotoFile,
            @RequestParam(required = false) MultipartFile characterVoiceFile,
            // 门店VI文件
            @RequestParam(required = false) MultipartFile logoFile,
            @RequestParam(required = false) MultipartFile workUniformFile,
            @RequestParam(required = false) MultipartFile ipImageFile

    ) throws Exception {
        if (bizStore == null) {
            return R.error("门店信息不得为空");
        }
        
        // 1. 保存或更新门店基本信息，获取storeId
        String storeId = saveOrUpdateStore(bizStore);
        
        // 2. 处理门店VI文件上传并保存
        saveOrUpdateStoreViWithFiles(storeId, logoFile, workUniformFile, ipImageFile);
        
        // 3. 处理人物形象文件上传并保存
        saveOrUpdateStoreCharacterWithFiles(storeId, characterPhotoFile, characterVoiceFile, characterRole);
        
        return R.ok();
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
            BizStore existingStore = bizStoreService.getById(storeId);
            if (existingStore != null) {
                // 存在记录，用传入参数覆盖，但保留创建时间
                Date originalCreateTime = existingStore.getCreateTime();
                bizStore.setStoreId(storeId);
                bizStore.setCreateTime(originalCreateTime);
                bizStore.setUpdateTime(new Date());
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
        bizStoreService.saveOrUpdate(bizStore);
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
     * 处理门店VI文件上传并保存
     * @param storeId 门店ID
     * @param logoFile LOGO文件
     * @param workUniformFile 工作服文件
     * @param ipImageFile IP形象文件
     */
    private void saveOrUpdateStoreViWithFiles(String storeId, MultipartFile logoFile,
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

        // 上传文件并设置URL（只有上传了文件才更新URL）
        if (logoFile != null && !logoFile.isEmpty()) {
            String logoUrl = uploadFile(logoFile, storeId);
            bizStoreVi.setLogoUrl(logoUrl);
            logger.info("LOGO文件上传成功: {}", logoUrl);
        }
        if (workUniformFile != null && !workUniformFile.isEmpty()) {
            String workUniformUrl = uploadFile(workUniformFile, storeId);
            bizStoreVi.setWorkUniformUrl(workUniformUrl);
            logger.info("工作服文件上传成功: {}", workUniformUrl);
        }
        if (ipImageFile != null && !ipImageFile.isEmpty()) {
            String ipImageUrl = uploadFile(ipImageFile, storeId);
            bizStoreVi.setIpImageUrl(ipImageUrl);
            logger.info("IP形象文件上传成功: {}", ipImageUrl);
        }

        bizStoreViService.saveOrUpdate(bizStoreVi);
    }

    /**
     * 处理人物形象文件上传并保存
     * @param storeId 门店ID
     * @param characterPhotoFile 人物照片文件
     * @param characterVoiceFile 人物声音文件
     * @param characterRole 人物角色
     */
    private void saveOrUpdateStoreCharacterWithFiles(String storeId, MultipartFile characterPhotoFile,
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

        // 上传文件并设置URL（只有上传了文件才更新URL）
        if (characterPhotoFile != null && !characterPhotoFile.isEmpty()) {
            String characterPhotoUrl = uploadFile(characterPhotoFile, storeId);
            bizStoreCharacter.setCharacterPhotoUrl(characterPhotoUrl);
            logger.info("人物照片文件上传成功: {}", characterPhotoUrl);
        }
        if (characterVoiceFile != null && !characterVoiceFile.isEmpty()) {
            String characterVoiceUrl = uploadFile(characterVoiceFile, storeId);
            bizStoreCharacter.setCharacterVoiceUrl(characterVoiceUrl);
            logger.info("人物声音文件上传成功: {}", characterVoiceUrl);
        }

        bizStoreCharacterService.saveOrUpdate(bizStoreCharacter);
    }

    /**
     * 上传文件到COS并返回URL
     * 文件名格式：原始文件名（不含扩展名）+ storeId + 扩展名（不添加LOGO）
     * @param file 文件
     * @param storeId 门店ID
     * @return 文件URL
     */
    private String uploadFile(MultipartFile file, String storeId) throws Exception {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        // 提取文件名（不含扩展名）
        String fileNameWithoutExt;
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            fileNameWithoutExt = originalFilename.substring(0, lastDotIndex);
        } else {
            fileNameWithoutExt = originalFilename;
        }

        // 上传到COS，文件名格式：原始文件名（不含扩展名）+ storeId + 扩展名（不添加LOGO）
        String cosFileName = fileNameWithoutExt + "_" + storeId;
        String fileUrl = storageService.storeWithoutLogo(file, cosFileName);
        logger.info("文件上传COS成功（未添加LOGO）: {}", fileUrl);
        return fileUrl;
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("biz:bizstore:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody BizStore bizStore) {
        bizStoreService.updateById(bizStore);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    // @RequiresPermissions("biz:bizstore:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] storeIds) {
        bizStoreService.removeByIds(Arrays.asList(storeIds));
        return R.ok();
    }
}

