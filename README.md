# 非遗知识教学平台（SpringBoot + Vue3）

毕设项目：非遗知识教学平台，前后端分离架构，包含**用户前台展示端** + **管理员后台管理端**。

## 技术栈

| 端 | 技术 |
| --- | --- |
| 后端 | Java 8、SpringBoot 2.7.18、MyBatis-Plus 3.5.3.1、MySQL 8.0、Maven |
| 前端 | Vue3、Vite、Element Plus、Axios |
| 其他 | JWT 登录鉴权、BCrypt 密码加密、逻辑删除、业务层关联（无物理外键） |

## 目录结构（按阶段逐步生成）

```
heritage-teaching-platform
├── docs/       # 设计文档（需求分析、数据库设计，论文素材）
├── sql/        # 数据库脚本
├── backend/    # SpringBoot 后端（阶段2创建）
└── frontend/   # Vue3 前端（阶段4创建，前台+后台同工程）
```

## 快速开始（当前阶段：数据库初始化）

1. 安装 MySQL 8.0 并启动服务；
2. 执行建表脚本：
   ```
   mysql -uroot -p --default-character-set=utf8mb4 < sql\heritage_teaching_platform.sql
   ```
   （或在 Navicat 等工具中直接运行 `sql/heritage_teaching_platform.sql`）
3. 脚本自动创建数据库 `heritage_teaching`、10 张表及演示数据。

**初始化账号（密码均为 `123456`）**

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 管理员 | admin | 123456 |
| 普通用户 | user1 | 123456 |

## 阶段计划与进度

- [x] **阶段1：系统需求分析与数据库设计** —— 建表 SQL、ER 图文字说明、表作用说明、设计规范（`docs/01-需求分析与数据库设计.md`）
- [ ] 阶段2：后端基础框架搭建 —— SpringBoot 2.7.18 + MyBatis-Plus 3.5.3.1 工程初始化、统一返回结果、全局异常处理、跨域配置、JWT 登录鉴权、BCrypt 密码
- [ ] 阶段3：后端业务模块开发 —— 分类/非遗项目/课程章节/收藏/评论/学习进度/轮播图/公告/用户管理接口 + 文件上传
- [ ] 阶段4：用户前台开发 —— Vue3 + Element Plus（首页、非遗列表与详情、课程学习、个人中心）
- [ ] 阶段5：管理后台开发 —— 布局与各管理模块页面
- [ ] 阶段6：前后端联调、功能测试、打包部署与论文素材整理

> 执行原则：严格按阶段顺序执行，每完成一个阶段本地运行验证无误后进入下一阶段，并保存一次 git 提交。
