@echo off
chcp 65001 >nul
echo ================================
echo    农贸通 - 一键启动
echo ================================
echo.

echo 正在启动后端服务...
start "农贸通-后端" cmd /c "%~dp0start-backend.bat"

timeout /t 10 /nobreak >nul

echo 正在启动前端服务...
start "农贸通-前端" cmd /c "%~dp0start-frontend.bat"

echo.
echo ================================
echo 服务启动完成！
echo.
echo 后端地址: http://localhost:8888
echo 前端地址: http://localhost:5173
echo.
echo 测试账号:
echo   管理员 - admin / 123456
echo   农户   - farmer1 / 123456
echo   采购商 - buyer1 / 123456
echo ================================
echo.

start http://localhost:5173

pause
