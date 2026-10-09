-- 阶段三：非遗小测验 + 传承人专题
-- 1. 题库表（quiz_question）
CREATE TABLE IF NOT EXISTS `quiz_question` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题库ID',
  `heritage_id` BIGINT NOT NULL COMMENT '关联非遗项目ID',
  `question` VARCHAR(255) NOT NULL COMMENT '题目内容',
  `option_a` VARCHAR(255) NOT NULL COMMENT '选项A',
  `option_b` VARCHAR(255) NOT NULL COMMENT '选项B',
  `option_c` VARCHAR(255) NOT NULL COMMENT '选项C',
  `option_d` VARCHAR(255) NOT NULL COMMENT '选项D',
  `answer` CHAR(1) NOT NULL COMMENT '正确答案（A/B/C/D）',
  `analysis` TEXT COMMENT '答案解析',
  `score` INT NOT NULL DEFAULT 20 COMMENT '每题分值',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_heritage_id` (`heritage_id`),
  CONSTRAINT `fk_quiz_question_heritage` FOREIGN KEY (`heritage_id`) REFERENCES `heritage_info` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='非遗小测验题库';

-- 2. 答题记录表（quiz_record）
CREATE TABLE IF NOT EXISTS `quiz_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '答题记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `question_id` BIGINT NOT NULL COMMENT '题目ID',
  `is_correct` TINYINT NOT NULL DEFAULT 0 COMMENT '是否答对（0-否，1-是）',
  `answer` CHAR(1) COMMENT '用户选择的答案',
  `answer_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_question_id` (`question_id`),
  CONSTRAINT `fk_quiz_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_quiz_record_question` FOREIGN KEY (`question_id`) REFERENCES `quiz_question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='非遗小测验答题记录';

-- 3. 传承人表（inheritor）
CREATE TABLE IF NOT EXISTS `inheritor` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '传承人ID',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `avatar` VARCHAR(255) COMMENT '头像',
  `biography` TEXT COMMENT '个人简介',
  `representative_works` VARCHAR(500) COMMENT '代表作品',
  `inheritance_relation` VARCHAR(100) COMMENT '传承关系（如：国家级传承人、省级传承人）',
  `region` VARCHAR(100) COMMENT '所属地区',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_region` (`region`),
  INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='非遗传承人';

-- 4. 修改heritage_info表，添加inheritor_id字段
ALTER TABLE `heritage_info` 
ADD COLUMN `inheritor_id` BIGINT NULL COMMENT '传承人ID' AFTER `inheritor`,
ADD INDEX `idx_inheritor_id` (`inheritor_id`),
ADD CONSTRAINT `fk_heritage_inheritor` FOREIGN KEY (`inheritor_id`) REFERENCES `inheritor` (`id`) ON DELETE SET NULL;

-- 5. 修改user表，添加total_score字段
ALTER TABLE `user` 
ADD COLUMN `total_score` INT NOT NULL DEFAULT 0 COMMENT '累计积分' AFTER `last_login_time`;

-- 6. 添加题库示例数据（可选）
INSERT INTO `quiz_question` (`heritage_id`, `question`, `option_a`, `option_b`, `option_c`, `option_d`, `answer`, `analysis`, `score`) VALUES
(1, '昆曲起源于哪个朝代？', '唐代', '宋代', '元代', '明代', 'C', '昆曲起源于元代，是汉族传统戏曲中最古老的剧种之一。', 20),
(1, '昆曲的四大声腔不包括以下哪项？', '昆山腔', '海盐腔', '余姚腔', '黄梅腔', 'D', '昆曲的四大声腔是昆山腔、海盐腔、余姚腔、弋阳腔。', 20),
(2, '剪纸艺术主要流行于哪个地区？', '东北地区', '西北地区', '西南地区', '华北地区', 'B', '剪纸艺术主要流行于西北地区，特别是陕西、甘肃等地。', 20),
(2, '以下哪种剪纸技法被称为"阳刻"？', '镂空部分保留', '镂空部分去除', '全部保留', '全部去除', 'A', '阳刻是指保留图案线条，镂空其余部分。', 20),
(3, '皮影戏起源于哪个朝代？', '汉代', '唐代', '宋代', '元代', 'A', '皮影戏起源于汉代，是中国古老的民间艺术形式。', 20);

-- 7. 添加传承人示例数据（可选）
INSERT INTO `inheritor` (`name`, `avatar`, `biography`, `representative_works`, `inheritance_relation`, `region`) VALUES
('张三', 'https://example.com/avatar1.jpg', '国家级昆曲传承人，从事昆曲表演30余年。', '《牡丹亭》《长生殿》', '国家级传承人', '江苏苏州'),
('李四', 'https://example.com/avatar2.jpg', '著名剪纸艺术家，作品多次获奖。', '《十二生肖》《福字系列》', '省级传承人', '陕西延安'),
('王五', 'https://example.com/avatar3.jpg', '皮影戏大师，擅长传统皮影制作。', '《西游记》《三国演义》', '市级传承人', '河北唐山');