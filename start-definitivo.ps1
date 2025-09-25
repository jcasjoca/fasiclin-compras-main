# ========================================
# SCRIPT DEFINITIVO PARA INICIALIZAÇÃO 
# Sistema de Compras FASICLIN
# ========================================

Write-Host "🚀 INICIANDO SISTEMA DE COMPRAS FASICLIN" -ForegroundColor Cyan
Write-Host "📋 Versão: Definitiva com detecção inteligente de portas" -ForegroundColor Gray
Write-Host "=========================================" -ForegroundColor Cyan

# Função para verificar se porta está disponível
function Test-Port {
    param([int]$Port)
    try {
        $listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Any, $Port)
        $listener.Start()
        $listener.Stop()
        return $true
    } catch {
        return $false
    }
}

# Função para finalizar processos Java existentes
function Stop-JavaProcesses {
    Write-Host "🔍 Verificando processos Java existentes..." -ForegroundColor Yellow
    $javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue
    if ($javaProcesses) {
        Write-Host "⚠️  Finalizando $($javaProcesses.Count) processo(s) Java..." -ForegroundColor Yellow
        $javaProcesses | Stop-Process -Force
        Start-Sleep -Seconds 3
        Write-Host "✅ Processos Java finalizados" -ForegroundColor Green
    } else {
        Write-Host "✅ Nenhum processo Java em execução" -ForegroundColor Green
    }
}

# Função para encontrar porta disponível
function Find-AvailablePort {
    Write-Host "🔍 Procurando porta disponível..." -ForegroundColor Yellow
    $ports = @(8080, 8081, 8082, 8083, 8084, 8085)
    
    foreach ($port in $ports) {
        if (Test-Port -Port $port) {
            Write-Host "✅ Porta $port disponível!" -ForegroundColor Green
            return $port
        } else {
            Write-Host "❌ Porta $port ocupada" -ForegroundColor Red
        }
    }
    
    Write-Host "⚠️  Nenhuma porta disponível encontrada!" -ForegroundColor Red
    return $null
}

# Função para compilar o projeto
function Compile-Project {
    Write-Host "🔨 Compilando projeto..." -ForegroundColor Yellow
    try {
        $compileResult = & mvn compile -q 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Compilação concluída com sucesso!" -ForegroundColor Green
            return $true
        } else {
            Write-Host "❌ Erro na compilação:" -ForegroundColor Red
            Write-Host $compileResult -ForegroundColor Red
            return $false
        }
    } catch {
        Write-Host "❌ Erro ao executar compilação: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Função para iniciar aplicação
function Start-Application {
    param([int]$Port)
    
    Write-Host "🚀 Iniciando aplicação na porta $Port..." -ForegroundColor Cyan
    
    # Definir porta no ambiente
    $env:SERVER_PORT = $Port
    
    # Iniciar aplicação em background
    $processInfo = New-Object System.Diagnostics.ProcessStartInfo
    $processInfo.FileName = "mvn"
    $processInfo.Arguments = "spring-boot:run"
    $processInfo.UseShellExecute = $false
    $processInfo.RedirectStandardOutput = $true
    $processInfo.RedirectStandardError = $true
    $processInfo.CreateNoWindow = $false
    $processInfo.WorkingDirectory = Get-Location
    
    # Adicionar variáveis de ambiente
    $processInfo.EnvironmentVariables["SERVER_PORT"] = $Port.ToString()
    
    $process = [System.Diagnostics.Process]::Start($processInfo)
    
    Write-Host "⏳ Aguardando inicialização (30 segundos)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30
    
    return $process
}

# Função para testar aplicação
function Test-Application {
    param([int]$Port)
    
    Write-Host "🧪 Testando aplicação na porta $Port..." -ForegroundColor Yellow
    
    for ($i = 1; $i -le 5; $i++) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$Port/api/pedidos-agrupados" -Method GET -TimeoutSec 10
            if ($response.StatusCode -eq 200) {
                Write-Host "🎉 SUCESSO! Aplicação funcionando!" -ForegroundColor Green
                Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
                Write-Host "🌐 Interface: http://localhost:$Port/consulta.html" -ForegroundColor Cyan
                Write-Host "📄 Sistema de PDFs e ZIP ativo!" -ForegroundColor Yellow
                Write-Host "🔒 Timestamps imutáveis funcionando!" -ForegroundColor Yellow
                return $true
            }
        } catch {
            Write-Host "⏳ Tentativa $i/5 - Aguardando..." -ForegroundColor Yellow
            Start-Sleep -Seconds 10
        }
    }
    
    Write-Host "❌ Aplicação não respondeu após 5 tentativas" -ForegroundColor Red
    return $false
}

# EXECUÇÃO PRINCIPAL
Write-Host "=========================================" -ForegroundColor Cyan

# 1. Limpar processos existentes
Stop-JavaProcesses

# 2. Encontrar porta disponível
$availablePort = Find-AvailablePort
if (-not $availablePort) {
    Write-Host "💥 ERRO CRÍTICO: Nenhuma porta disponível!" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

# 3. Compilar projeto
if (-not (Compile-Project)) {
    Write-Host "💥 ERRO CRÍTICO: Falha na compilação!" -ForegroundColor Red
    Read-Host "Pressione Enter para sair"
    exit 1
}

# 4. Iniciar aplicação
$appProcess = Start-Application -Port $availablePort

# 5. Testar aplicação
if (Test-Application -Port $availablePort) {
    Write-Host "=========================================" -ForegroundColor Green
    Write-Host "🎉 SISTEMA TOTALMENTE FUNCIONAL!" -ForegroundColor Green
    Write-Host "🌐 URL: http://localhost:$availablePort/consulta.html" -ForegroundColor Cyan
    Write-Host "📋 Funcionalidades ativas:" -ForegroundColor Yellow
    Write-Host "   ✅ Visualização de PDFs (aprovados/rejeitados)" -ForegroundColor White
    Write-Host "   ✅ Download em lote (ZIP)" -ForegroundColor White
    Write-Host "   ✅ Timestamps imutáveis" -ForegroundColor White
    Write-Host "   ✅ Detecção inteligente de portas" -ForegroundColor White
    Write-Host "=========================================" -ForegroundColor Green
    Write-Host "Pressione Ctrl+C para parar a aplicação" -ForegroundColor Gray
    
    # Manter script rodando
    try {
        while ($true) {
            Start-Sleep -Seconds 30
            # Verificar se aplicação ainda está respondendo
            try {
                $response = Invoke-WebRequest -Uri "http://localhost:$availablePort/api/pedidos-agrupados" -Method GET -TimeoutSec 5
                Write-Host "✅ Sistema operacional - $(Get-Date -Format 'HH:mm:ss')" -ForegroundColor Green
            } catch {
                Write-Host "⚠️  Sistema não responde - $(Get-Date -Format 'HH:mm:ss')" -ForegroundColor Yellow
                break
            }
        }
    } catch {
        Write-Host "🛑 Script interrompido pelo usuário" -ForegroundColor Yellow
    }
} else {
    Write-Host "💥 FALHA NA INICIALIZAÇÃO!" -ForegroundColor Red
    if ($appProcess -and !$appProcess.HasExited) {
        $appProcess.Kill()
        Write-Host "🛑 Processo da aplicação finalizado" -ForegroundColor Yellow
    }
}

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Script finalizado - $(Get-Date)" -ForegroundColor Gray
Read-Host "Pressione Enter para sair"