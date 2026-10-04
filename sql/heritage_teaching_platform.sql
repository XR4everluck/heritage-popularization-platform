-- ============================================================
-- 非遗知识教学平台 —— 数据库初始化脚本
-- 适用数据库 : MySQL 8.0
-- 字符集     : utf8mb4 / utf8mb4_general_ci
-- 存储引擎   : InnoDB
-- 设计约定   :
--   1. 所有表主键均为 BIGINT 类型自增主键；
--   2. 不建立物理外键，表间关联由业务层维护，关联字段建立普通索引；
--   3. 业务主体表通过 deleted 字段实现逻辑删除（0-未删除，1-已删除）；
--   4. 时间字段统一使用 datetime 类型；
--   5. 用户密码使用 BCrypt 算法加密存储，不保存明文。
-- 执行方式   :
--   方式一：mysql -uroot -p --default-character-set=utf8mb4 < heritage_teaching_platform.sql
--   方式二：在 Navicat / DataGrip / IDEA Database 工具中直接运行本脚本
-- 初始化账号（密码均为 123456）：
--   管理员   admin / 123456
--   普通用户 user1  / 123456
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 0. 创建数据库
-- ------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS `heritage_teaching`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;
USE `heritage_teaching`;

-- ------------------------------------------------------------
-- 1. 系统用户表 sys_user
--    存储普通用户与管理员账号，role 字段区分角色
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键自增',
  `username`     VARCHAR(50)  NOT NULL                COMMENT '用户名（登录账号），唯一',
  `password`     VARCHAR(100) NOT NULL                COMMENT '密码（BCrypt加密存储）',
  `nickname`     VARCHAR(50)  DEFAULT NULL            COMMENT '用户昵称',
  `phone`        VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
  `email`        VARCHAR(100) DEFAULT NULL            COMMENT '邮箱',
  `avatar`       VARCHAR(255) DEFAULT NULL            COMMENT '头像图片地址',
  `role`         VARCHAR(20)  NOT NULL DEFAULT 'user' COMMENT '角色：user-普通用户，admin-系统管理员',
  `introduction` VARCHAR(500) DEFAULT NULL            COMMENT '个人简介',
  `status`       TINYINT      NOT NULL DEFAULT 1      COMMENT '账号状态：1-正常，0-禁用',
  `deleted`      TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除标记：0-未删除，1-已删除',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（注册时间）',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户表';

-- ------------------------------------------------------------
-- 2. 非遗分类表 heritage_category
--    非遗项目的一级分类（传统音乐、传统技艺、民俗等）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `heritage_category`;
CREATE TABLE `heritage_category` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID，主键自增',
  `name`        VARCHAR(50) NOT NULL                COMMENT '分类名称',
  `description` VARCHAR(500) DEFAULT NULL           COMMENT '分类描述',
  `icon`        VARCHAR(255) DEFAULT NULL           COMMENT '分类图标（图标名/图标图片地址）',
  `sort`        INT         NOT NULL DEFAULT 0      COMMENT '排序号（数字越小越靠前）',
  `deleted`     TINYINT     NOT NULL DEFAULT 0      COMMENT '逻辑删除标记：0-未删除，1-已删除',
  `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '非遗分类表';

-- ------------------------------------------------------------
-- 3. 非遗项目表 heritage_info
--    平台核心内容表，存储非遗项目的基础信息与图文详情
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `heritage_info`;
CREATE TABLE `heritage_info` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '非遗项目ID，主键自增',
  `category_id`     BIGINT       NOT NULL                COMMENT '分类ID（业务层关联 heritage_category.id）',
  `name`            VARCHAR(100) NOT NULL                COMMENT '非遗名称',
  `level`           VARCHAR(20)  NOT NULL DEFAULT '国家级' COMMENT '非遗级别：国家级/省级/市级',
  `region`          VARCHAR(100) DEFAULT NULL            COMMENT '所属地区',
  `inheritor`       VARCHAR(50)  DEFAULT NULL            COMMENT '代表性传承人（群体传承可填"群体传承"）',
  `summary`         VARCHAR(500) DEFAULT NULL            COMMENT '非遗简介（列表页摘要展示）',
  `content`         MEDIUMTEXT   DEFAULT NULL            COMMENT '详细内容（富文本图文详情）',
  `cover_image`     VARCHAR(255) DEFAULT NULL            COMMENT '封面图片地址',
  `view_count`      INT          NOT NULL DEFAULT 0      COMMENT '浏览量（冗余统计字段，业务层累加）',
  `collection_count` INT         NOT NULL DEFAULT 0      COMMENT '收藏数（冗余统计字段，业务层维护）',
  `publish_time`    DATETIME     DEFAULT NULL            COMMENT '发布时间（NULL 表示未发布/草稿）',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除标记：0-未删除，1-已删除',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_level` (`level`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '非遗项目表';

-- ------------------------------------------------------------
-- 4. 课程表 course
--    围绕某个非遗项目开设的教学课程
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '课程ID，主键自增',
  `heritage_id`  BIGINT       NOT NULL                COMMENT '关联非遗项目ID（业务层关联 heritage_info.id）',
  `name`         VARCHAR(100) NOT NULL                COMMENT '课程名称',
  `summary`      VARCHAR(500) DEFAULT NULL            COMMENT '课程简介',
  `cover`        VARCHAR(255) DEFAULT NULL            COMMENT '课程封面图片地址',
  `teacher`      VARCHAR(50)  DEFAULT NULL            COMMENT '讲师',
  `duration`     INT          NOT NULL DEFAULT 0      COMMENT '总时长（单位：分钟）',
  `view_count`   INT          NOT NULL DEFAULT 0      COMMENT '浏览量（冗余统计字段，业务层累加）',
  `publish_time` DATETIME     DEFAULT NULL            COMMENT '发布时间（NULL 表示未发布/草稿）',
  `deleted`      TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除标记：0-未删除，1-已删除',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_heritage_id` (`heritage_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '课程表';

-- ------------------------------------------------------------
-- 5. 课程章节表 course_chapter
--    课程的视频章节，作为课程从属数据采用物理删除
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `course_chapter`;
CREATE TABLE `course_chapter` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '章节ID，主键自增',
  `course_id`   BIGINT       NOT NULL                COMMENT '课程ID（业务层关联 course.id）',
  `title`       VARCHAR(100) NOT NULL                COMMENT '章节标题',
  `video_url`   VARCHAR(255) DEFAULT NULL            COMMENT '视频地址（文件上传后的访问路径）',
  `content`     MEDIUMTEXT   DEFAULT NULL            COMMENT '章节内容（图文讲义）',
  `sort`        INT          NOT NULL DEFAULT 0      COMMENT '排序号（数字越小越靠前）',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '课程章节表';

-- ------------------------------------------------------------
-- 6. 用户收藏表 user_collection
--    用户与非遗项目"多对多"收藏联系的关联表；
--    取消收藏即物理删除记录，无保留价值，故不设逻辑删除字段
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user_collection`;
CREATE TABLE `user_collection` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏ID，主键自增',
  `user_id`     BIGINT   NOT NULL                COMMENT '用户ID（业务层关联 sys_user.id）',
  `heritage_id` BIGINT   NOT NULL                COMMENT '非遗项目ID（业务层关联 heritage_info.id）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_heritage` (`user_id`, `heritage_id`),
  KEY `idx_heritage_id` (`heritage_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户收藏表';

-- ------------------------------------------------------------
-- 7. 评论表 user_comment
--    用户对非遗项目的评论，管理员可屏蔽违规评论
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user_comment`;
CREATE TABLE `user_comment` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '评论ID，主键自增',
  `user_id`     BIGINT        NOT NULL                COMMENT '评论用户ID（业务层关联 sys_user.id）',
  `heritage_id` BIGINT        NOT NULL                COMMENT '非遗项目ID（业务层关联 heritage_info.id）',
  `content`     VARCHAR(1000) NOT NULL                COMMENT '评论内容',
  `like_count`  INT           NOT NULL DEFAULT 0      COMMENT '点赞数（冗余统计字段，业务层维护）',
  `status`      TINYINT       NOT NULL DEFAULT 1      COMMENT '状态：1-正常，0-屏蔽（管理员审核后屏蔽）',
  `deleted`     TINYINT       NOT NULL DEFAULT 0      COMMENT '逻辑删除标记：0-未删除，1-已删除',
  `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_heritage_id` (`heritage_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '评论表';

-- ------------------------------------------------------------
-- 8. 学习进度表 study_progress
--    用户课程学习进度的关联表，业务约束：一人一章仅一条进度记录
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `study_progress`;
CREATE TABLE `study_progress` (
  `id`             BIGINT   NOT NULL AUTO_INCREMENT COMMENT '学习进度ID，主键自增',
  `user_id`        BIGINT   NOT NULL                COMMENT '用户ID（业务层关联 sys_user.id）',
  `course_id`      BIGINT   NOT NULL                COMMENT '课程ID（业务层关联 course.id，冗余便于按课程统计）',
  `chapter_id`     BIGINT   NOT NULL                COMMENT '章节ID（业务层关联 course_chapter.id）',
  `study_duration` INT      NOT NULL DEFAULT 0      COMMENT '学习时长（单位：分钟，累计值）',
  `finished`       TINYINT  NOT NULL DEFAULT 0      COMMENT '完成状态：0-未完成，1-已完成',
  `update_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（最近学习时间）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_chapter` (`user_id`, `chapter_id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '学习进度表';

-- ------------------------------------------------------------
-- 9. 轮播图表 banner
--    前台首页轮播图配置
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '轮播图ID，主键自增',
  `image`       VARCHAR(255) NOT NULL                COMMENT '图片地址',
  `link_url`    VARCHAR(255) DEFAULT NULL            COMMENT '点击跳转链接（前端路由或外部地址）',
  `title`       VARCHAR(100) DEFAULT NULL            COMMENT '标题',
  `sort`        INT          NOT NULL DEFAULT 0      COMMENT '排序号（数字越小越靠前）',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-启用，0-停用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮播图表';

-- ------------------------------------------------------------
-- 10. 系统公告表 notice
--     平台公告的发布与管理
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '公告ID，主键自增',
  `title`        VARCHAR(100) NOT NULL                COMMENT '公告标题',
  `content`      MEDIUMTEXT   NOT NULL                COMMENT '公告内容',
  `publish_time` DATETIME     DEFAULT NULL            COMMENT '发布时间（NULL 表示未发布/草稿）',
  `status`       TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-已发布，0-下架/草稿',
  `deleted`      TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除标记：0-未删除，1-已删除',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统公告表';

-- ============================================================
-- 以下为初始化演示数据（开发演示用，可按需清理）
-- 注意：heritage_info.collection_count 与 user_collection 种子数据
--       保持一致，view_count 等统计字段为演示数值
-- ============================================================

-- 1. 用户（密码均为 123456 的 BCrypt 密文）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `phone`, `email`, `role`, `introduction`) VALUES
(1, 'admin', '$2b$10$mzAq8eSHR308mTQFa1CA8OA76LSJ8lvtGTSQzLGYHzQKohCHYMPq.', '系统管理员', '13800000000', 'admin@heritage.com', 'admin', '非遗知识教学平台系统管理员'),
(2, 'user1', '$2b$10$mzAq8eSHR308mTQFa1CA8OA76LSJ8lvtGTSQzLGYHzQKohCHYMPq.', '非遗爱好者', '13900000000', 'user1@heritage.com', 'user', '热爱传统文化，正在学习非遗知识');

-- 2. 非遗分类
INSERT INTO `heritage_category` (`id`, `name`, `description`, `icon`, `sort`) VALUES
(1, '传统音乐', '民歌、器乐等传统音乐类非遗项目', '🎶', 1),
(2, '传统舞蹈', '各民族传统舞蹈类非遗项目', '💃', 2),
(3, '传统戏剧', '昆曲、京剧等传统戏剧类非遗项目', '🎭', 3),
(4, '传统技艺', '陶瓷、织染、酿造等传统手工技艺', '🏺', 4),
(5, '传统美术', '剪纸、刺绣、书法等传统美术类项目', '🖌️', 5),
(6, '民俗', '节庆礼仪、二十四节气等民俗类项目', '🎊', 6);

-- 3. 非遗项目（传承人为演示数据）
INSERT INTO `heritage_info` (`id`, `category_id`, `name`, `level`, `region`, `inheritor`, `summary`, `content`, `view_count`, `collection_count`, `publish_time`) VALUES
(1, 3, '昆曲', '国家级', '江苏省苏州市', '张继青（演示）', '昆曲又称昆剧，发源于江苏昆山，被誉为"百戏之祖"，2001年被联合国教科文组织列入首批"人类口述和非物质遗产代表作"名录。', '<p>昆曲发源于元末明初的江苏昆山，距今已有六百多年历史，唱腔婉转细腻，被称为"水磨腔"。</p><p>（此处为富文本演示内容，可在后台编辑图文详解）</p>', 328, 1, NOW()),
(2, 5, '中国剪纸', '国家级', '河北省蔚县等地', '周淑英（演示）', '中国剪纸是用剪刀或刻刀在纸上剪刻花纹的民间艺术，2009年入选联合国教科文组织人类非物质文化遗产代表作名录。', '<p>剪纸艺术承载着丰富的历史文化信息，题材涵盖吉祥纹样、戏曲人物、民俗生活等。</p><p>（此处为富文本演示内容）</p>', 256, 1, NOW()),
(3, 3, '京剧', '国家级', '北京市', '梅葆玖（演示）', '京剧形成于清代道光年间的北京，是中国影响力最大的戏曲剧种，有"国剧"之称，2010年入选人类非物质文化遗产代表作名录。', '<p>京剧融合唱、念、做、打多种表演形式，行当分为生、旦、净、丑。</p><p>（此处为富文本演示内容）</p>', 189, 0, NOW()),
(4, 6, '二十四节气', '国家级', '全国各地区', '群体传承', '二十四节气是中国人通过观察太阳周年运动而形成的时间知识体系及其实践，2016年入选人类非物质文化遗产代表作名录。', '<p>立春、雨水、惊蛰……二十四节气指导着传统农业生产和日常生活，被誉为"中国的第五大发明"。</p><p>（此处为富文本演示内容）</p>', 142, 0, NOW());

-- 4. 课程
INSERT INTO `course` (`id`, `heritage_id`, `name`, `summary`, `teacher`, `duration`, `view_count`, `publish_time`) VALUES
(1, 1, '昆曲入门：从水磨腔开始', '带您走近"百戏之祖"昆曲，了解昆曲的历史渊源、行当身段与经典剧目赏析。', '张继青（演示）', 90, 120, NOW()),
(2, 2, '剪纸基础技法十二讲', '从工具选择到阳刻阴刻，系统学习中国剪纸的基础技法，零基础也能上手。', '周淑英（演示）', 120, 86, NOW());

-- 5. 课程章节（video_url 待文件上传模块完成后由后台上传填充）
INSERT INTO `course_chapter` (`id`, `course_id`, `title`, `video_url`, `content`, `sort`) VALUES
(1, 1, '第1章 认识昆曲：六百年水磨调', NULL, '昆曲起源于江苏昆山，其唱腔细腻婉转如水磨糯米粉……', 1),
(2, 1, '第2章 昆曲的行当与身段', NULL, '昆曲行当分为生、旦、净、末、丑，各行当身段功法各具特色……', 2),
(3, 1, '第3章 经典剧目赏析：《牡丹亭·游园》', NULL, '【皂罗袍】原来姹紫嫣红开遍，似这般都付与断井颓垣……', 3),
(4, 2, '第1讲 剪纸工具与纸张选择', NULL, '剪刀、刻刀、垫板与宣纸的选择直接影响剪纸效果……', 1),
(5, 2, '第2讲 对折剪与阳刻基础', NULL, '阳刻留线、阴刻留面，先从对称纹样练起……', 2);

-- 6. 用户收藏（与 heritage_info.collection_count 保持一致）
INSERT INTO `user_collection` (`id`, `user_id`, `heritage_id`) VALUES
(1, 2, 1),
(2, 2, 2);

-- 7. 评论
INSERT INTO `user_comment` (`id`, `user_id`, `heritage_id`, `content`, `like_count`, `status`) VALUES
(1, 2, 1, '昆曲之美，百听不厌！希望平台多上一些经典剧目赏析课程。', 3, 1),
(2, 2, 4, '二十四节气是老祖宗留下的智慧，为我们感到骄傲！', 1, 1);

-- 8. 学习进度
INSERT INTO `study_progress` (`id`, `user_id`, `course_id`, `chapter_id`, `study_duration`, `finished`) VALUES
(1, 2, 1, 1, 12, 1),
(2, 2, 1, 2, 5, 0);

-- 9. 轮播图（image 为占位路径，待文件上传后替换）
INSERT INTO `banner` (`id`, `image`, `link_url`, `title`, `sort`, `status`) VALUES
(1, '/files/banner/banner-1.jpg', '/heritage/1', '非遗之美，尽在指尖', 1, 1),
(2, '/files/banner/banner-2.jpg', '/heritage/2', '一张红纸，剪出千年文脉', 2, 1),
(3, '/files/banner/banner-3.jpg', '/course/list', '名师课程，免费学习', 3, 1);

-- 10. 系统公告
INSERT INTO `notice` (`id`, `title`, `content`, `publish_time`, `status`) VALUES
(1, '非遗知识教学平台正式上线', '欢迎来到非遗知识教学平台！平台汇聚国家级、省级、市级非遗项目知识，提供体系化课程学习服务，帮助大家了解和学习中华优秀传统文化。', NOW(), 1),
(2, '关于课程内容持续更新的公告', '平台课程内容将持续更新，本周已上线《昆曲入门》《剪纸基础技法十二讲》两门课程，欢迎大家学习并留言交流。', NOW(), 1);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 脚本执行完毕，共 10 张表 + 演示数据
-- 验证语句：SELECT table_name, table_comment FROM information_schema.tables
--           WHERE table_schema = 'heritage_teaching';
-- ============================================================
