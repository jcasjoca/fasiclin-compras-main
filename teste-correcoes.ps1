# SCRIPT DE TESTE DAS CORREÇÕES IMPLEMENTADAS
# Preserva o filtro por ID (que funciona) e testa as correções

Write-Host "🧪 TESTANDO CORREÇÕES IMPLEMENTADAS" -ForegroundColor Cyan
Write-Host "══════════════════════════════════════════════════════════" -ForegroundColor Cyan

# Função para testar conectividade
function Test-Aplicacao {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/pedidos-agrupados" -Method GET -TimeoutSec 5
        return $true
    } catch {
        return $false
    }
}

# Aguardar aplicação ficar online
Write-Host "⏳ Aguardando aplicação ficar online..." -ForegroundColor Yellow
$tentativas = 0
while ((!(Test-Aplicacao)) -and ($tentativas -lt 10)) {
    Start-Sleep -Seconds 5
    $tentativas++
    Write-Host "   Tentativa $tentativas/10..." -ForegroundColor Gray
}

if (!(Test-Aplicacao)) {
    Write-Host "❌ ERRO: Aplicação não respondeu após 10 tentativas" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Aplicação online! Iniciando testes..." -ForegroundColor Green
Write-Host ""

# TESTE 1: Verificar se duplicatas PED-3 foram corrigidas
Write-Host "🔍 TESTE 1: Verificando duplicatas PED-3..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/pedidos-agrupados" -Method GET -TimeoutSec 10
    $pedidos = $response.Content | ConvertFrom-Json
    $ped3 = $pedidos | Where-Object { $_.idPedido -like "PED-3-*" }
    
    Write-Host "   Pedidos PED-3 encontrados: $($ped3.Count)" -ForegroundColor White
    $ped3 | ForEach-Object { 
        Write-Host "   → $($_.idPedido) - Status: $($_.status) - Fornecedor: $($_.nomeFornecedor)" -ForegroundColor White 
    }
    
    if ($ped3.Count -eq 1) {
        Write-Host "✅ SUCESSO: Duplicata corrigida! Agora só há 1 linha PED-3" -ForegroundColor Green
    } else {
        Write-Host "⚠️  AINDA COM PROBLEMA: $($ped3.Count) linhas encontradas (esperado: 1)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ ERRO no teste 1: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# TESTE 2: Filtro por Status Pendente
Write-Host "🔍 TESTE 2: Filtro por Status 'Pendente'..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/pedidos-agrupados?status=Pendente" -Method GET -TimeoutSec 10
    $pedidos = $response.Content | ConvertFrom-Json
    
    Write-Host "   Pedidos com status Pendente: $($pedidos.Count)" -ForegroundColor White
    $pedidos | ForEach-Object { 
        Write-Host "   → $($_.idPedido) - Status: $($_.status)" -ForegroundColor White 
    }
    
    if ($pedidos.Count -gt 0) {
        Write-Host "✅ SUCESSO: Filtro por status Pendente funcionando!" -ForegroundColor Green
    } else {
        Write-Host "⚠️  PROBLEMA: Nenhum pedido Pendente encontrado" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ ERRO no teste 2: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# TESTE 3: Filtro por Status Aprovado
Write-Host "🔍 TESTE 3: Filtro por Status 'Aprovado'..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/pedidos-agrupados?status=Aprovado" -Method GET -TimeoutSec 10
    $pedidos = $response.Content | ConvertFrom-Json
    
    Write-Host "   Pedidos com status Aprovado: $($pedidos.Count)" -ForegroundColor White
    $pedidos | ForEach-Object { 
        Write-Host "   → $($_.idPedido) - Status: $($_.status)" -ForegroundColor White 
    }
    
    if ($pedidos.Count -gt 0) {
        Write-Host "✅ SUCESSO: Filtro por status Aprovado funcionando!" -ForegroundColor Green
    } else {
        Write-Host "⚠️  PROBLEMA: Nenhum pedido Aprovado encontrado" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ ERRO no teste 3: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# TESTE 4: Filtro por Fornecedor
Write-Host "🔍 TESTE 4: Filtro por Fornecedor 'Teste'..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/pedidos-agrupados?fornecedorNome=Teste" -Method GET -TimeoutSec 10
    $pedidos = $response.Content | ConvertFrom-Json
    
    Write-Host "   Pedidos do fornecedor 'Teste': $($pedidos.Count)" -ForegroundColor White
    $pedidos | ForEach-Object { 
        Write-Host "   → $($_.idPedido) - Fornecedor: $($_.nomeFornecedor)" -ForegroundColor White 
    }
    
    if ($pedidos.Count -gt 0) {
        Write-Host "✅ SUCESSO: Filtro por fornecedor funcionando!" -ForegroundColor Green
    } else {
        Write-Host "⚠️  PROBLEMA: Nenhum pedido do fornecedor 'Teste' encontrado" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ ERRO no teste 4: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# TESTE 5: CRÍTICO - Verificar se filtro por ID ainda funciona (PRESERVADO)
Write-Host "🔍 TESTE 5: CRÍTICO - Filtro por ID (DEVE ESTAR PRESERVADO)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/pedidos-agrupados?idPedido=PED-1-20250924-007" -Method GET -TimeoutSec 10
    $pedidos = $response.Content | ConvertFrom-Json
    
    if ($pedidos.Count -eq 1) {
        Write-Host "✅ PERFEITO: Filtro por ID preservado e funcionando!" -ForegroundColor Green
        Write-Host "   → $($pedidos[0].idPedido)" -ForegroundColor White
    } else {
        Write-Host "❌ CRÍTICO: Filtro por ID foi afetado! Encontrados $($pedidos.Count) pedidos" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ CRÍTICO: Filtro por ID parou de funcionar: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "🏁 TESTE CONCLUÍDO" -ForegroundColor Cyan
Write-Host "══════════════════════════════════════════════════════════" -ForegroundColor Cyan