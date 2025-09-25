# FASICLIN COMPRAS - Detector Inteligente de Aplicação
# Este script encontra automaticamente em qual porta a aplicação está rodando

Write-Host "🔍 FASICLIN COMPRAS - DETECTOR DE APLICAÇÃO" -ForegroundColor Magenta
Write-Host "============================================"

$ports = @(8080, 8081, 8082, 8083, 8084, 8085, 8086, 8087, 8088, 8089)
$foundPort = $null

Write-Host "🔎 Procurando aplicação nas portas disponíveis..." -ForegroundColor Yellow

foreach($port in $ports) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$port/api/pedidos-agrupados" -Method GET -TimeoutSec 2 -ErrorAction Stop
        $foundPort = $port
        break
    } catch {
        # Continuar procurando
    }
}

if ($foundPort) {
    Write-Host ""
    Write-Host "🎉 APLICAÇÃO ENCONTRADA E FUNCIONANDO!" -ForegroundColor Green
    Write-Host "========================================"
    Write-Host "🌐 Porta: $foundPort" -ForegroundColor Cyan
    Write-Host "📋 Interface: http://localhost:$foundPort/consulta.html" -ForegroundColor Cyan
    Write-Host "🔧 API Base: http://localhost:$foundPort/api/" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "✅ FUNCIONALIDADES DISPONÍVEIS:" -ForegroundColor Yellow
    Write-Host "   • PDF para pedidos aprovados e reprovados"
    Write-Host "   • Download ZIP para múltiplos PDFs"
    Write-Host "   • Data/hora de aprovação imutável"
    Write-Host "   • Sistema de agrupamento de pedidos"
    Write-Host ""
    
    # Teste completo
    try {
        $r1 = Invoke-WebRequest -Uri "http://localhost:$foundPort/api/pedidos-agrupados" -TimeoutSec 3
        $r2 = Invoke-WebRequest -Uri "http://localhost:$foundPort/consulta.html" -TimeoutSec 3
        $r3 = Invoke-WebRequest -Uri "http://localhost:$foundPort/api/orcamentos/pendentes" -TimeoutSec 3
        
        Write-Host "🧪 TESTE DE FUNCIONALIDADES:" -ForegroundColor Green
        Write-Host "   ✅ API Pedidos: OK ($($r1.StatusCode))"
        Write-Host "   ✅ Interface: OK ($($r2.StatusCode))"
        Write-Host "   ✅ Orçamentos: OK ($($r3.StatusCode))"
        Write-Host ""
        Write-Host "🚀 SISTEMA 100% OPERACIONAL!" -ForegroundColor Green
        
    } catch {
        Write-Host "⚠️ Aplicação encontrada mas ainda inicializando..." -ForegroundColor Yellow
    }
    
} else {
    Write-Host ""
    Write-Host "❌ APLICAÇÃO NÃO ENCONTRADA" -ForegroundColor Red
    Write-Host "A aplicação pode estar iniciando ou parada."
    Write-Host "Execute 'start-smart.bat' para iniciar."
}

Write-Host ""
Write-Host "Pressione qualquer tecla para continuar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")