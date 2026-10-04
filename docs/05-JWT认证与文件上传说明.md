# 阶段5：JWT 认证 + 文件上传 说明

> 阶段5目标：为后端增加 JWT 认证与本地文件上传能力，完善毕设答辩所需的完整度。
> 认证体系覆盖全部业务接口：公开接口白名单放行，其余需携带 `Authorization: Bearer {token}`；`/api/admin/**` 额外要求 admin 角色。

---

## 1. 新增/修改文件清单

### 1.1 新增文件

| 文件路径 | 说明 |
| --- | --- |
| `backend/src/main/java/com/heritage/util/JwtUtil.java` | JWT 工具类：生成/解析/校验 token（jjwt 0.11.5），subject 存用户ID，附 username/role 声明 |
| `backend/src/main/java/com/heritage/util/AuthContext.java` | 认证上下文：控制器从请求属性获取当前登录用户ID/角色 |
| `backend/src/main/java/com/heritage/interceptor/JwtInterceptor.java` | JWT 登录拦截器：token 校验 → 账号状态实时校验 → 管理员角色校验 |
| `backend/src/main/java/com/heritage/config/WebMvcConfig.java` | 拦截器注册（含公开接口白名单）+ 上传文件静态资源映射 |
| `backend/src/main/java/com/heritage/vo/LoginVO.java` | 登录结果：token + 用户信息 |
| `backend/src/main/java/com/heritage/controller/FileController.java` | 文件上传接口（图片/视频） |
| `backend/src/test/java/com/heritage/JwtUtilTest.java` | JWT 单元测试：生成/解析/篡改失效/过期失效/异密钥失效 |

### 1.2 修改文件

| 文件路径 | 修改内容 |
| --- | --- |
| `backend/pom.xml` | 新增 jjwt-api / jjwt-impl / jjwt-jackson 0.11.5（`jjwt.version` 属性） |
| `backend/src/main/resources/application.yml` | 新增 `heritage.jwt`（密钥/有效期）、`heritage.upload.path`；multipart 上限放宽至 500MB |
| `controller/front/FrontUserController.java` | 登录返回 `LoginVO{token,user}`；资料查询/修改改为从 token 解析用户ID |
| `controller/front/FrontCollectionController.java` | 用户ID改由 token 解析（去掉 userId 参数） |
| `controller/front/FrontCommentController.java` | 发布/删除评论改由 token 解析用户ID（分页仍为公开接口） |
| `controller/front/FrontProgressController.java` | 用户ID改由 token 解析 |
| `backend/src/main/java/com/heritage/exception/GlobalExceptionHandler.java` | 上传超限提示同步为"图片10MB/视频500MB" |

## 2. 认证体系设计

### 2.1 令牌机制

- 登录成功 → `JwtUtil.generateToken(userId, username, role)` 签发 HS256 token，有效期 24 小时（`heritage.jwt.expire-hours` 可调）；
- 后续请求携带请求头 `Authorization: Bearer {token}`；
- token 无状态自包含（用户ID/用户名/角色），无需服务端会话。

### 2.2 拦截器校验流程（JwtInterceptor）

1. 缺少/格式错误的 `Authorization` 头 → 返回 `{code:401}`"未登录或缺少令牌"；
2. 签名不合法或已过期 → `{code:401}`"登录已过期或令牌无效"；
3. **实时查库校验**：用户不存在（已注销）或 status=0（被禁用）→ 立即 401——即使 token 未过期，禁用账号当场失效；
4. 请求 `/api/admin/**` 且角色非 admin → `{code:403}`"无权限访问管理员接口"；
5. 校验通过 → 将 userId/role 写入请求属性，控制器经 `AuthContext.getUserId(request)` 获取可信身份。

### 2.3 公开接口白名单（无需登录）

注册/登录、`/api/category/**`、`/api/heritage/**`（含浏览量自增）、`/api/course/**`、`/api/portal/**`、`GET /api/comment/page`、`/api/hello/**`、静态文件 `/files/**`。

需要登录：个人资料、收藏、评论发布/删除、学习进度、文件上传；其中 `/api/admin/**` 仅限管理员。

### 2.4 密钥管理

`application.yml` 中 `heritage.jwt.secret: ${HERITAGE_JWT_SECRET:}` —— 从环境变量读取，**源码与配置文件中不落任何真实密钥**；未配置时后端启动自动生成随机密钥并告警（重启后登录态失效，仅限开发调试）。生产/演示环境设置环境变量即可（如 64 位随机字符串）。

## 3. 文件上传设计

- **接口**：`POST /api/file/upload`（需登录），表单参数 `file`（必填）+ `type`（image/video，默认 image）；
- **类型白名单**：图片 jpg/jpeg/png/gif/webp/bmp，视频 mp4/webm/mov/avi/mkv；扩展名仅允许 1-10 位字母数字（防止特殊字符进入存储路径）；
- **大小限制**：图片 10MB、视频 500MB（框架层 multipart 上限同步放宽到 500MB）；
- **存储策略**：`{heritage.upload.path}/{type}/{yyyyMMdd}/{UUID}.{ext}`——文件名 UUID 重生成，不使用用户原始文件名；
- **访问方式**：`WebMvcConfig` 将 `/files/**` 映射到上传目录，返回 `http://host:8080/files/image/20261004/xxx.png` 形式的 URL；
- **安全**：上传接口需登录（不在白名单），防匿名写盘；超限/非法类型均返回统一 Result 结构。

## 4. 阶段5验证记录（本机实测，一次性 MySQL 8.0.34 沙箱实例 + 固定测试密钥）

1. `mvn compile` → BUILD SUCCESS；`mvn test` → 6 个测试全部通过（新增 JWT 三项：合法令牌解析、篡改/异密钥令牌失效、过期令牌失效）；
2. 认证冒烟（curl）：
   - admin/user1 登录均返回 token（载荷含正确的 userId 与 role）；
   - 无 token 访问 `/api/admin/category/page` → 401；user1 token → 403；admin token → 200；
   - `/api/user/profile` 携带 user1 token → 返回 id=2（身份来自 token，非参数）；
   - 收藏/取消、进度 upsert（5+10=15 分钟累计）、评论发布均带 token 成功；无 token 的删除请求被 401 拦截；
   - 公开接口（评论分页等）无 token 正常访问；
3. 上传冒烟：无 token → 401；user1 上传 1×1 PNG → 返回 URL `.../files/image/20261004/{uuid}.png`；GET 该 URL 下载内容与原文件**逐字节一致**；上传 .txt → 400"不支持的文件类型 .txt"；
4. 沙箱实例与测试文件已全部清理，未触碰本机真实数据库。

## 5. 前端对接提示（阶段6/7 使用）

- 登录后保存 `data.token`，axios 请求拦截器统一加 `Authorization: Bearer {token}`；
- 响应拦截器：`code === 401` 跳转登录页，`code === 403` 提示无权限；
- 头像/封面/章节视频统一走 `POST /api/file/upload`（type=image/video），返回的 URL 直接填入对应字段或 `<img>`/`<video>` 地址（相对路径可直接回源站访问）；
- 管理端路由守卫：登录用户 `user.role === 'admin'` 才可进入后台页面（后端已双重校验）。
