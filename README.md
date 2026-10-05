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

### ★ 一键启动（推荐）

双击项目根目录的 **`一键启动.bat`**：
- 首次运行会要求输入一次 MySQL 密码（可选择记住，保存在本地 `.db_secret`，已被 git 忽略）；
- 自动检查数据库、缺库时自动导入建表脚本、自动打包后端、启动前后端并打开浏览器；
- 停止系统：运行 `一键启动.bat stop`。

### 手动启动

#### 1. 初始化数据库

1. 安装 MySQL 8.0 并启动服务；
2. 执行建表脚本：
   ```
   mysql -uroot -p --default-character-set=utf8mb4 < sql\heritage_teaching_platform.sql
   ```
   （或在 Navicat 等工具中直接运行 `sql/heritage_teaching_platform.sql`）
3. （推荐）执行种子数据扩充脚本，获得 33 个非遗项目、10 门课程（37 个章节均配真实科普视频）、轮播图与公告等完整演示数据：
   ```
   mysql -uroot -p --default-character-set=utf8mb4 < sql\seed_expansion.sql
   ```
   扩充脚本可重复执行（自带幂等守卫），不修改表结构；
4. 建表脚本自动创建数据库 `heritage_teaching`、10 张表及基础演示数据。

#### 2. 启动后端（阶段2已就绪）

1. 修改 `backend/src/main/resources/application.yml` 中的数据库账号密码（`TODO` 注释处）；
2. 启动：`cd backend && mvn spring-boot:run`（或 IDEA 直接运行 `HeritageApplication`）；
3. 自检：访问 [http://localhost:8080/api/hello](http://localhost:8080/api/hello) 返回 `{"code":200,...}` 即成功，`/api/hello/db` 验证数据库连通。

#### 3. 启动前端

1. `cd frontend && npm install`（首次）；
2. `npm run dev`；
3. 访问 [http://localhost:5173](http://localhost:5173)。

**初始化账号（密码均为 `123456`）**

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 管理员 | admin | 123456 |
| 普通用户 | user1 | 123456 |

## 阶段计划与进度

- [x] **阶段1：系统需求分析与数据库设计** —— 建表 SQL、ER 图文字说明、表作用说明、设计规范（`docs/01-需求分析与数据库设计.md`）
- [x] **阶段1-1：种子数据批量扩充** —— 新增 29 个非遗项目（共 33 个，覆盖国家级/省级/市级与六大分类，详细内容均 200 字以上）、8 门课程 32 个新章节（全部 37 个章节配真实可播放的B站科普视频）、封面图与轮播图均使用已验证的真实外链，另扩充用户/评论/收藏/学习进度/公告演示数据（`sql/seed_expansion.sql`，可重复执行）
- [x] **阶段2：后端基础框架搭建** —— SpringBoot 2.7.18 + MyBatis-Plus 3.5.3.1 骨架、pom 依赖、application.yml、Result 统一返回、全局异常处理、跨域配置、分页与逻辑删除配置（`docs/02-后端项目骨架说明.md`）
- [x] **阶段3：实体层 + 数据层 + 业务层** —— 10 张表的 Entity（@TableName/@TableId/@TableField/@TableLogic）、Mapper（BaseMapper）、Service（IService）/ServiceImpl（40 个类，纯 MP 内置方法，无 XML；`docs/03-实体层与业务层说明.md`）
- [x] **阶段4：后端业务接口层** —— 前台 8 个 + 后台 8 个 Controller（完整 REST 接口）、DTO/VO 分层、BCrypt 注册登录、收藏/进度/评论跨表业务组装（`docs/04-接口层说明.md`）
- [x] **阶段5：毕设增强功能（JWT 认证 + 文件上传）** —— jjwt 签发/校验、登录返回 token、拦截器保护接口（白名单放行公开接口）、admin 角色权限、图片/视频本地上传与静态映射（`docs/05-JWT认证与文件上传说明.md`）
- [x] **阶段6：Vue3 前端（前台 + 后台）** —— Vue3 + Vite + Element Plus + Pinia + axios 封装，前台 6 页 + 后台 10 页，路由守卫、富文本、图片/视频上传、浏览器 E2E 验证（`docs/06-前端项目说明.md`）
- [x] **阶段7：系统联调与测试文档** —— 部署运行文档、完整接口文档（11 个模块 40+ 接口含返回示例）、18 条功能测试用例；后端 jar 打包并以 `java -jar` 连库实测通过（`docs/07-部署运行文档.md`、`docs/08-接口文档.md`、`docs/09-系统测试用例.md`）

> 执行原则：严格按阶段顺序执行，每完成一个阶段本地运行验证无误后进入下一阶段，并保存一次 git 提交。
