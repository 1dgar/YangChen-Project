@echo off
chcp 65001 >nul
echo ================================
echo    农贸通 - 前端服务启动
echo ================================
echo.

cd /d "%~dp0agri-web"

echo [1/3] 检查Node.js环境...
node -v >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Node.js环境，请先安装Node.js 16或更高版本
    pause
    exit /b 1
)

echo [2/3] 检查依赖...
if not exist "node_modules" (
    echo 首次运行，正在安装依赖...
    call npm install
)

echo [3/3] 启动前端服务...
echo.
echo 前端服务地址: http://localhost:5173
echo.

call npm run dev

pause
