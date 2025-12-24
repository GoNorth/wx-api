-- 消息发送进度表
-- 用于记录混合消息的发送状态，支持断点续发
DROP TABLE IF EXISTS `wx_msg_send_progress`;
CREATE TABLE `wx_msg_send_progress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_hash` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '消息内容的hash code，用于标识同一条消息',
  `appid` char(20) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '公众号appid',
  `openid` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '用户openid',
  `original_content` text CHARACTER SET utf8mb4 NOT NULL COMMENT '原始消息内容',
  `message_items` text CHARACTER SET utf8mb4 NOT NULL COMMENT '消息项列表（JSON格式）',
  `total_count` int(11) NOT NULL DEFAULT 0 COMMENT '总消息数量',
  `sent_count` int(11) NOT NULL DEFAULT 0 COMMENT '已发送的消息数量（从0开始）',
  `completed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已完成（0:未完成，1:已完成）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_content_hash_openid` (`content_hash`, `openid`) USING BTREE COMMENT '内容hash和openid索引',
  INDEX `idx_openid_completed` (`openid`, `completed`) USING BTREE COMMENT 'openid和完成状态索引',
  INDEX `idx_update_time` (`update_time`) USING BTREE COMMENT '更新时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息发送进度记录表';

