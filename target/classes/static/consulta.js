// Estado da aplicação
let ordensCarregadas = [];
let loading = false;

// Elementos do DOM
const loadingDiv = document.getElementById('loadingDiv');
const errorDiv = document.getElementById('errorDiv');
const resultsDiv = document.getElementById('resultsDiv');
const noResultsDiv = document.getElementById('noResultsDiv');
const consultarBtn = document.getElementById('consultarBtn');
const limparFiltrosBtn = document.getElementById('limparFiltros');
const resultsTableBody = document.getElementById('resultsTableBody');
const resultsCount = document.getElementById('resultsCount');
const lastUpdate = document.getElementById('lastUpdate');
const exportPdfBtn = document.getElementById('exportPdfBtn');
const exportExcelBtn = document.getElementById('exportExcelBtn');

// Campos de filtro
const filtros = {
    dataInicio: document.getElementById('dataInicio'),
    dataFim: document.getElementById('dataFim'),
    fornecedor: document.getElementById('fornecedor'),
    produto: document.getElementById('produto'),
    status: document.getElementById('status'),
    valorMinimo: document.getElementById('valorMinimo'),
    valorMaximo: document.getElementById('valorMaximo')
};

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    configurarEventListeners();
    definirDatasPadrao();
    consultarOrdens();
});

function configurarEventListeners() {
    consultarBtn.addEventListener('click', consultarOrdens);
    limparFiltrosBtn.addEventListener('click', limparFiltros);
    
    // Consulta automática ao pressionar Enter nos campos
    Object.values(filtros).forEach(campo => {
        campo.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                consultarOrdens();
            }
        });
    });

    // Validação de datas
    filtros.dataInicio.addEventListener('change', validarDatas);
    filtros.dataFim.addEventListener('change', validarDatas);
    
    // Validação de valores
    filtros.valorMinimo.addEventListener('change', validarValores);
    filtros.valorMaximo.addEventListener('change', validarValores);

    // Export handlers
    exportPdfBtn.addEventListener('click', () => exportarDados('pdf'));
    exportExcelBtn.addEventListener('click', () => exportarDados('excel'));
}

/**
 * Define datas padrão (últimos 30 dias)
 */
function definirDatasPadrao() {
    const hoje = new Date();
    const trintaDiasAtras = new Date();
    trintaDiasAtras.setDate(hoje.getDate() - 30);
    
    filtros.dataFim.value = hoje.toISOString().split('T')[0];
    filtros.dataInicio.value = trintaDiasAtras.toISOString().split('T')[0];
}

/**
 * Valida as datas de início e fim
 */
function validarDatas() {
    const dataInicio = new Date(filtros.dataInicio.value);
    const dataFim = new Date(filtros.dataFim.value);
    
    if (filtros.dataInicio.value && filtros.dataFim.value && dataInicio > dataFim) {
        showError('A data de início não pode ser posterior à data de fim.');
        filtros.dataInicio.style.borderColor = '#e74c3c';
        filtros.dataFim.style.borderColor = '#e74c3c';
        return false;
    } else {
        filtros.dataInicio.style.borderColor = '#ddd';
        filtros.dataFim.style.borderColor = '#ddd';
        hideError();
        return true;
    }
}

/**
 * Valida os valores mínimo e máximo
 */
function validarValores() {
    const valorMinimo = parseFloat(filtros.valorMinimo.value);
    const valorMaximo = parseFloat(filtros.valorMaximo.value);
    
    if (!isNaN(valorMinimo) && !isNaN(valorMaximo) && valorMinimo > valorMaximo) {
        showError('O valor mínimo não pode ser maior que o valor máximo.');
        filtros.valorMinimo.style.borderColor = '#e74c3c';
        filtros.valorMaximo.style.borderColor = '#e74c3c';
        return false;
    } else {
        filtros.valorMinimo.style.borderColor = '#ddd';
        filtros.valorMaximo.style.borderColor = '#ddd';
        hideError();
        return true;
    }
}

/**
 * Consulta as ordens de compra
 */
async function consultarOrdens() {
    if (loading) return;

    // Validações
    if (!validarDatas() || !validarValores()) {
        return;
    }

    try {
        loading = true;
        consultarBtn.disabled = true;
        consultarBtn.textContent = 'Consultando...';
        mostrarLoading(true);
        hideError();
        hideResults();

        // Construir URL com parâmetros
        const params = new URLSearchParams();
        
        Object.entries(filtros).forEach(([key, elemento]) => {
            if (elemento.value && elemento.value.trim() !== '') {
                params.append(key, elemento.value.trim());
            }
        });

        const url = `/api/ordens-de-compra${params.toString() ? '?' + params.toString() : ''}`;
        
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Basic ' + btoa('admin:admin')
            }
        });

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status} - ${response.statusText}`);
        }

        const ordens = await response.json();
        
        if (!Array.isArray(ordens)) {
            throw new Error('Formato de resposta inválido');
        }

        ordensCarregadas = ordens;
        exibirResultados(ordens);

    } catch (error) {
        console.error('Erro ao consultar ordens:', error);
        showError('Erro ao consultar ordens de compra: ' + error.message);
    } finally {
        loading = false;
        consultarBtn.disabled = false;
        consultarBtn.textContent = '🔍 Consultar';
        mostrarLoading(false);
    }
}

/**
 * Exibe os resultados da consulta
 */
function exibirResultados(ordens) {
    if (ordens.length === 0) {
        mostrarSemResultados();
        return;
    }

    resultsTableBody.innerHTML = '';
    
    ordens.forEach(ordem => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${ordem.id}</td>
            <td class="date-column">${formatarData(ordem.dataCotacao)}</td>
            <td>
                <strong>${ordem.fornecedor.nomeFantasia}</strong><br>
                <small>${ordem.fornecedor.razaoSocial}</small>
            </td>
            <td>
                <strong>${ordem.produto.nome}</strong><br>
                <small>${ordem.produto.descricao || ''}</small>
            </td>
            <td>${ordem.quantidade} ${ordem.unimedida.sigla}</td>
            <td class="currency">R$ ${formatarMoeda(ordem.valorUnitario)}</td>
            <td class="currency">R$ ${formatarMoeda(ordem.valorTotal)}</td>
            <td>
                <span class="status-badge status-${ordem.status}">
                    ${traduzirStatus(ordem.status)}
                </span>
            </td>
            <td>${ordem.observacoes || '-'}</td>
        `;
        resultsTableBody.appendChild(tr);
    });

    // Atualizar estatísticas
    const totalOrdens = ordens.length;
    const valorTotal = ordens.reduce((sum, ordem) => sum + parseFloat(ordem.valorTotal), 0);
    
    resultsCount.innerHTML = `
        ${totalOrdens} ordem${totalOrdens !== 1 ? 's' : ''} encontrada${totalOrdens !== 1 ? 's' : ''} 
        • Valor total: R$ ${formatarMoeda(valorTotal)}
    `;
    
    lastUpdate.textContent = `Última atualização: ${new Date().toLocaleString('pt-BR')}`;
    
    mostrarResultados();
    habilitarExportacao(true);
}

/**
 * Limpa todos os filtros
 */
function limparFiltros() {
    Object.values(filtros).forEach(campo => {
        campo.value = '';
        campo.style.borderColor = '#ddd';
    });
    
    definirDatasPadrao();
    hideError();
    hideResults();
    habilitarExportacao(false);
}

/**
 * Exporta os dados em formato especificado
 */
function exportarDados(formato) {
    if (ordensCarregadas.length === 0) {
        showError('Não há dados para exportar. Execute uma consulta primeiro.');
        return;
    }

    try {
        if (formato === 'pdf') {
            exportarPDF();
        } else if (formato === 'excel') {
            exportarExcel();
        }
    } catch (error) {
        console.error('Erro na exportação:', error);
        showError('Erro ao exportar dados: ' + error.message);
    }
}

/**
 * Exporta para PDF (simulado)
 */
function exportarPDF() {
    // Criar conteúdo para impressão
    const conteudo = criarConteudoParaImpressao();
    
    // Abrir em nova janela para impressão
    const janelaImpressao = window.open('', '_blank');
    janelaImpressao.document.write(conteudo);
    janelaImpressao.document.close();
    janelaImpressao.print();
}

/**
 * Exporta para Excel (CSV)
 */
function exportarExcel() {
    const headers = [
        'ID', 'Data', 'Fornecedor', 'Razão Social', 'Produto', 
        'Descrição', 'Quantidade', 'Unidade', 'Valor Unitário', 
        'Valor Total', 'Status', 'Observações'
    ];
    
    let csvContent = headers.join(',') + '\n';
    
    ordensCarregadas.forEach(ordem => {
        const row = [
            ordem.id,
            formatarData(ordem.dataCotacao),
            `"${ordem.fornecedor.nomeFantasia}"`,
            `"${ordem.fornecedor.razaoSocial}"`,
            `"${ordem.produto.nome}"`,
            `"${ordem.produto.descricao || ''}"`,
            ordem.quantidade,
            ordem.unimedida.sigla,
            ordem.valorUnitario.toString().replace('.', ','),
            ordem.valorTotal.toString().replace('.', ','),
            traduzirStatus(ordem.status),
            `"${ordem.observacoes || ''}"`
        ];
        csvContent += row.join(',') + '\n';
    });
    
    // Download do arquivo
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `ordens-compra-${new Date().toISOString().split('T')[0]}.csv`;
    link.click();
}

/**
 * Cria conteúdo HTML para impressão
 */
function criarConteudoParaImpressao() {
    let html = `
        <!DOCTYPE html>
        <html>
        <head>
            <title>Relatório de Ordens de Compra</title>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; font-size: 12px; }
                .header { text-align: center; margin-bottom: 20px; }
                .filters { margin-bottom: 20px; padding: 10px; background-color: #f5f5f5; }
                table { width: 100%; border-collapse: collapse; font-size: 10px; }
                th, td { border: 1px solid #ddd; padding: 4px; text-align: left; }
                th { background-color: #3498db; color: white; }
                .currency { text-align: right; }
                .date-column { white-space: nowrap; }
                @media print { .no-print { display: none; } }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>Relatório de Ordens de Compra</h1>
                <p>Gerado em: ${new Date().toLocaleString('pt-BR')}</p>
            </div>
    `;
    
    // Adicionar filtros aplicados
    const filtrosAplicados = Object.entries(filtros)
        .filter(([key, elemento]) => elemento.value)
        .map(([key, elemento]) => `${key}: ${elemento.value}`);
    
    if (filtrosAplicados.length > 0) {
        html += `
            <div class="filters">
                <strong>Filtros aplicados:</strong> ${filtrosAplicados.join(' • ')}
            </div>
        `;
    }
    
    html += `<table>`;
    html += `
        <thead>
            <tr>
                <th>ID</th><th>Data</th><th>Fornecedor</th><th>Produto</th>
                <th>Qtd</th><th>Vlr Unit.</th><th>Vlr Total</th><th>Status</th>
            </tr>
        </thead>
        <tbody>
    `;
    
    ordensCarregadas.forEach(ordem => {
        html += `
            <tr>
                <td>${ordem.id}</td>
                <td class="date-column">${formatarData(ordem.dataCotacao)}</td>
                <td>${ordem.fornecedor.nomeFantasia}</td>
                <td>${ordem.produto.nome}</td>
                <td>${ordem.quantidade} ${ordem.unimedida.sigla}</td>
                <td class="currency">R$ ${formatarMoeda(ordem.valorUnitario)}</td>
                <td class="currency">R$ ${formatarMoeda(ordem.valorTotal)}</td>
                <td>${traduzirStatus(ordem.status)}</td>
            </tr>
        `;
    });
    
    const valorTotal = ordensCarregadas.reduce((sum, ordem) => sum + parseFloat(ordem.valorTotal), 0);
    
    html += `
        </tbody>
        <tfoot>
            <tr style="font-weight: bold; background-color: #f8f9fa;">
                <td colspan="6">TOTAL GERAL</td>
                <td class="currency">R$ ${formatarMoeda(valorTotal)}</td>
                <td>${ordensCarregadas.length} ordens</td>
            </tr>
        </tfoot>
    </table>
    </body>
    </html>
    `;
    
    return html;
}

/**
 * Utilitários de interface
 */
function mostrarLoading(show) {
    loadingDiv.style.display = show ? 'block' : 'none';
}

function mostrarResultados() {
    resultsDiv.style.display = 'block';
    noResultsDiv.style.display = 'none';
}

function mostrarSemResultados() {
    resultsDiv.style.display = 'none';
    noResultsDiv.style.display = 'block';
    habilitarExportacao(false);
}

function hideResults() {
    resultsDiv.style.display = 'none';
    noResultsDiv.style.display = 'none';
}

function showError(message) {
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}

function hideError() {
    errorDiv.style.display = 'none';
}

function habilitarExportacao(habilitar) {
    exportPdfBtn.disabled = !habilitar;
    exportExcelBtn.disabled = !habilitar;
}

function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(valor);
}

function formatarData(dataString) {
    const data = new Date(dataString);
    return data.toLocaleDateString('pt-BR');
}

function traduzirStatus(status) {
    const traducoes = {
        'PENDENTE': 'Pendente',
        'APROVADO': 'Aprovado',
        'REJEITADO': 'Rejeitado'
    };
    return traducoes[status] || status;
}