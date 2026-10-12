-- ============================================================
-- 用户内容发布审核 —— 增量脚本
-- 用户提交科普快讯/科普视频（B站外链），管理员审核通过后前台展示
-- ============================================================

CREATE TABLE IF NOT EXISTS `user_submission` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '发布ID',
  `user_id`      BIGINT       NOT NULL COMMENT '提交用户ID（关联 sys_user.id）',
  `type`         VARCHAR(10)  NOT NULL DEFAULT 'news' COMMENT '类型：news-科普快讯，course-科普视频',
  `title`        VARCHAR(120) NOT NULL COMMENT '标题',
  `heritage_id`  BIGINT       DEFAULT NULL COMMENT '关联非遗项目ID（可空，关联 heritage_info.id）',
  `content`      TEXT         COMMENT '快讯正文 / 视频简介',
  `video_url`    VARCHAR(500) DEFAULT NULL COMMENT '视频链接（B站视频页或检索页）',
  `status`       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-未通过',
  `review_note`  VARCHAR(255) DEFAULT NULL COMMENT '审核意见（驳回原因）',
  `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `submit_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `review_time`  DATETIME     DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户内容发布审核表';
