package com.github.niefy.modules.wx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.niefy.config.TaskExcutor;
import com.github.niefy.modules.wx.entity.MsgReplyRule;
import com.github.niefy.modules.wx.entity.WxMsg;
import com.github.niefy.modules.wx.service.MsgReplyRuleService;
import com.github.niefy.modules.wx.service.MsgReplyService;
import com.github.niefy.modules.wx.service.WxMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 微信公众号消息处理
 * 官方文档：https://developers.weixin.qq.com/doc/offiaccount/Message_Ma nagement/Service_Center_messages.html#7
 * 参考WxJava客服消息文档：https://github.com/Wechat-Group/WxJava/wiki/MP_主动发送消息（客服消息）
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MsgReplyServiceImpl implements MsgReplyService {
    @Autowired
    MsgReplyRuleService msgReplyRuleService;
    @Autowired
    WxMpService wxMpService;
    @Value("${wx.mp.autoReplyInterval:1000}")
    Long autoReplyInterval;
    @Autowired
    WxMsgService wxMsgService;

    /**
     * 根据规则配置通过微信客服消息接口自动回复消息
     *
     *
     * @param appid 公众号appid
     * @param exactMatch 是否精确匹配
     * @param toUser     用户openid
     * @param keywords   匹配关键词
     * @return 是否已自动回复，无匹配规则则不自动回复
     */
    @Override
    public boolean tryAutoReply(String appid, boolean exactMatch, String toUser, String keywords) {
        try {
            List<MsgReplyRule> rules = msgReplyRuleService.getMatchedRules(appid,exactMatch, keywords);
            if (rules.isEmpty()) {
                return false;
            }
            long delay = 0;
            for (MsgReplyRule rule : rules) {
                TaskExcutor.schedule(() -> {
                    wxMpService.switchover(appid);
                    this.reply(toUser,rule.getReplyType(),rule.getReplyContent());
                }, delay, TimeUnit.MILLISECONDS);
                delay += autoReplyInterval;
            }
            return true;
        } catch (Exception e) {
            log.error("自动回复出错：", e);
        }
        return false;
    }

    @Override
    public void replyText(String toUser, String content) throws WxErrorException {
        wxMpService.getKefuService().sendKefuMessage(WxMpKefuMessage.TEXT().toUser(toUser).content(content).build());

        JSONObject json = new JSONObject().fluentPut("content",content);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.TEXT,toUser,json));
    }

    @Override
    public void replyImage(String toUser, String mediaId) throws WxErrorException {
        wxMpService.getKefuService().sendKefuMessage(WxMpKefuMessage.IMAGE().toUser(toUser).mediaId(mediaId).build());

        JSONObject json = new JSONObject().fluentPut("mediaId",mediaId);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.IMAGE,toUser,json));
    }

    @Override
    public void replyVoice(String toUser, String mediaId) throws WxErrorException {
        wxMpService.getKefuService().sendKefuMessage(WxMpKefuMessage.VOICE().toUser(toUser).mediaId(mediaId).build());

        JSONObject json = new JSONObject().fluentPut("mediaId",mediaId);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.VOICE,toUser,json));
    }

    @Override
    public void replyVideo(String toUser, String mediaId) throws WxErrorException {
        wxMpService.getKefuService().sendKefuMessage(WxMpKefuMessage.VIDEO().toUser(toUser).mediaId(mediaId).build());

        JSONObject json = new JSONObject().fluentPut("mediaId",mediaId);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.VIDEO,toUser,json));
    }

    @Override
    public void replyMusic(String toUser, String musicInfoJson) throws WxErrorException {
        JSONObject json = JSON.parseObject(musicInfoJson);
        wxMpService.getKefuService().sendKefuMessage(
            WxMpKefuMessage.MUSIC().toUser(toUser)
                .musicUrl(json.getString("musicurl"))
                .hqMusicUrl(json.getString("hqmusicurl"))
                .title(json.getString("title"))
                .description(json.getString("description"))
                .thumbMediaId(json.getString("thumb_media_id"))
                .build());

        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.IMAGE,toUser,json));
    }

    /**
     * 发送图文消息（点击跳转到外链） 图文消息条数限制在1条以内
     * @param toUser
     * @param newsInfoJson
     * @throws WxErrorException
     */
    @Override
    public void replyNews(String toUser, String newsInfoJson) throws WxErrorException {
        WxMpKefuMessage.WxArticle wxArticle = JSON.parseObject(newsInfoJson, WxMpKefuMessage.WxArticle.class);
        List<WxMpKefuMessage.WxArticle> newsList = new ArrayList<WxMpKefuMessage.WxArticle>(){{add(wxArticle);}};
        wxMpService.getKefuService().sendKefuMessage(WxMpKefuMessage.NEWS().toUser(toUser).articles(newsList).build());

        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.NEWS,toUser,JSON.parseObject(newsInfoJson)));
    }

    /**
     * 发送图文消息（点击跳转到图文消息页面） 图文消息条数限制在1条以内
     * @param toUser
     * @param mediaId
     * @throws WxErrorException
     */
    @Override
    public void replyMpNews(String toUser, String mediaId) throws WxErrorException {
        wxMpService.getKefuService().sendKefuMessage(WxMpKefuMessage.MPNEWS().toUser(toUser).mediaId(mediaId).build());

        JSONObject json = new JSONObject().fluentPut("mediaId",mediaId);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.MPNEWS,toUser,json));
    }

    @Override
    public void replyWxCard(String toUser, String cardId) throws WxErrorException {
        wxMpService.getKefuService().sendKefuMessage(WxMpKefuMessage.WXCARD().toUser(toUser).cardId(cardId).build());

        JSONObject json = new JSONObject().fluentPut("cardId",cardId);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.WXCARD,toUser,json));
    }

    @Override
    public void replyMiniProgram(String toUser, String miniProgramInfoJson) throws WxErrorException {
        JSONObject json = JSON.parseObject(miniProgramInfoJson);
        wxMpService.getKefuService().sendKefuMessage(
            WxMpKefuMessage.MINIPROGRAMPAGE()
                .toUser(toUser)
                .title(json.getString("title"))
                .appId(json.getString("appid"))
                .pagePath(json.getString("pagepath"))
                .thumbMediaId(json.getString("thumb_media_id"))
                .build());

        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.IMAGE,toUser,json));
    }

    @Override
    public void replyMsgMenu(String toUser, String msgMenusJson) throws WxErrorException {
        JSONObject json = JSON.parseObject(msgMenusJson);
        List<WxMpKefuMessage.MsgMenu> msgMenus = JSON.parseArray(json.getString("list"),WxMpKefuMessage.MsgMenu.class);
        wxMpService.getKefuService().sendKefuMessage(
            WxMpKefuMessage.MSGMENU()
                .toUser(toUser)
                .headContent(json.getString("head_content"))
                .tailContent(json.getString("tail_content"))
                .msgMenus(msgMenus).build());

        wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.IMAGE,toUser,json));
    }

    /**
     * 回复混合消息（包含文本和多个媒体文件）
     * 解析规则：
     * 1. 按照模板逐行解析，保持顺序
     * 2. 遇到 $IMAGE{{mediaId}} 格式，发送图片
     * 3. 遇到 $VIDEO{{mediaId}} 格式，发送视频
     * 4. 其他内容作为文本发送
     * 5. 按照模板顺序逐行发送：文本块 -> 图片/视频 -> 文本块 -> 图片/视频...
     */
    @Override
    public void replyMixed(String toUser, String mixedContent) throws WxErrorException {
        if (mixedContent == null || mixedContent.trim().isEmpty()) {
            return;
        }

        // 匹配 $IMAGE{{mediaId}} 或 $VIDEO{{mediaId}} 格式
        Pattern imagePattern = Pattern.compile("\\$IMAGE\\{\\{([^}]+)\\}\\}");
        Pattern videoPattern = Pattern.compile("\\$VIDEO\\{\\{([^}]+)\\}\\}");
        
        // 按行分割内容，保持顺序
        String[] lines = mixedContent.split("\n", -1);
        List<MessageItem> messageItems = new ArrayList<>();
        StringBuilder currentTextBlock = new StringBuilder();
        
        for (String line : lines) {
            line = line.trim();
            
            // 检查是否是图片标记
            Matcher imageMatcher = imagePattern.matcher(line);
            if (imageMatcher.find()) {
                // 先发送当前文本块（如果有内容）
                if (currentTextBlock.length() > 0) {
                    String textContent = currentTextBlock.toString().trim();
                    if (!textContent.isEmpty()) {
                        messageItems.add(new MessageItem("text", textContent));
                    }
                    currentTextBlock.setLength(0);
                }
                // 添加图片消息
                String mediaId = imageMatcher.group(1).trim();
                messageItems.add(new MessageItem("image", mediaId));
                continue;
            }
            
            // 检查是否是视频标记
            Matcher videoMatcher = videoPattern.matcher(line);
            if (videoMatcher.find()) {
                // 先发送当前文本块（如果有内容）
                if (currentTextBlock.length() > 0) {
                    String textContent = currentTextBlock.toString().trim();
                    if (!textContent.isEmpty()) {
                        messageItems.add(new MessageItem("text", textContent));
                    }
                    currentTextBlock.setLength(0);
                }
                // 添加视频消息
                String mediaId = videoMatcher.group(1).trim();
                messageItems.add(new MessageItem("video", mediaId));
                continue;
            }
            
            // 普通文本行，添加到当前文本块
            if (currentTextBlock.length() > 0) {
                currentTextBlock.append("\n");
            }
            currentTextBlock.append(line);
        }
        
        // 处理最后的文本块
        if (currentTextBlock.length() > 0) {
            String textContent = currentTextBlock.toString().trim();
            if (!textContent.isEmpty()) {
                messageItems.add(new MessageItem("text", textContent));
            }
        }

        // 按照顺序逐行发送消息
        boolean shouldStop = false;
        int sentCount = 0;
        int failedCount = 0;
        
        for (MessageItem item : messageItems) {
            if (shouldStop) {
                log.warn("已达到客服消息发送上限，停止发送剩余{}条消息", messageItems.size() - sentCount - failedCount);
                break;
            }
            
            try {
                if ("text".equals(item.type)) {
                    replyText(toUser, item.content);
                    log.debug("发送文本消息: {}", item.content.substring(0, Math.min(50, item.content.length())));
                } else if ("image".equals(item.type)) {
                    replyImage(toUser, item.content);
                    log.debug("发送图片消息，mediaId: {}", item.content.substring(0, Math.min(20, item.content.length())));
                } else if ("video".equals(item.type)) {
                    replyVideo(toUser, item.content);
                    log.debug("发送视频消息，mediaId: {}", item.content.substring(0, Math.min(20, item.content.length())));
                }
                sentCount++;
                // 添加延迟，避免消息发送过快
                Thread.sleep(autoReplyInterval);
            } catch (WxErrorException e) {
                failedCount++;
                // 检查是否是客服接口下行条数超过上限的错误（45047）
                int errorCode = e.getError() != null ? e.getError().getErrorCode() : 0;
                if (errorCode == 45047 || (e.getMessage() != null && e.getMessage().contains("45047"))) {
                    log.error("客服接口下行条数超过上限（错误代码：45047），已发送{}条消息，停止发送剩余消息。错误信息：{}", 
                        sentCount, e.getMessage());
                    shouldStop = true;
                    break;
                }
                // 其他错误（如mediaId无效或过期），记录错误但继续发送其他消息
                log.warn("发送{}消息失败，内容: {}，错误: {}", 
                    item.type, 
                    item.content.substring(0, Math.min(30, item.content.length())), 
                    e.getMessage());
                // 继续发送下一个消息
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        if (shouldStop) {
            log.warn("由于达到发送上限，共发送{}条消息，失败{}条，剩余{}条未发送", 
                sentCount, failedCount, messageItems.size() - sentCount - failedCount);
        }

        // 记录混合消息日志
        JSONObject json = new JSONObject();
        json.put("originalContent", mixedContent);
        json.put("messageCount", messageItems.size());
        List<JSONObject> itemsJson = new ArrayList<>();
        for (MessageItem item : messageItems) {
            JSONObject itemJson = new JSONObject();
            itemJson.put("type", item.type);
            itemJson.put("content", item.content.length() > 100 ? item.content.substring(0, 100) + "..." : item.content);
            itemsJson.add(itemJson);
        }
        json.put("messageItems", itemsJson);
        wxMsgService.addWxMsg(WxMsg.buildOutMsg("mixed", toUser, json));
    }

    /**
     * 消息项内部类
     */
    private static class MessageItem {
        String type; // "text", "image", or "video"
        String content; // 文本内容或mediaId

        MessageItem(String type, String content) {
            this.type = type;
            this.content = content;
        }
    }

}
