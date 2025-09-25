# Script para iniciar aplicação de forma estável
Write-Host "🚀 Iniciando aplicação de forma estável..." -ForegroundColor Green

# Matar qualquer processo Java existente
Write-Host "⚡ Limpando processos Java..." -ForegroundColor Yellow
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force

# Aguardar limpeza
Start-Sleep -Seconds 3

# Verificar se porta está livre
$portTest = Test-NetConnection -ComputerName localhost -Port 8080 -WarningAction SilentlyContinue
if ($portTest.TcpTestSucceeded) {
    Write-Host "❌ Porta 8080 ainda ocupada" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Porta 8080 livre" -ForegroundColor Green

# Iniciar aplicação
Write-Host "🔄 Iniciando Spring Boot..." -ForegroundColor Cyan
& mvn spring-boot:run

Write-Host "📋 Aplicação iniciada!" -ForegroundColor Green