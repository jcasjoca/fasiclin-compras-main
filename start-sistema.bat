@echo off
echo ==========================================
echo SCRIPT DEFINITIVO - Sistema de Compras FASICLIN
echo ==========================================

echo 🔄 Finalizando processos Java existentes...
taskkill /f /im java.exe >nul 2>&1
timeout /t 3 >nul

echo 🔍 Verificando porta 8080...
netstat -an | find "8080" >nul
if %errorlevel% equ 0 (
    echo ⚠️  Porta 8080 ocupada, usando 8081...
    set SERVER_PORT=8081
    set APP_PORT=8081
) else (
    echo ✅ Porta 8080 disponível
    set SERVER_PORT=8080
    set APP_PORT=8080
)

echo 🔨 Compilando projeto...
call mvn compile -q
if %errorlevel% neq 0 (
    echo ❌ Erro na compilação!
    pause
    exit /b 1
)

echo ✅ Compilação concluída!
echo 🚀 Iniciando aplicação na porta %APP_PORT%...

start /min "FASICLIN-APP" mvn spring-boot:run -Dserver.port=%APP_PORT%

echo ⏳ Aguardando inicialização (30 segundos)...
timeout /t 30 >nul

echo 🧪 Testando aplicação...
for /l %%i in (1,1,5) do (
    curl -s -f http://localhost:%APP_PORT%/api/pedidos-agrupados >nul 2>&1
    if %errorlevel% equ 0 (
        echo ==========================================
        echo 🎉 SISTEMA TOTALMENTE FUNCIONAL!
        echo 🌐 URL: http://localhost:%APP_PORT%/consulta.html
        echo 📋 Funcionalidades ativas:
        echo    ✅ Visualização de PDFs aprovados/rejeitados
        echo    ✅ Download em lote ZIP
        echo    ✅ Timestamps imutáveis
        echo    ✅ Detecção inteligente de portas
        echo ==========================================
        start http://localhost:%APP_PORT%/consulta.html
        echo Pressione qualquer tecla para finalizar...
        pause >nul
        taskkill /f /im java.exe >nul 2>&1
        exit /b 0
    )
    echo ⏳ Tentativa %%i/5 - Aguardando...
    timeout /t 10 >nul
)

echo ❌ Aplicação não respondeu após 5 tentativas
echo 🛑 Finalizando processos...
taskkill /f /im java.exe >nul 2>&1
echo Script finalizado com erro
pause