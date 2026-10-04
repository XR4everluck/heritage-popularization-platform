@echo off
setlocal enabledelayedexpansion
title 非遗知识教学平台 - 一键启动
cd /d "%~dp0"

:: ==================== 可配置区域（一般无需修改） ====================
:: 以下变量均可用"系统环境变量"覆盖（如需连接非 3306 端口的数据库，设置 DB_PORT 即可）
if not defined BACKEND_PORT  set "BACKEND_PORT=8080"
if not defined FRONTEND_PORT set "FRONTEND_PORT=5173"
if not defined DB_HOST       set "DB_HOST=localhost"
if not defined DB_PORT       set "DB_PORT=3306"
if not defined DB_NAME       set "DB_NAME=heritage_teaching"
if not defined DB_USER       set "DB_USER=root"
set "SQL_FILE=sql\heritage_teaching_platform.sql"
:: ===================================================================

if /i "%~1"=="stop" goto :stop

echo(
echo   ==========================================
echo      非遗知识教学平台 · 一键启动
echo   ==========================================
echo(

:: ---------- 0. 基础环境检查 ----------
where java >nul 2>&1
if errorlevel 1 (
    echo   [×] 未检测到 java，请先安装 JDK 8+ 并加入 PATH
    goto :fail
)
where node >nul 2>&1
if errorlevel 1 (
    echo   [×] 未检测到 node，请先安装 Node.js 18+ 并加入 PATH
    goto :fail
)
where curl >nul 2>&1
if errorlevel 1 (
    echo   [×] 未检测到 curl（Windows 10 以上系统自带）
    goto :fail
)

:: ---------- 1. 数据库连接与自动导入 ----------
echo   [1/5] 检查数据库...
where mysql >nul 2>&1
if errorlevel 1 (
    echo   [!] 未找到 mysql 命令，跳过自动检查（请确保数据库已创建并导入 sql 脚本）
    goto :jwt
)
set "PWD_FROM_FILE=0"
set "DB_PASSWORD="
if exist ".db_secret" for /f "usebackq delims=" %%a in (".db_secret") do (
    set "DB_PASSWORD=%%a"
    set "PWD_FROM_FILE=1"
)
if not defined DB_PASSWORD goto :ask_pwd
goto :test_server
:ask_pwd
set /p "DB_PASSWORD=  请输入 MySQL 密码（%DB_USER%@%DB_HOST%:%DB_PORT%）："
if not defined DB_PASSWORD (
    echo   [×] 未输入密码，无法连接数据库
    goto :fail
)
set "PWD_FROM_FILE=0"
:test_server
:: 第一步：只测服务器连通性（与具体数据库无关），避免把"库不存在"误判成"密码错误"
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --connect-timeout=5 -e "SELECT 1;" >nul 2>&1
if errorlevel 1 goto :connect_fail
:: 第二步：检查业务库是否存在，不存在则自动导入
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "USE %DB_NAME%;" >nul 2>&1
if errorlevel 1 goto :import_db
echo   数据库连接正常。
if "%PWD_FROM_FILE%"=="1" goto :jwt
choice /c YN /n /m "  是否记住数据库密码（保存到本目录 .db_secret，已被 git 忽略）？[Y/N]："
if errorlevel 2 goto :jwt
<nul set /p="%DB_PASSWORD%">.db_secret
echo   已保存，下次启动不再询问。
goto :jwt
:connect_fail
if "%PWD_FROM_FILE%"=="1" (
    echo   [!] 本地保存的密码已失效，请重新输入
    del .db_secret >nul 2>&1
    goto :ask_pwd
)
echo   [×] 无法连接数据库服务器 %DB_HOST%:%DB_PORT%
echo       请检查：1.MySQL 服务是否启动  2.账号密码是否正确
goto :fail
:import_db
echo   [!] 数据库 %DB_NAME% 不存在，自动导入建表脚本与演示数据...
if not exist "%SQL_FILE%" (
    echo   [×] 缺少 %SQL_FILE%，无法自动导入
    goto :fail
)
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < "%SQL_FILE%"
if errorlevel 1 (
    echo   [×] 导入失败，请手动执行 sql\heritage_teaching_platform.sql 后重试
    goto :fail
)
echo   导入完成。
if "%PWD_FROM_FILE%"=="1" goto :jwt
choice /c YN /n /m "  是否记住数据库密码（保存到本目录 .db_secret，已被 git 忽略）？[Y/N]："
if errorlevel 2 goto :jwt
<nul set /p="%DB_PASSWORD%">.db_secret
echo   已保存，下次启动不再询问。

:: ---------- 1.5 JWT 密钥（首次自动生成并保存，保证重启后登录态不失效） ----------
:jwt
set "JWT_SECRET="
if exist ".jwt_secret" for /f "usebackq delims=" %%a in (".jwt_secret") do set "JWT_SECRET=%%a"
:: 密钥至少 32 字符（HS256 要求 256 位以上），过短则自动重新生成
if defined JWT_SECRET if not "!JWT_SECRET:~31,1!"=="" goto :jwt_ok
set "JWT_SECRET="
for /f %%i in ('powershell -NoProfile -Command "[guid]::NewGuid().ToString('N')+[guid]::NewGuid().ToString('N')"') do set "JWT_SECRET=%%i"
<nul set /p="%JWT_SECRET%">.jwt_secret
echo   已生成 JWT 密钥并保存到 .jwt_secret。
:jwt_ok

:: ---------- 2. 后端（无 jar 包时自动打包） ----------
echo   [2/5] 准备后端服务...
if exist "backend\target\heritage-backend.jar" goto :be_start
echo   未找到 jar 包，开始自动打包（首次约 1-3 分钟）...
set "MVN="
where mvn >nul 2>&1 && set "MVN=mvn"
if not defined MVN (
    for /d %%d in ("D:\develop\apache-maven*") do (
        if exist "%%d\bin\mvn.cmd" set "MVN=%%d\bin\mvn.cmd"
    )
)
if not defined MVN (
    echo   [×] 未找到 Maven：请先安装 Maven，或在 IDEA 中打包一次生成 backend\target\heritage-backend.jar
    goto :fail
)
call "!MVN!" -ntp -q -f backend\pom.xml clean package -DskipTests
if errorlevel 1 (
    echo   [×] 后端打包失败，请查看上方错误信息
    goto :fail
)
echo   打包完成。
:be_start
set "SPRING_DATASOURCE_URL=jdbc:mysql://%DB_HOST%:%DB_PORT%/%DB_NAME%?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
set "SPRING_DATASOURCE_USERNAME=%DB_USER%"
set "SPRING_DATASOURCE_PASSWORD=%DB_PASSWORD%"
set "HERITAGE_JWT_SECRET=%JWT_SECRET%"
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":%BACKEND_PORT% " ^| findstr "LISTENING"') do (
    echo   端口 %BACKEND_PORT% 被占用（PID %%p），自动结束旧实例...
    taskkill /f /pid %%p >nul 2>&1
)
echo   启动后端（独立窗口保留运行日志）...
start "非遗平台-后端" cmd /k "chcp 65001 >nul && java -Dfile.encoding=UTF-8 -jar backend\target\heritage-backend.jar"

:: ---------- 3. 等待后端就绪 ----------
echo   [3/5] 等待后端就绪（最长约 90 秒）...
set /a BE_TRIES=0
:wait_be
ping -n 3 127.0.0.1 >nul
curl -s -m 2 -o nul "http://localhost:%BACKEND_PORT%/api/hello"
if errorlevel 1 (
    set /a BE_TRIES+=1
    if !BE_TRIES! geq 30 (
        echo   [×] 后端启动超时，请查看"非遗平台-后端"窗口中的日志
        goto :fail
    )
    goto :wait_be
)
echo   后端已就绪：http://localhost:%BACKEND_PORT%

:: ---------- 4. 前端 ----------
echo   [4/5] 准备前端服务...
if not exist "frontend\node_modules" (
    echo   首次运行，安装前端依赖（需要几分钟，请耐心等待）...
    pushd frontend
    call npm install --no-audit --no-fund
    if errorlevel 1 (
        popd
        echo   [×] 前端依赖安装失败，请检查网络后重试
        goto :fail
    )
    popd
)
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":%FRONTEND_PORT% " ^| findstr "LISTENING"') do (
    echo   端口 %FRONTEND_PORT% 被占用（PID %%p），自动结束旧实例...
    taskkill /f /pid %%p >nul 2>&1
)
echo   启动前端（独立窗口保留运行日志）...
start "非遗平台-前端" cmd /k "chcp 65001 >nul && cd frontend && npm run dev"

:: ---------- 5. 等待前端就绪并打开浏览器 ----------
echo   [5/5] 等待前端就绪...
set /a FE_TRIES=0
:wait_fe
ping -n 2 127.0.0.1 >nul
curl -s -m 2 -o nul "http://localhost:%FRONTEND_PORT%/"
if errorlevel 1 (
    set /a FE_TRIES+=1
    if !FE_TRIES! geq 30 (
        echo   [×] 前端启动超时，请查看"非遗平台-前端"窗口中的日志
        goto :fail
    )
    goto :wait_fe
)

echo(
echo   ==========================================
echo      启动完成！
echo      前台/后台入口: http://localhost:%FRONTEND_PORT%
echo      管理员登录:    http://localhost:%FRONTEND_PORT%/admin/login
echo      账号: admin / 123456    user1 / 123456
echo      停止系统: 在本目录运行  一键启动.bat stop
echo   ==========================================
echo(
if defined NO_BROWSER (
    echo   （NO_BROWSER 模式：跳过自动打开浏览器）
) else (
    start "" "http://localhost:%FRONTEND_PORT%/"
)
pause
exit /b 0

:: ---------- 失败出口 ----------
:fail
echo(
echo   启动失败，请按上方提示排查；更多方案见 docs\07-部署运行文档.md
echo(
pause
exit /b 1

:: ---------- 停止模式：一键启动.bat stop ----------
:stop
echo 正在停止非遗知识教学平台...
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":%BACKEND_PORT% " ^| findstr "LISTENING"') do (
    echo   结束后端进程 PID %%p
    taskkill /f /pid %%p >nul 2>&1
)
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":%FRONTEND_PORT% " ^| findstr "LISTENING"') do (
    echo   结束前端进程 PID %%p
    taskkill /f /pid %%p >nul 2>&1
)
taskkill /fi "WINDOWTITLE eq 非遗平台-后端*" >nul 2>&1
taskkill /fi "WINDOWTITLE eq 非遗平台-前端*" >nul 2>&1
echo 完成。相关窗口可手动关闭。
pause
exit /b 0
