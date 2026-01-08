package com.github.niefy.config;

import com.github.niefy.common.utils.ConfigConstant;
import com.github.niefy.common.utils.SpringContextUtils;
import com.github.niefy.modules.oss.cloud.CloudStorageConfig;
import com.github.niefy.modules.sys.service.SysConfigService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosConfig {
    @Bean
    public COSClient cosClient() {
        SysConfigService sysConfigService = SpringContextUtils.getBean(SysConfigService.class);
        CloudStorageConfig config = sysConfigService.getConfigObject(ConfigConstant.CLOUD_STORAGE_CONFIG_KEY, CloudStorageConfig.class);
        
        if (config == null || StringUtils.isBlank(config.getQcloudSecretId()) 
                || StringUtils.isBlank(config.getQcloudSecretKey()) 
                || StringUtils.isBlank(config.getQcloudRegion())) {
            throw new RuntimeException("腾讯云COS配置不完整，请先在系统配置中设置云存储配置信息");
        }
        
        COSCredentials cred = new BasicCOSCredentials(config.getQcloudSecretId(), config.getQcloudSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(config.getQcloudRegion()));
        return new COSClient(cred, clientConfig);
    }
}

