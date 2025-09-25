# TESTE FINAL DOS FILTROS CORRIGIDOS
Write-Host "TESTE FINAL - FILTROS CORRIGIDOS" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green

# Teste 1: CRITICO - Filtro por ID (deve continuar funcionando)
Write-Host "1. Testando filtro por ID (PRESERVADO):"
try {
    $idTest = Invoke-WebRequest -Uri "http://localhost:8080/api/pedidos-agrupados?idPedido=PED-1-20250924-004" -Method GET -TimeoutSec 10
    $resultId = $idTest.Content | ConvertFrom-Json
    if ($resultId.Count -eq 1) {
        Write-Host "   SUCESSO - Filtro ID funcionando: $($resultId[0].idPedido)" -ForegroundColor Green
    } else {
        Write-Host "   ERRO - Filtro ID quebrado: $($resultId.Count) resultados" -ForegroundColor Red
    }
} catch {
    Write-Host "   ERRO - Filtro ID: $($_.Exception.Message)" -ForegroundColor Red
}

# Teste 2: Filtro por Status Aprovado
Write-Host "2. Testando filtro por Status=Aprovado:"
try {
    $statusTest = Invoke-WebRequest -Uri "http://localhost:8080/api/pedidos-agrupados?status=Aprovado" -Method GET -TimeoutSec 10
    $resultStatus = $statusTest.Content | ConvertFrom-Json
    Write-Host "   Encontrados: $($resultStatus.Count) pedidos aprovados" -ForegroundColor $(if($resultStatus.Count -gt 0) { "Green" } else { "Yellow" })
    
    if ($resultStatus.Count -gt 0) {
        $pedidoTeste = $resultStatus[0].idPedido
        Write-Host "   Testando PDF: $pedidoTeste"
        try {
            $pdfTest = Invoke-WebRequest -Uri "http://localhost:8080/api/pedidos-agrupados/pdf/$pedidoTeste" -Method GET -TimeoutSec 10
            Write-Host "   SUCESSO - PDF gerado!" -ForegroundColor Green
        } catch {
            Write-Host "   ERRO - PDF: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
} catch {
    Write-Host "   ERRO - Status: $($_.Exception.Message)" -ForegroundColor Red
}

# Teste 3: Filtro por Data (que estava dando erro)
Write-Host "3. Testando filtro por Data 24/09:"
try {
    $dataTest = Invoke-WebRequest -Uri "http://localhost:8080/api/pedidos-agrupados?dataInicial=2025-09-24&dataFinal=2025-09-24" -Method GET -TimeoutSec 10
    $resultData = $dataTest.Content | ConvertFrom-Json
    Write-Host "   Encontrados: $($resultData.Count) pedidos em 24/09" -ForegroundColor $(if($resultData.Count -gt 0) { "Green" } else { "Yellow" })
} catch {
    Write-Host "   ERRO - Data: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "=================================" -ForegroundColor Green
Write-Host "TESTE CONCLUIDO" -ForegroundColor Green