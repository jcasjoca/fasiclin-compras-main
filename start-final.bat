@echo off
setlocal

echo ========================================
echo    FASICLIN COMPRAS - INICIADOR FINAL
echo ========================================

REM Definir variáveis
set "APP_DIR=C:\Users\João Carlos Almeida\Documents\fasiclin-compras-main"
set "LOG_FILE=%APP_DIR%\app-startup.log"

REM Ir para diretório da aplicação
cd /d "%APP_DIR%"

REM Limpar log anterior
if exist "%LOG_FILE%" del "%LOG_FILE%"

echo [%date% %time%] Iniciando FASICLIN COMPRAS... >> "%LOG_FILE%"

REM Parar processos Java anteriores apenas da aplicação
echo Parando processos anteriores da aplicacao...
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /NH 2^>nul ^| findstr /C:"ComprasApplication"') do (
    taskkill /F /PID %%i >nul 2>&1
)

REM Aguardar limpeza
timeout /t 3 /nobreak >nul

echo Iniciando aplicacao em segundo plano...
echo [%date% %time%] Executando mvn spring-boot:run... >> "%LOG_FILE%"

REM Iniciar aplicação em background usando PowerShell
powershell -WindowStyle Hidden -Command "Start-Process cmd -ArgumentList '/c cd /d \"%APP_DIR%\" && mvn spring-boot:run >> \"%LOG_FILE%\" 2>&1' -WindowStyle Hidden"

echo.
echo ========================================
echo    APLICACAO INICIADA EM SEGUNDO PLANO
echo ========================================
echo.
echo A aplicacao esta sendo iniciada...
echo Verifique em alguns segundos em:
echo.
echo http://localhost:8080/consulta.html
echo http://localhost:8081/consulta.html (se 8080 ocupada)
echo http://localhost:8082/consulta.html (se 8081 ocupada)
echo.
echo Log de inicializacao: %LOG_FILE%
echo.
echo Pressione qualquer tecla para continuar...
pause >nul