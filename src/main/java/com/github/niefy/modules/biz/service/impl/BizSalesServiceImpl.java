package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizSalesMapper;
import com.github.niefy.modules.biz.entity.BizSales;
import com.github.niefy.modules.biz.service.BizSalesService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 销售员表
 *
 * @author niefy
 * @date 2025-01-01
 */
@Service
public class BizSalesServiceImpl extends ServiceImpl<BizSalesMapper, BizSales> implements BizSalesService {

    @Override
    public IPage<BizSales> queryPage(Map<String, Object> params) {
        String salesId = (String) params.get("salesId");
        String salesAccount = (String) params.get("salesAccount");
        String salesName = (String) params.get("salesName");
        String gender = (String) params.get("gender");
        String phone = (String) params.get("phone");
        
        QueryWrapper<BizSales> queryWrapper = new QueryWrapper<BizSales>()
                .eq(StringUtils.hasText(salesId), "sales_id", salesId)
                .eq(StringUtils.hasText(salesAccount), "sales_account", salesAccount)
                .like(StringUtils.hasText(salesName), "sales_name", salesName)
                .eq(StringUtils.hasText(gender), "gender", gender)
                .eq(StringUtils.hasText(phone), "phone", phone)
                .eq("deleted", 0);

        queryWrapper.orderByDesc("create_time");

        return this.page(new Query<BizSales>().getPage(params), queryWrapper);
    }
}

