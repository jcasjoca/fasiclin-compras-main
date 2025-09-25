@echo off
title FASICLIN COMPRAS - Sistema Inteligente
echo ========================================
echo    FASICLIN COMPRAS - INICIALIZACAO INTELIGENTE
echo ========================================
echo.

REM Limpar processos Java anteriores apenas se necessário
echo [%date% %time%] Verificando processos anteriores...
tasklist /FI "IMAGENAME eq java.exe" /FI "WINDOWTITLE eq *compras*" >nul 2>&1
if %errorlevel% == 0 (
    echo Parando processos anteriores da aplicacao...
    for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /FI "WINDOWTITLE eq *compras*" ^| findstr java') do taskkill /F /PID %%i >nul 2>&1
    timeout /t 3 /nobreak >nul
)

echo [%date% %time%] Iniciando aplicacao com deteccao automatica de porta...
echo.
echo ========================================
echo    APLICACAO COM PORTA AUTOMATICA
echo    A aplicacao encontrara uma porta livre automaticamente
echo    Aguarde as informacoes de conexao...
echo ========================================
echo.

REM Iniciar aplicação com detecção automática de porta
mvn spring-boot:run -Dspring-boot.run.fork=false

echo.
echo [%date% %time%] Aplicacao finalizada.
pause