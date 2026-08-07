@echo off
echo Starting tengan-admin-web (http://localhost:8090) ...
start "tengan-admin-web" cmd /k "cd /d %~dp0tengan-admin-web && pnpm dev"

echo Starting tengan-mall-web (http://localhost:3000) ...
start "tengan-mall-web" cmd /k "cd /d %~dp0tengan-mall-web && npm run dev"

echo Both dev servers are starting in separate windows.
