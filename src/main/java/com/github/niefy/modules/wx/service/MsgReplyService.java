package com.github.niefy.modules.wx.service;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 公众号消息处理
 * 官方文档：https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Service_Center_messages.html#7
 * WxJava客服消息文档：https://github.com/Wechat-Group/WxJava/wiki/MP_主动发送消息（客服消息）
 */
public interface MsgReplyService {
    Logger logger = LoggerFactory.getLogger(MsgReplyService.class);

    /**
     * 根据规则配置通过微信客服消息接口自动回复消息
     *
     *
     * @param appid
     * @param exactMatch 是否精确匹配
     * @param toUser     用户openid
     * @param keywords   匹配关键词
     * @return 是否已自动回复，无匹配规则则不自动回复
     */
    boolean tryAutoReply(String appid, boolean exactMatch, String toUser, String keywords);

    default void reply(String toUser,String replyType, String replyContent){
        try {
            // 自动检测：如果replyType是支持混合内容的媒体类型（如image、video），且内容包含多个mediaId和文本，自动转换为mixed类型
            if (isMediaTypeSupportingMixedContent(replyType) && isMixedContent(replyContent)) {
                logger.info("检测到混合消息内容，自动将{}类型转换为mixed类型", replyType);
                this.replyMixed(toUser, replyContent);
                return;
            }
            
            if (WxConsts.KefuMsgType.TEXT.equals(replyType)) {
                this.replyText(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.IMAGE.equals(replyType)) {
                this.replyImage(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.VOICE.equals(replyType)) {
                this.replyVoice(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.VIDEO.equals(replyType)) {
                this.replyVideo(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.MUSIC.equals(replyType)) {
                this.replyMusic(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.NEWS.equals(replyType)) {
                this.replyNews(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.MPNEWS.equals(replyType)) {
                this.replyMpNews(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.WXCARD.equals(replyType)) {
                this.replyWxCard(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.MINIPROGRAMPAGE.equals(replyType)) {
                this.replyMiniProgram(toUser, replyContent);
            } else if (WxConsts.KefuMsgType.MSGMENU.equals(replyType)) {
                this.replyMsgMenu(toUser, replyContent);
            } else if ("mixed".equals(replyType)) {
                // 混合消息类型：包含文本和多个媒体文件
                this.replyMixed(toUser, replyContent);
            }
        } catch (Exception e) {
            logger.error("自动回复出错：", e);
        }
    }

    /**
     * 检查回复类型是否支持混合内容（文本+媒体文件）
     * 支持的媒体类型：IMAGE、VIDEO等
     * 这些类型如果内容包含文本和多个mediaId，会自动转换为mixed类型处理
     * 
     * @param replyType 回复类型
     * @return true表示支持混合内容自动转换
     */
    default boolean isMediaTypeSupportingMixedContent(String replyType) {
        if (replyType == null) {
            return false;
        }
        // 支持混合内容的媒体类型列表：IMAGE、VIDEO等
        // 可以根据需要扩展其他类型
        return WxConsts.KefuMsgType.IMAGE.equals(replyType) 
            || WxConsts.KefuMsgType.VIDEO.equals(replyType);
    }

    /**
     * 检测内容是否为混合消息（包含文本和多个mediaId）
     * @param content 消息内容
     * @return true表示是混合消息
     */
    default boolean isMixedContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        
        // 优先检查新格式：$IMAGE{{...}} 或 $VIDEO{{...}}
        java.util.regex.Pattern newFormatPattern = java.util.regex.Pattern.compile("\\$(IMAGE|VIDEO)\\{\\{[^}]+\\}\\}");
        if (newFormatPattern.matcher(content).find()) {
            return true;
        }
        
        // 兼容旧格式：匹配mediaId的正则表达式：60-70位字符
        java.util.regex.Pattern mediaIdPattern = java.util.regex.Pattern.compile("\\b([A-Za-z0-9_-]{60,70})\\b");
        java.util.regex.Matcher matcher = mediaIdPattern.matcher(content);
        
        int mediaIdCount = 0;
        while (matcher.find()) {
            mediaIdCount++;
            if (mediaIdCount >= 2) {
                // 如果找到2个或更多mediaId，且内容长度明显超过单个mediaId，则认为是混合消息
                return true;
            }
        }
        
        // 如果找到1个mediaId，但内容包含明显的文本（长度超过100字符或包含换行），也可能是混合消息
        if (mediaIdCount == 1 && content.length() > 100) {
            // 检查是否包含明显的文本内容（去除mediaId后的长度）
            String withoutMediaId = content.replaceAll("\\b[A-Za-z0-9_-]{60,70}\\b", "").trim();
            return withoutMediaId.length() > 20; // 如果去除mediaId后还有20个字符以上的文本，认为是混合消息
        }
        
        return false;
    }

    /**
     * 回复文字消息
     */
    void replyText(String toUser, String replyContent) throws WxErrorException;

    /**
     * 回复图片消息
     */
    void replyImage(String toUser, String mediaId) throws WxErrorException;

    /**
     * 回复录音消息
     */
    void replyVoice(String toUser, String mediaId) throws WxErrorException;

    /**
     * 回复视频消息
     */
    void replyVideo(String toUser, String mediaId) throws WxErrorException;

    /**
     * 回复音乐消息
     */
    void replyMusic(String toUser, String mediaId) throws WxErrorException;

    /**
     * 回复图文消息（点击跳转到外链）
     * 图文消息条数限制在1条以内
     */
    void replyNews(String toUser, String newsInfoJson) throws WxErrorException;

    /**
     * 回复公众号文章消息（点击跳转到图文消息页面）
     * 图文消息条数限制在1条以内
     */
    void replyMpNews(String toUser, String mediaId) throws WxErrorException;

    /**
     * 回复卡券消息
     */
    void replyWxCard(String toUser, String cardId) throws WxErrorException;

    /**
     * 回复小程序消息
     */
    void replyMiniProgram(String toUser, String miniProgramInfoJson) throws WxErrorException;

    /**
     * 回复菜单消息
     */
    void replyMsgMenu(String toUser, String msgMenusJson) throws WxErrorException;

    /**
     * 回复混合消息（包含文本和多个媒体文件）
     * 格式：文本内容中可以包含多个mediaId，系统会自动识别并分别发送
     * mediaId格式：64位字符的字符串（微信媒体ID标准格式）
     * 注意：由于微信限制，文本和媒体文件需要分别发送多条消息
     * @param toUser 用户openid
     * @param mixedContent 混合内容，包含文本和mediaId
     * @throws WxErrorException
     */
    void replyMixed(String toUser, String mixedContent) throws WxErrorException;
}
