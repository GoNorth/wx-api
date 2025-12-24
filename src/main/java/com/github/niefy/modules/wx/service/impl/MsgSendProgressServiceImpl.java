package com.github.niefy.modules.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.modules.wx.dao.MsgSendProgressMapper;
import com.github.niefy.modules.wx.entity.MsgSendProgress;
import com.github.niefy.modules.wx.service.MsgSendProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 消息发送进度服务实现
 */
@Service
@Slf4j
public class MsgSendProgressServiceImpl extends ServiceImpl<MsgSendProgressMapper, MsgSendProgress> implements MsgSendProgressService {

    @Override
    public void saveOrUpdateProgress(String contentHash, String appid, String openid, 
                                     String originalContent, String messageItems, 
                                     Integer totalCount, Integer sentCount, Integer completed) {
        LambdaQueryWrapper<MsgSendProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MsgSendProgress::getContentHash, contentHash)
               .eq(MsgSendProgress::getOpenid, openid);
        
        MsgSendProgress progress = this.getOne(wrapper);
        
        if (progress == null) {
            progress = new MsgSendProgress();
            progress.setContentHash(contentHash);
            progress.setAppid(appid);
            progress.setOpenid(openid);
            progress.setCreateTime(new Date());
        }
        
        progress.setOriginalContent(originalContent);
        progress.setMessageItems(messageItems);
        progress.setTotalCount(totalCount);
        progress.setSentCount(sentCount);
        progress.setCompleted(completed);
        progress.setUpdateTime(new Date());
        
        this.saveOrUpdate(progress);
    }

    @Override
    public MsgSendProgress getProgress(String contentHash, String openid) {
        LambdaQueryWrapper<MsgSendProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MsgSendProgress::getContentHash, contentHash)
               .eq(MsgSendProgress::getOpenid, openid)
               .orderByDesc(MsgSendProgress::getUpdateTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public MsgSendProgress getLatestUncompletedProgress(String openid) {
        LambdaQueryWrapper<MsgSendProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MsgSendProgress::getOpenid, openid)
               .eq(MsgSendProgress::getCompleted, 0)
               .orderByDesc(MsgSendProgress::getUpdateTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }
}

