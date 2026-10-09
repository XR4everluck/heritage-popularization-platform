-- ============================================================
-- 阶段五增量：科普快讯（非遗短文）表
--
-- 幂等：可重复执行（建表用 IF NOT EXISTS）。
-- 用法：mysql -h<host> -P<port> -u<user> -p --default-character-set=utf8mb4 <库名> < upgrade_phase5.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS heritage_news (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '快讯ID',
    title         VARCHAR(200) NOT NULL                  COMMENT '标题',
    content       TEXT         NULL                      COMMENT '正文内容',
    heritage_id   BIGINT       NULL                      COMMENT '关联非遗项目ID（可空）',
    heritage_name VARCHAR(100) NULL                      COMMENT '非遗项目名称（冗余，便于列表展示）',
    cover_image   VARCHAR(255) NULL                      COMMENT '封面图URL',
    is_top        TINYINT      NOT NULL DEFAULT 0        COMMENT '是否置顶：0-否，1-是',
    status        TINYINT      NOT NULL DEFAULT 0        COMMENT '状态：0-草稿，1-发布',
    sort          INT          NOT NULL DEFAULT 0        COMMENT '排序序号',
    publish_time  DATETIME     NULL                      COMMENT '发布时间',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator       VARCHAR(50)  NULL                      COMMENT '创建者',
    updater       VARCHAR(50)  NULL                      COMMENT '更新者',
    deleted       TINYINT      NOT NULL DEFAULT 0        COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_news_heritage_id (heritage_id),
    INDEX idx_news_status (status),
    INDEX idx_news_is_top (is_top)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='非遗科普快讯（短文）';

-- 示例快讯（仅当表为空时插入）
SET @news_empty = (SELECT COUNT(*) = 0 FROM heritage_news);
INSERT INTO heritage_news (title, content, is_top, status, sort, publish_time)
SELECT t.title, t.content, t.is_top, 1, t.sort, NOW() FROM (
              SELECT '一分钟读懂：什么是非物质文化遗产？' AS title,
                     '非物质文化遗产指各族人民世代相传、并视为其文化遗产组成部分的各种传统文化表现形式，以及与之相关的实物和场所。它强调的不是物件本身，而是技艺、知识与活态传承。' AS content,
                     1 AS is_top, 0 AS sort
    UNION ALL SELECT '为什么非遗保护强调活态传承？',
                     '非遗的核心是人的技艺与记忆。只有传承人还在实践、还在带徒授艺，这项技艺才是活的。因此保护非遗，关键在保护传承人和传承生态，而非仅仅保存实物或影像记录。',
                     0, 1
) AS t
WHERE @news_empty;
