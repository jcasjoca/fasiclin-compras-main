@echo off
title FASICLIN COMPRAS - Sistema de Gestao
echo ========================================
echo    FASICLIN COMPRAS - INICIANDO SISTEMA
echo ========================================
echo.

REM Limpar processos Java anteriores
echo [%date% %time%] Limpando processos anteriores...
taskkill /F /IM java.exe /T >nul 2>&1
timeout /t 3 /nobreak >nul

REM Verificar se a porta 8080 esta livre
echo [%date% %time%] Verificando porta 8080...
netstat -an | find ":8080" >nul
if %errorlevel% == 0 (
    echo ERRO: Porta 8080 ainda ocupada. Aguardando...
    timeout /t 5 /nobreak >nul
)

echo [%date% %time%] Iniciando aplicacao...
echo.
echo ========================================
echo    APLICACAO RODANDO - NAO FECHE
echo    Acesse: http://localhost:8080/consulta.html
echo ========================================
echo.

REM Executar Spring Boot em loop para manter rodando
:LOOP
mvn spring-boot:run
echo.
echo [%date% %time%] Aplicacao parou. Reiniciando em 5 segundos...
timeout /t 5 /nobreak >nul
goto LOOP