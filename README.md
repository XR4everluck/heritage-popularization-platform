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

## 快速开始

### 1. 初始化数据库

1. 安装 MySQL 8.0 并启动服务；
2. 执行建表脚本：
   ```
   mysql -uroot -p --default-character-set=utf8mb4 < sql\heritage_teaching_platform.sql
   ```
   （或在 Navicat 等工具中直接运行 `sql/heritage_teaching_platform.sql`）
3. 脚本自动创建数据库 `heritage_teaching`、10 张表及演示数据。

### 2. 启动后端（阶段2已就绪）

1. 修改 `backend/src/main/resources/application.yml` 中的数据库账号密码（`TODO` 注释处）；
2. 启动：`cd backend && mvn spring-boot:run`（或 IDEA 直接运行 `HeritageApplication`）；
3. 自检：访问 [http://localhost:8080/api/hello](http://localhost:8080/api/hello) 返回 `{"code":200,...}` 即成功，`/api/hello/db` 验证数据库连通。

**初始化账号（密码均为 `123456`）**

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 管理员 | admin | 123456 |
| 普通用户 | user1 | 123456 |

## 阶段计划与进度

- [x] **阶段1：系统需求分析与数据库设计** —— 建表 SQL、ER 图文字说明、表作用说明、设计规范（`docs/01-需求分析与数据库设计.md`）
- [x] **阶段2：后端基础框架搭建** —— SpringBoot 2.7.18 + MyBatis-Plus 3.5.3.1 骨架、pom 依赖、application.yml、Result 统一返回、全局异常处理、跨域配置、分页与逻辑删除配置（`docs/02-后端项目骨架说明.md`）
- [x] **阶段3：实体层 + 数据层 + 业务层** —— 10 张表的 Entity（@TableName/@TableId/@TableField/@TableLogic）、Mapper（BaseMapper）、Service（IService）/ServiceImpl（40 个类，纯 MP 内置方法，无 XML；`docs/03-实体层与业务层说明.md`）
- [x] **阶段4：后端业务接口层** —— 前台 8 个 + 后台 8 个 Controller（完整 REST 接口）、DTO/VO 分层、BCrypt 注册登录、收藏/进度/评论跨表业务组装（`docs/04-接口层说明.md`；暂未接入 JWT，用户身份调试期由 userId 参数传入）
- [ ] 阶段5：用户前台开发 —— Vue3 + Element Plus（首页、非遗列表与详情、课程学习、个人中心）
- [ ] 阶段6：管理后台开发 —— 布局与各管理模块页面
- [ ] 阶段7：JWT 鉴权接入、前后端联调、功能测试、打包部署与论文素材整理

> 执行原则：严格按阶段顺序执行，每完成一个阶段本地运行验证无误后进入下一阶段，并保存一次 git 提交。
