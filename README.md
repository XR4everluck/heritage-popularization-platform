# 🏮 非遗知识科普平台 (Heritage Popularization Platform)

[![Java](https://img.shields.io/badge/Java-8-blue.svg)](https://openjdk.java.net/projects/jdk/8/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-green.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3-green.svg)](https://vuejs.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> 🎨 基于 SpringBoot + Vue3 的现代化非遗知识科普平台，致力于传承和弘扬中华优秀传统文化

## ✨ 项目特色

- 🎯 **全栈现代化架构** - SpringBoot 2.7.18 + Vue3 + Vite 前后端分离
- 🗺️ **非遗地图可视化** - 高德地图集成，直观展示非遗项目分布
- 👥 **传承人社区互动** - 支持用户投稿、评论、收藏等社交功能
- 📸 **多媒体图片库** - 支持批量上传、相册展示、分类管理
- 🎭 **丰富的内容展示** - 非遗项目、传承人、课程、测验等完整模块
- 🚀 **一键启动部署** - 智能数据库初始化，开箱即用
- 🎨 **国风主题设计** - 传统美学与现代UI完美融合

## 🛠️ 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| **后端** | Java 8 + SpringBoot 2.7.18 | 核心框架，稳定可靠 |
| | MyBatis-Plus 3.5.3.1 | 数据持久化，简化开发 |
| | MySQL 8.0 | 主数据库 |
| | Maven | 项目构建管理 |
| **前端** | Vue 3 + Vite | 现代前端框架，快速构建 |
| | Element Plus | UI组件库 |
| | Axios | HTTP客户端 |
| | Pinia | 状态管理 |
| **其他** | JWT + BCrypt | 安全认证与加密 |
| | 逻辑删除 | 数据安全策略 |
| | 高德地图API | 地图服务集成 |

## 📁 项目结构

```
heritage-popularization-platform/
├── 📚 docs/               # 项目文档
│   ├── 需求分析与数据库设计.md
│   ├── 后端项目骨架说明.md
│   ├── 接口文档.md
│   ├── 部署运行文档.md
│   └── ... (21个文档)
├── 🗄️ sql/                # 数据库脚本
│   ├── heritage_teaching_platform.sql  # 主建表脚本
│   ├── seed_expansion.sql             # 种子数据
│   ├── upgrade_*.sql                  # 版本升级脚本
│   └── ...
├── 🔧 backend/           # SpringBoot 后端
│   ├── src/main/java/com/heritage/
│   │   ├── controller/    # 控制器层
│   │   ├── service/      # 业务逻辑层
│   │   ├── entity/       # 实体类
│   │   ├── mapper/       # 数据访问层
│   │   └── config/      # 配置类
│   └── pom.xml          # Maven 依赖
└── 🎨 frontend/          # Vue3 前端
    ├── src/
    │   ├── views/        # 页面组件
    │   ├── components/   # 公共组件
    │   ├── api/          # API 接口
    │   ├── utils/        # 工具函数
    │   └── styles/       # 样式文件
    └── package.json      # npm 依赖
```

## 🚀 快速开始

### 🎯 一键启动（推荐）

**Windows 用户**：双击项目根目录的 **`一键启动.bat`**

```bash
# 一键启动流程：
# 1. 检查 MySQL 连接（首次需要输入密码）
# 2. 自动创建数据库和表结构
# 3. 导入演示数据（33个非遗项目、10门课程等）
# 4. 打包并启动后端服务
# 5. 启动前端开发服务器
# 6. 自动打开浏览器访问系统
```

**停止系统**：
```bash
一键启动.bat stop
```

### 🛠️ 手动启动

#### 1. 环境准备

```bash
# 必需软件
- Java 8+
- MySQL 8.0+
- Node.js 16+
- Maven 3.6+
```

#### 2. 数据库初始化

```bash
# 创建数据库并导入主表结构
mysql -uroot -p --default-character-set=utf8mb4 < sql/heritage_teaching_platform.sql

# 导入演示数据（可选）
mysql -uroot -p --default-character-set=utf8mb4 < sql/seed_expansion.sql
```

#### 3. 后端启动

```bash
cd backend

# 配置数据库连接
# 编辑 backend/src/main/resources/application.yml

# 启动后端
mvn spring-boot:run

# 或直接运行 HeritageApplication.java
```

**后端自检**：
```bash
curl http://localhost:8080/api/hello  # 应返回 {"code":200,...}
curl http://localhost:8080/api/hello/db  # 验证数据库连通
```

#### 4. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问 http://localhost:5173
```

## 👤 初始账号

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|
| 🎭 管理员 | admin | 123456 | 全部管理权限 |
| 👤 普通用户 | user1 | 123456 | 基础浏览和交互 |

## 🌟 核心功能模块

### 🎭 前台用户端
- **首页** - 精品课程推荐、热门搜索、继续学习、猜你喜欢
- **非遗项目** - 33个非遗项目详细介绍，包含历史渊源、传承信息
- **传承人社区** - 传承人专题页面，支持互动评论和社区交流
- **课程学习** - 10门课程37个章节，支持视频播放和学习进度跟踪
- **非遗地图** - 可视化展示非遗项目地理分布，支持关联推荐
- **个人中心** - 学习数据仪表板、收藏管理、个人信息设置
- **用户投稿** - 支持图文、视频、音频多种格式的非遗内容投稿

### 🛠️ 后台管理端
- **项目管理** - 非遗项目的CRUD管理，支持富文本编辑
- **传承人管理** - 传承人信息维护和专题内容管理
- **课程管理** - 课程章节、视频资源、学习进度管理
- **投稿管理** - 用户投稿审核和管理
- **用户管理** - 用户账户、权限、活跃度管理
- **系统管理** - 轮播图、公告、分类等基础数据管理
- **数据统计** - 访问量、用户活跃度、内容统计等

### 🎨 特色功能
- **国风主题设计** - 传统美学与现代UI完美融合
- **智能推荐系统** - 基于用户行为的个性化内容推荐
- **多媒体学习** - 支持B站视频嵌入、本地视频播放
- **互动社区** - 评论、收藏、分享等社交功能
- **数据可视化** - 学习进度、地图分布等图表展示

## 📈 项目进度

### ✅ 已完成功能

| 阶段 | 功能描述 | 状态 |
|------|----------|------|
| **阶段1** | 需求分析、数据库设计、种子数据扩充 | ✅ 完成 |
| **阶段2** | 后端框架搭建、国风主题设计、首页交互升级 | ✅ 完成 |
| **阶段3** | 详情页重构、播放器升级、个人中心优化 | ✅ 完成 |
| **阶段4** | 后端接口开发、前端页面实现 | ✅ 完成 |
| **阶段5** | JWT认证、文件上传、权限管理 | ✅ 完成 |
| **阶段6** | 前后端联调、测试文档 | ✅ 完成 |
| **阶段7** | **社区功能** - 传承人社区、用户投稿、非遗地图 | ✅ 完成 |

### 🔄 最新更新

- 🆕 **社区互动系统** - 完整的传承人社区和用户投稿功能
- 🆕 **非遗地图可视化** - 高德地图集成，地理分布展示
- 🆕 **图片库管理** - 批量上传、相册展示、分类管理
- 🆕 **性能优化** - 修复白屏问题，提升用户体验
- 🆕 **组件增强** - 面包屑、分页、版权声明等UI组件

## 🔧 开发指南

### 环境要求

```bash
# 必需软件版本
- Java 8+ (推荐 JDK 8u292+)
- MySQL 8.0+ (推荐 8.0.26+)
- Node.js 16+ (推荐 16.14.0+)
- Maven 3.6+ (推荐 3.8.1+)
- Git (推荐 2.30+)

# 可选开发工具
- IntelliJ IDEA (Java开发)
- Visual Studio Code (前端开发)
- Navicat/MySQL Workbench (数据库管理)
- Postman (API测试)
```

### 开发环境搭建

```bash
# 1. 克隆项目
git clone https://github.com/XR4everluck/heritage-popularization-platform.git
cd heritage-popularization-platform

# 2. 数据库配置
# 创建数据库
mysql -u root -p -e "CREATE DATABASE heritage_teaching CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入表结构
mysql -u root -p heritage_teaching < sql/heritage_teaching_platform.sql

# 导入演示数据（可选）
mysql -u root -p heritage_teaching < sql/seed_expansion.sql

# 3. 后端开发
cd backend
# 修改 application.yml 中的数据库配置
mvn spring-boot:run

# 4. 前端开发
cd ../frontend
npm install
npm run dev
```

### API文档

系统提供完整的 RESTful API，主要模块包括：

```bash
# 前台接口
GET    /api/heritage/list          # 非遗项目列表
GET    /api/heritage/{id}         # 非遗项目详情
GET    /api/course/list           # 课程列表
GET    /api/course/{id}           # 课程详情
POST   /api/user/login            # 用户登录
GET    /api/heritage/map          # 非遗地图数据

# 后台接口
GET    /api/admin/heritage/list   # 管理端非遗列表
POST   /api/admin/heritage        # 添加非遗项目
PUT    /api/admin/heritage/{id}   # 更新非遗项目
DELETE /api/admin/heritage/{id}   # 删除非遗项目
```

详细接口文档请参考 `docs/08-接口文档.md`

## 📸 系统截图

> 🖼️ 这里展示系统的主要界面截图
> 
> - 🏠 **首页** - 国风设计，精品课程推荐
> - 🗺️ **非遗地图** - 地理可视化展示
> - 👥 **传承人社区** - 互动交流平台
> - 📚 **课程学习** - 视频播放与进度跟踪
> - 🎭 **后台管理** - 功能完善的管理界面

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 开发流程

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/your-feature`
3. 提交更改：`git commit -m 'feat: add your feature'`
4. 推送分支：`git push origin feature/your-feature`
5. 创建 Pull Request

### 代码规范

- Java 代码遵循 Google Java Style
- Vue 代码使用 ESLint + Prettier
- 提交信息使用 Conventional Commits
- 函数和变量使用有意义的名称

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🙏 致谢

- 感谢 Spring Boot 和 Vue.js 框架的作者
- 感谢高德地图提供的地图服务
- 感谢所有为非遗文化传承做出贡献的人们

## 📞 联系方式

- 项目地址：https://github.com/XR4everluck/heritage-popularization-platform
- 作者：XR4everluck
- 邮箱：[your-email@example.com] (请替换为实际联系方式)

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！**

![Star History](https://img.shields.io/github/stars/XR4everluck/heritage-popularization-platform?style=social)

</div>
