package com.github.niefy.modules.biz.manage;

import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.R;
import com.github.niefy.modules.biz.entity.BizStore;
import com.github.niefy.modules.biz.service.BizStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Map;

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
    @Autowired
    private BizStoreService bizStoreService;

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
     * 信息
     */
    @GetMapping("/info/{storeId}")
    // @RequiresPermissions("biz:bizstore:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("storeId") String storeId) {
        BizStore bizStore = bizStoreService.getById(storeId);
        return R.ok().put("bizStore", bizStore);
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
            // 门店VI文件
            @RequestParam(required = false) MultipartFile logoFile,
            @RequestParam(required = false) MultipartFile workUniformFile,
            @RequestParam(required = false) MultipartFile ipImageFile,
            // 人物形象文件
            @RequestParam(required = false) MultipartFile characterPhotoFile,
            @RequestParam(required = false) MultipartFile characterVoiceFile,
            // 人物角色
            @RequestParam(required = false) String characterRole
    ) throws Exception {
        if (bizStore == null) {
            return R.error("门店信息不得为空");
        }
        bizStoreService.saveWithFiles(bizStore, logoFile, workUniformFile, ipImageFile, 
                characterPhotoFile, characterVoiceFile, characterRole);
        return R.ok();
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

