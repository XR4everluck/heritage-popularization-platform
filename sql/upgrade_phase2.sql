-- ============================================================
-- 阶段二：内容形态升级 + 首页科普化重构
-- 幂等执行：字段用 ADD COLUMN IF NOT EXISTS（MySQL 8.0.29+），表用 IF NOT EXISTS
-- ============================================================

-- 1. course_chapter 表新增 content_type 字段：区分视频/图文/音频内容形态
ALTER TABLE course_chapter ADD COLUMN IF NOT EXISTS content_type VARCHAR(20) NOT NULL DEFAULT 'video'
    COMMENT '内容形态：video(视频)、article(图文)、audio(音频)' AFTER sort;

-- 2. heritage_info 表新增 is_news 字段：标记科普快讯/短文
ALTER TABLE heritage_info ADD COLUMN IF NOT EXISTS is_news TINYINT NOT NULL DEFAULT 0
    COMMENT '是否为科普快讯：0-否，1-是' AFTER endanger_level;
CREATE INDEX IF NOT EXISTS idx_is_news ON heritage_info(is_news);

-- 3. 新增冷知识表 heritage_tip
CREATE TABLE IF NOT EXISTS heritage_tip (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '冷知识ID',
    content     VARCHAR(500) NOT NULL              COMMENT '冷知识内容',
    heritage_id BIGINT       NULL                  COMMENT '关联非遗项目ID（可空，空表示通用冷知识）',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_heritage_id (heritage_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='非遗冷知识表';

-- 4. 插入一些示例冷知识数据
INSERT INTO heritage_tip (content, heritage_id) VALUES
('昆曲是中国最古老的戏曲形式之一，被誉为"百戏之祖"，已有600多年历史', NULL),
('中国剪纸艺术在2009年被联合国教科文组织列入人类非物质文化遗产代表作名录', NULL),
('皮影戏最早可追溯到西汉，比西方皮影早了近千年', NULL),
('二十四节气在2016年被正式列入联合国教科文组织人类非物质文化遗产代表作名录', NULL),
('古琴是中国最古老的弹拨乐器之一，已有3000多年历史，被列入世界非遗', NULL),
('中国书法不仅是一种书写方式，更被视为一种独特的视觉艺术形式', NULL),
('太极拳融合了阴阳哲学思想，2020年成功申遗，成为中国传统武术的代表', NULL),
('中国传统制茶技艺及其相关习俗于2022年列入联合国教科文组织非遗名录', NULL);
