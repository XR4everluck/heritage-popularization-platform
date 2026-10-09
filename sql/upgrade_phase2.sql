-- ============================================================
-- 阶段二增量：内容形态升级 + 科普快讯标记 + 冷知识表
--
-- 幂等：可重复执行（建表用 IF NOT EXISTS；加列先查 information_schema）。
-- 用法：mysql -h<host> -P<port> -u<user> -p --default-character-set=utf8mb4 <库名> < upgrade_phase2.sql
--       注意必须指定目标库名，脚本内部通过 DATABASE() 判断列是否已存在。
-- ============================================================

-- 1. course_chapter 新增 content_type：区分视频/图文/音频内容形态
SET @ddl = (SELECT IF(COUNT(*) = 0,
    'ALTER TABLE course_chapter ADD COLUMN content_type VARCHAR(20) NOT NULL DEFAULT ''video'' COMMENT ''内容形态：video(视频)、article(图文)、audio(音频)'' AFTER sort',
    'DO 0')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'course_chapter' AND column_name = 'content_type');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. heritage_info 新增 is_news：标记科普快讯/短文
SET @ddl = (SELECT IF(COUNT(*) = 0,
    'ALTER TABLE heritage_info ADD COLUMN is_news TINYINT NOT NULL DEFAULT 0 COMMENT ''是否为科普快讯：0-否，1-是'' AFTER endanger_level',
    'DO 0')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'heritage_info' AND column_name = 'is_news');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. 冷知识表 heritage_tip
CREATE TABLE IF NOT EXISTS heritage_tip (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY          COMMENT '冷知识ID',
    title       VARCHAR(100)  NULL                          COMMENT '标题（后台列表展示）',
    content     VARCHAR(500)  NOT NULL                      COMMENT '冷知识内容',
    heritage_id BIGINT        NULL                          COMMENT '关联非遗项目ID（空表示通用冷知识）',
    status      TINYINT       NOT NULL DEFAULT 1            COMMENT '状态：0-禁用，1-启用',
    sort        INT           NOT NULL DEFAULT 0            COMMENT '排序序号，越小越靠前',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT       NOT NULL DEFAULT 0            COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_tip_heritage_id (heritage_id),
    INDEX idx_tip_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='非遗冷知识表';

-- 4. 示例冷知识（仅当表为空时插入，避免重复执行时数据翻倍）
SET @tip_empty = (SELECT COUNT(*) = 0 FROM heritage_tip);
INSERT INTO heritage_tip (title, content, status, sort)
SELECT t.title, t.content, 1, t.sort FROM (
              SELECT '百戏之祖' AS title, '昆曲是中国最古老的戏曲形式之一，被誉为百戏之祖，已有600多年历史' AS content, 0 AS sort
    UNION ALL SELECT '人类非遗', '中国剪纸艺术在2009年被联合国教科文组织列入人类非物质文化遗产代表作名录', 1
    UNION ALL SELECT '千年影戏', '皮影戏最早可追溯到西汉，比西方皮影早了近千年', 2
    UNION ALL SELECT '世界非遗', '二十四节气在2016年被正式列入联合国教科文组织人类非物质文化遗产代表作名录', 3
    UNION ALL SELECT '古琴三千年', '古琴是中国最古老的弹拨乐器之一，已有3000多年历史，被列入世界非遗', 4
    UNION ALL SELECT '书法即艺术', '中国书法不仅是一种书写方式，更被视为一种独特的视觉艺术形式', 5
    UNION ALL SELECT '太极申遗', '太极拳融合了阴阳哲学思想，2020年成功申遗，成为中国传统武术的代表', 6
    UNION ALL SELECT '制茶技艺', '中国传统制茶技艺及其相关习俗于2022年列入联合国教科文组织非遗名录', 7
) AS t
WHERE @tip_empty;
