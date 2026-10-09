-- ============================================================
-- 阶段三增量：非遗小测验（题库 + 答题记录）+ 传承人专题
--
-- 幂等：可重复执行（建表用 IF NOT EXISTS；加列先查 information_schema）。
-- 用法：mysql -h<host> -P<port> -u<user> -p --default-character-set=utf8mb4 <库名> < upgrade_phase3.sql
--
-- 说明：本脚本不使用外键约束。原因是逻辑删除（deleted 标记）与级联外键
--       语义冲突，且 quiz_record 会随用户/题目删除而需要保留历史统计。
--       用户表真实表名为 sys_user。
-- ============================================================

-- 1. 题库表 quiz_question
CREATE TABLE IF NOT EXISTS quiz_question (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '题库ID',
    question      VARCHAR(255) NOT NULL                  COMMENT '题目内容',
    option_a      VARCHAR(255) NOT NULL                  COMMENT '选项A',
    option_b      VARCHAR(255) NOT NULL                  COMMENT '选项B',
    option_c      VARCHAR(255) NOT NULL                  COMMENT '选项C',
    option_d      VARCHAR(255) NOT NULL                  COMMENT '选项D',
    correct_answer CHAR(1)     NOT NULL                  COMMENT '正确答案（A/B/C/D）',
    analysis      TEXT         NULL                      COMMENT '答案解析',
    score         INT          NOT NULL DEFAULT 20       COMMENT '每题分值',
    difficulty    TINYINT      NOT NULL DEFAULT 1        COMMENT '难度：1-简单，2-中等，3-困难',
    heritage_id   BIGINT       NULL                      COMMENT '关联非遗项目ID',
    heritage_name VARCHAR(100) NULL                      COMMENT '非遗项目名称（冗余，便于列表展示）',
    question_type TINYINT      NOT NULL DEFAULT 1        COMMENT '题型：1-单选题，2-多选题，3-判断题',
    status        TINYINT      NOT NULL DEFAULT 1        COMMENT '状态：0-禁用，1-启用',
    sort          INT          NOT NULL DEFAULT 0        COMMENT '排序序号',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator       VARCHAR(50)  NULL                      COMMENT '创建者',
    updater       VARCHAR(50)  NULL                      COMMENT '更新者',
    deleted       TINYINT      NOT NULL DEFAULT 0        COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_qq_heritage_id (heritage_id),
    INDEX idx_qq_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='非遗小测验题库';

-- 2. 答题记录表 quiz_record
CREATE TABLE IF NOT EXISTS quiz_record (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY        COMMENT '答题记录ID',
    user_id     BIGINT       NOT NULL                    COMMENT '用户ID',
    heritage_id BIGINT       NULL                        COMMENT '非遗项目ID',
    question_id BIGINT       NOT NULL                    COMMENT '题目ID',
    user_answer VARCHAR(10)  NULL                        COMMENT '用户提交的答案',
    is_correct  TINYINT      NOT NULL DEFAULT 0          COMMENT '是否答对：0-否，1-是',
    score       INT          NOT NULL DEFAULT 0          COMMENT '本题得分',
    duration    INT          NULL                        COMMENT '答题用时（秒）',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
    update_time DATETIME     NULL                        COMMENT '更新时间',
    INDEX idx_qr_user_id (user_id),
    INDEX idx_qr_question_id (question_id),
    INDEX idx_qr_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='非遗小测验答题记录';

-- 3. 传承人表 inheritor
CREATE TABLE IF NOT EXISTS inheritor (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY      COMMENT '传承人ID',
    name          VARCHAR(50)  NOT NULL                  COMMENT '姓名',
    title         VARCHAR(100) NULL                      COMMENT '头衔（如：国家级代表性传承人）',
    avatar        VARCHAR(255) NULL                      COMMENT '头像图片URL',
    introduction  TEXT         NULL                      COMMENT '个人简介',
    experience    TEXT         NULL                      COMMENT '传承经历',
    achievements  TEXT         NULL                      COMMENT '荣誉成就',
    tags          VARCHAR(255) NULL                      COMMENT '标签（多个用英文逗号分隔）',
    heritage_id   BIGINT       NULL                      COMMENT '关联非遗项目ID',
    heritage_name VARCHAR(100) NULL                      COMMENT '非遗项目名称（冗余，便于列表展示）',
    status        TINYINT      NOT NULL DEFAULT 1        COMMENT '状态：0-禁用，1-启用',
    sort          INT          NOT NULL DEFAULT 0        COMMENT '排序序号',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator       VARCHAR(50)  NULL                      COMMENT '创建者',
    updater       VARCHAR(50)  NULL                      COMMENT '更新者',
    deleted       TINYINT      NOT NULL DEFAULT 0        COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_inh_heritage_id (heritage_id),
    INDEX idx_inh_status (status),
    INDEX idx_inh_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='非遗传承人';

-- 4. heritage_info 新增 inheritor_id：关联传承人
SET @ddl = (SELECT IF(COUNT(*) = 0,
    'ALTER TABLE heritage_info ADD COLUMN inheritor_id BIGINT NULL COMMENT ''传承人ID'' AFTER inheritor',
    'DO 0')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'heritage_info' AND column_name = 'inheritor_id');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5. sys_user 新增 total_score：累计积分
SET @ddl = (SELECT IF(COUNT(*) = 0,
    'ALTER TABLE sys_user ADD COLUMN total_score INT NOT NULL DEFAULT 0 COMMENT ''累计积分''',
    'DO 0')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'sys_user' AND column_name = 'total_score');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 6. 示例题库（仅当表为空时插入；heritage_id 取已存在的非遗项目）
SET @qq_empty = (SELECT COUNT(*) = 0 FROM quiz_question);
INSERT INTO quiz_question (question, option_a, option_b, option_c, option_d, correct_answer, analysis, score, heritage_id)
SELECT t.question, t.a, t.b, t.c, t.d, t.ans, t.ana, 20, (SELECT id FROM heritage_info WHERE deleted = 0 ORDER BY id LIMIT 1)
FROM (
              SELECT '昆曲起源于哪个朝代？' AS question, '唐代' AS a, '宋代' AS b, '元代' AS c, '明代' AS d, 'C' AS ans, '昆曲起源于元代，是汉族传统戏曲中最古老的剧种之一。' AS ana
    UNION ALL SELECT '昆曲的四大声腔不包括以下哪项？', '昆山腔', '海盐腔', '余姚腔', '黄梅腔', 'D', '昆曲的四大声腔是昆山腔、海盐腔、余姚腔、弋阳腔。'
    UNION ALL SELECT '皮影戏起源于哪个朝代？', '汉代', '唐代', '宋代', '元代', 'A', '皮影戏起源于汉代，是中国古老的民间艺术形式。'
    UNION ALL SELECT '中国剪纸艺术于哪一年被列入人类非遗名录？', '2001年', '2009年', '2016年', '2020年', 'B', '中国剪纸艺术在2009年被列入联合国教科文组织人类非物质文化遗产代表作名录。'
    UNION ALL SELECT '太极拳于哪一年成功申遗？', '2016年', '2018年', '2020年', '2022年', 'C', '太极拳融合阴阳哲学思想，2020年成功申遗。'
) AS t
WHERE @qq_empty AND EXISTS (SELECT 1 FROM heritage_info WHERE deleted = 0);

-- 7. 示例传承人（仅当表为空时插入）
SET @inh_empty = (SELECT COUNT(*) = 0 FROM inheritor);
INSERT INTO inheritor (name, title, avatar, introduction, experience, achievements, tags, status, sort)
SELECT t.name, t.title, t.avatar, t.intro, t.exp, t.ach, t.tags, 1, t.sort FROM (
              SELECT '张继青' AS name, '国家级代表性传承人' AS title, NULL AS avatar,
                     '昆曲表演艺术家，工旦角，师承昆曲传字辈名家。' AS intro,
                     '1950年代入江苏省苏昆剧团学艺，长期从事昆曲表演与教学。' AS exp,
                     '中国戏剧梅花奖得主，被誉为昆曲皇后。' AS ach, '昆曲,旦角,国家级' AS tags, 0 AS sort
    UNION ALL SELECT '李秀芳', '省级代表性传承人', NULL,
                     '陕北剪纸艺术家，作品以粗犷质朴、寓意吉祥见长。', '自幼随祖母学习剪纸，从事剪纸创作五十余年。',
                     '作品多次入选全国民间美术展览并获奖。', '剪纸,陕北,省级', 1
    UNION ALL SELECT '王天稳', '市级代表性传承人', NULL,
                     '皮影戏表演与皮影雕刻艺人，擅长传统影人制作。', '少年时随父学艺，掌握皮影雕刻与操纵全套技艺。',
                     '多次赴海外参加文化交流演出。', '皮影戏,雕刻,市级', 2
) AS t
WHERE @inh_empty;

-- 8. 建立「传承人 ↔ 非遗项目」双向关联（仅补空缺，不覆盖后台已有配置）
--    前台传承人详情页的「关联非遗项目」「相关科普视频」两个页签依赖此关联
UPDATE inheritor SET heritage_id = (SELECT id FROM heritage_info WHERE name = '昆曲' AND deleted = 0 LIMIT 1),
                     heritage_name = '昆曲'
WHERE name = '张继青' AND heritage_id IS NULL
  AND EXISTS (SELECT 1 FROM heritage_info WHERE name = '昆曲' AND deleted = 0);

UPDATE inheritor SET heritage_id = (SELECT id FROM heritage_info WHERE name = '中国剪纸' AND deleted = 0 LIMIT 1),
                     heritage_name = '中国剪纸'
WHERE name = '李秀芳' AND heritage_id IS NULL
  AND EXISTS (SELECT 1 FROM heritage_info WHERE name = '中国剪纸' AND deleted = 0);

UPDATE heritage_info SET inheritor_id = (SELECT id FROM inheritor WHERE name = '张继青' LIMIT 1)
WHERE name = '昆曲' AND inheritor_id IS NULL
  AND EXISTS (SELECT 1 FROM inheritor WHERE name = '张继青');

UPDATE heritage_info SET inheritor_id = (SELECT id FROM inheritor WHERE name = '李秀芳' LIMIT 1)
WHERE name = '中国剪纸' AND inheritor_id IS NULL
  AND EXISTS (SELECT 1 FROM inheritor WHERE name = '李秀芳');
