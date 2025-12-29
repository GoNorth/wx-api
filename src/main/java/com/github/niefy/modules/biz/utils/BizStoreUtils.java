package com.github.niefy.modules.biz.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.niefy.common.utils.SpringContextUtils;
import com.github.niefy.modules.biz.entity.BizStore;
import com.github.niefy.modules.biz.service.BizStoreService;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 门店工具类
 * 提供从wx_openid获取storeId等公共方法
 *
 * @author niefy
 * @date 2024-12-29
 */
public class BizStoreUtils {

    /**
     * 从HttpServletRequest的header中获取wx_openid，并查询对应的storeId
     *
     * @param request HttpServletRequest对象
     * @return storeId，如果未找到则返回null
     */
    public static String getStoreIdByWxOpenid(HttpServletRequest request) {
        String wxOpenid = request.getHeader("wx_openid");
        if (!StringUtils.hasText(wxOpenid)) {
            return null;
        }
        return getStoreIdByWxOpenid(wxOpenid);
    }

    /**
     * 通过wx_openid查询对应的storeId
     *
     * @param wxOpenid 微信openid
     * @return storeId，如果未找到则返回null
     */
    public static String getStoreIdByWxOpenid(String wxOpenid) {
        if (!StringUtils.hasText(wxOpenid)) {
            return null;
        }

        BizStoreService bizStoreService = SpringContextUtils.getBean(BizStoreService.class);
        BizStore bizStore = bizStoreService.getOne(
                new LambdaQueryWrapper<BizStore>()
                        .eq(BizStore::getOwnerOpenid, wxOpenid)
                        .eq(BizStore::getDeleted, 0)
        );

        if (bizStore != null && StringUtils.hasText(bizStore.getStoreId())) {
            return bizStore.getStoreId();
        }
        return null;
    }

    /**
     * 从HttpServletRequest的header中获取wx_openid，并查询对应的BizStore对象
     *
     * @param request HttpServletRequest对象
     * @return BizStore对象，如果未找到则返回null
     */
    public static BizStore getBizStoreByWxOpenid(HttpServletRequest request) {
        String wxOpenid = request.getHeader("wx_openid");
        if (!StringUtils.hasText(wxOpenid)) {
            return null;
        }
        return getBizStoreByWxOpenid(wxOpenid);
    }

    /**
     * 通过wx_openid查询对应的BizStore对象
     *
     * @param wxOpenid 微信openid
     * @return BizStore对象，如果未找到则返回null
     */
    public static BizStore getBizStoreByWxOpenid(String wxOpenid) {
        if (!StringUtils.hasText(wxOpenid)) {
            return null;
        }

        BizStoreService bizStoreService = SpringContextUtils.getBean(BizStoreService.class);
        return bizStoreService.getOne(
                new LambdaQueryWrapper<BizStore>()
                        .eq(BizStore::getOwnerOpenid, wxOpenid)
                        .eq(BizStore::getDeleted, 0)
        );
    }
}

