package com.github.niefy.modules.oss.service;

import com.github.niefy.common.utils.ConfigConstant;
import com.github.niefy.modules.oss.cloud.CloudStorageConfig;
import com.github.niefy.modules.sys.service.SysConfigService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 腾讯云COS存储服务
 * 
 * @author niefy
 */
@Service
public class TosStorageService {
    private static final Logger logger = LoggerFactory.getLogger(TosStorageService.class);
    
    private final COSClient cosClient;
    private final SysConfigService sysConfigService;
    private CloudStorageConfig config;

    public TosStorageService(@Autowired COSClient cosClient, @Autowired SysConfigService sysConfigService) {
        this.cosClient = cosClient;
        this.sysConfigService = sysConfigService;
    }
    
    @PostConstruct
    public void init() {
        this.config = sysConfigService.getConfigObject(ConfigConstant.CLOUD_STORAGE_CONFIG_KEY, CloudStorageConfig.class);
        
        if (config == null || StringUtils.isBlank(config.getQcloudBucketName()) 
                || StringUtils.isBlank(config.getQcloudPrefix()) 
                || StringUtils.isBlank(config.getQcloudDomain())) {
            throw new RuntimeException("腾讯云COS配置不完整，请先在系统配置中设置云存储配置信息");
        }
    }

    /**
     * 上传文件到COS，不添加LOGO（跳过LOGO处理）
     *
     * @param file          文件
     * @param sceneWithCosNo COS文件名标识
     * @return 文件URL
     * @throws IOException 上传失败时抛出
     */
    public String storeWithoutLogo(MultipartFile file, String sceneWithCosNo) throws IOException {
        // 生成唯一文件名（带UUID）
        String originalFilename = file.getOriginalFilename();
        // 如果原始文件名包含中文，将中文部分转换为拼音首字母大写
        originalFilename = processChineseInFilename(originalFilename);
        String fileExtension = extractExtension(originalFilename);
        if (StringUtils.isEmpty(sceneWithCosNo)) {
            sceneWithCosNo = UUID.randomUUID().toString().replace("-", "");
        } else {
            // 如果sceneWithCosNo包含中文，将中文部分转换为拼音首字母大写
            sceneWithCosNo = processChineseInFilename(sceneWithCosNo);
        }
        String uniqueFileName = sceneWithCosNo + fileExtension;
        String folder = config.getQcloudPrefix();
        if (!folder.endsWith("/")) {
            folder = folder + "/";
        }
        String cosKey = folder + uniqueFileName;
        // 构建元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        // 直接上传，不经过LOGO处理
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest request = new PutObjectRequest(
                    config.getQcloudBucketName(), cosKey, inputStream, metadata
            );
            cosClient.putObject(request);
            String webUrl = config.getQcloudDomain();
            if (!webUrl.endsWith("/")) {
                webUrl = webUrl + "/";
            }
            return webUrl + cosKey;
        } catch (Exception e) {
            throw new IOException("COS文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 提取文件扩展名
     */
    private String extractExtension(String originalFilename) {
        if (StringUtils.isBlank(originalFilename) || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 处理文件名中的中文，将中文部分转换为拼音首字母大写
     * 例如："测试文件.jpg" -> "CSWJ.jpg"
     *
     * @param filename 原始文件名
     * @return 处理后的文件名
     */
    private String processChineseInFilename(String filename) {
        if (StringUtils.isBlank(filename)) {
            return filename;
        }

        // 检查是否包含中文字符
        if (!containsChinese(filename)) {
            return filename;
        }

        StringBuilder result = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        for (char c : filename.toCharArray()) {
            if (isChinese(c)) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        // 取第一个拼音的首字母
                        String pinyin = pinyinArray[0];
                        if (pinyin != null && pinyin.length() > 0) {
                            result.append(pinyin.charAt(0));
                        }
                    } else {
                        // 如果无法转换，保留原字符
                        result.append(c);
                    }
                } catch (Exception e) {
                    logger.warn("转换中文字符失败: {}", c, e);
                    // 转换失败时保留原字符
                    result.append(c);
                }
            } else {
                // 非中文字符直接保留
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * 判断字符串是否包含中文字符
     *
     * @param str 待检查的字符串
     * @return 如果包含中文返回true，否则返回false
     */
    private boolean containsChinese(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符是否为中文字符
     *
     * @param c 待检查的字符
     * @return 如果是中文返回true，否则返回false
     */
    private boolean isChinese(char c) {
        // 判断是否为中文字符（包括CJK统一汉字、扩展A区、扩展B区等）
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }
}

