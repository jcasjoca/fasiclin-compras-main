# FASICLIN COMPRAS - Monitor de Aplicacao
# Este script monitora e mantem a aplicacao rodando automaticamente

param(
    [string]$Action = "start"
)

$AppPath = "C:\Users\João Carlos Almeida\Documents\fasiclin-compras-main"
$LogFile = "$AppPath\app-monitor.log"

function Write-Log {
    param([string]$Message)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $logEntry = "[$timestamp] $Message"
    Write-Host $logEntry -ForegroundColor Cyan
    Add-Content -Path $LogFile -Value $logEntry
}

function Test-AppRunning {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/pedidos-agrupados" -Method GET -TimeoutSec 3 -ErrorAction Stop
        return $response.StatusCode -eq 200
    } catch {
        return $false
    }
}

function Start-App {
    Write-Log "Iniciando aplicacao FASICLIN COMPRAS..."
    
    # Limpar processos Java anteriores
    Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force
    Start-Sleep -Seconds 3
    
    # Iniciar aplicacao
    Set-Location $AppPath
    $process = Start-Process "mvn" -ArgumentList "spring-boot:run" -PassThru -WindowStyle Hidden
    
    Write-Log "Aplicacao iniciada com PID: $($process.Id)"
    return $process
}

function Monitor-App {
    Write-Log "=== INICIANDO MONITOR FASICLIN COMPRAS ==="
    Write-Log "Aplicacao: $AppPath"
    Write-Log "Acesso: http://localhost:8080/consulta.html"
    Write-Log "==========================================="
    
    $appProcess = Start-App
    
    while ($true) {
        Start-Sleep -Seconds 30
        
        if (-not (Test-AppRunning)) {
            Write-Log "ALERTA: Aplicacao nao esta respondendo. Reiniciando..."
            
            # Parar processo atual se existir
            if ($appProcess -and -not $appProcess.HasExited) {
                $appProcess.Kill()
            }
            
            # Reiniciar aplicacao
            $appProcess = Start-App
            Start-Sleep -Seconds 15
        } else {
            Write-Log "Status: Aplicacao funcionando normalmente"
        }
    }
}

if ($Action -eq "start") {
    Monitor-App
} elseif ($Action -eq "stop") {
    Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force
    Write-Log "Aplicacao parada"
}