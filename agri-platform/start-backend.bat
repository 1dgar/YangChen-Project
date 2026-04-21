@echo off
chcp 65001 >nul
echo ================================
echo    农贸通 - 后端服务启动
echo ================================
echo.

cd /d "%~dp0agri-server"

echo [1/2] 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Java环境，请先安装JDK 11或更高版本
    pause
    exit /b 1
)

echo [2/2] 启动后端服务...
echo.
echo 后端服务地址: http://localhost:8888
echo H2数据库控制台: http://localhost:8888/h2-console
echo.

call mvn spring-boot:run

pause
