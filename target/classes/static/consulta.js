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

// Campos de filtro
const filtros = {
    idPedido: document.getElementById('idPedido'),
    dataInicio: document.getElementById('dataInicio'),
    dataFim: document.getElementById('dataFim'),
    fornecedor: document.getElementById('fornecedor'),
    produto: document.getElementById('produto'),
    status: document.getElementById('status')
};

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    configurarEventListeners();
    // Carregar todas as ordens automaticamente na inicialização
    consultarOrdens();
});

function configurarEventListeners() {
    consultarBtn.addEventListener('click', consultarOrdens);
    limparFiltrosBtn.addEventListener('click', limparFiltros);
    
    // Consulta automática ao pressionar Enter nos campos
    Object.values(filtros).forEach(campo => {
        if (campo) { // Verificar se o elemento existe
            campo.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    consultarOrdens();
                }
            });
        }
    });

    // Validação de datas
    if (filtros.dataInicio) filtros.dataInicio.addEventListener('change', validarDatas);
    if (filtros.dataFim) filtros.dataFim.addEventListener('change', validarDatas);
    

    
    // Validação de ID do pedido
    if (filtros.idPedido) {
        filtros.idPedido.addEventListener('input', validarIdPedido);
        filtros.idPedido.addEventListener('change', validarIdPedido);
    }

    // Export handlers
    exportPdfBtn.addEventListener('click', () => exportarDados('pdf'));
}



/**
 * Valida as datas de início e fim
 */
function validarDatas() {
    const dataInicioValue = filtros.dataInicio.value;
    const dataFimValue = filtros.dataFim.value;
    
    // Se ambas as datas estão preenchidas, validar se data final é maior que inicial
    if (dataInicioValue && dataFimValue) {
        const dataInicio = new Date(dataInicioValue);
        const dataFim = new Date(dataFimValue);
        
        if (dataInicio > dataFim) {
            showError('A data final não pode ser anterior à data inicial.');
            filtros.dataInicio.style.borderColor = '#e74c3c';
            filtros.dataFim.style.borderColor = '#e74c3c';
            return false;
        }
    }
    
    // Reset das bordas se validação passou
    filtros.dataInicio.style.borderColor = '#d9d9d9';
    filtros.dataFim.style.borderColor = '#d9d9d9';
    hideError();
    return true;
}



/**
 * Valida o ID do pedido
 */
function validarIdPedido() {
    const idPedido = filtros.idPedido.value;
    
    if (idPedido && idPedido.trim() !== '') {
        const id = parseInt(idPedido.trim());
        if (isNaN(id) || id <= 0) {
            showError('O ID do pedido deve ser um número positivo válido.');
            filtros.idPedido.style.borderColor = '#e74c3c';
            return false;
        }
    }
    
    filtros.idPedido.style.borderColor = '#d9d9d9';
    hideError();
    return true;
}

/**
 * Valida todos os filtros antes da consulta
 */
function validarTodosFiltros() {
    let valido = true;
    
    // Validar datas
    if (!validarDatas()) valido = false;
    
    // Validar ID do pedido
    if (!validarIdPedido()) valido = false;
    
    return valido;
}

/**
 * Consulta as ordens de compra
 */
async function consultarOrdens() {
    if (loading) return;

    // Validações
    if (!validarTodosFiltros()) {
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
        
        // Construir parâmetros de forma mais clara
        if (filtros.idPedido.value && filtros.idPedido.value.trim() !== '') {
            const id = parseInt(filtros.idPedido.value.trim());
            if (!isNaN(id) && id > 0) {
                params.append('idOrcamento', id.toString());
            }
        }
        
        if (filtros.dataInicio.value && filtros.dataInicio.value.trim() !== '') {
            params.append('dataInicial', filtros.dataInicio.value.trim());
        }
        
        if (filtros.dataFim.value && filtros.dataFim.value.trim() !== '') {
            params.append('dataFinal', filtros.dataFim.value.trim());
        }
        
        if (filtros.fornecedor.value && filtros.fornecedor.value.trim() !== '' && filtros.fornecedor.value.trim().length >= 2) {
            params.append('fornecedorNome', filtros.fornecedor.value.trim());
        }
        
        if (filtros.produto.value && filtros.produto.value.trim() !== '' && filtros.produto.value.trim().length >= 2) {
            params.append('produtoNome', filtros.produto.value.trim());
        }
        
        if (filtros.status.value && filtros.status.value.trim() !== '') {
            params.append('status', filtros.status.value.trim());
        }

        const url = `/api/ordens-de-compra${params.toString() ? '?' + params.toString() : ''}`;
        
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 404) {
                showError('Nenhuma ordem de compra foi encontrada com os critérios informados.');
                mostrarSemResultados();
                return;
            }
            throw new Error(`Erro HTTP: ${response.status} - ${response.statusText}`);
        }

        const ordens = await response.json();
        
        if (!Array.isArray(ordens)) {
            throw new Error('Formato de resposta inválido');
        }

        console.log('Ordens recebidas:', ordens);
        // Backend já faz a filtragem, não precisamos filtrar localmente
        ordensCarregadas = ordens;
        exibirResultados(ordens);

    } catch (error) {
        console.error('Erro ao consultar ordens:', error);
        mostrarSemResultados();
    } finally {
        loading = false;
        consultarBtn.disabled = false;
        consultarBtn.textContent = '🔍 Consultar';
        mostrarLoading(false);
    }
}

/**
 * Filtra os resultados localmente para garantir consistência
 */
function filtrarResultadosLocalmente(ordens) {
    return ordens.filter(ordem => {
        // Filtro por ID
        if (filtros.idPedido.value && filtros.idPedido.value.trim() !== '') {
            const idBuscado = parseInt(filtros.idPedido.value.trim());
            if (ordem.id !== idBuscado) return false;
        }
        
        // Filtro por fornecedor (busca parcial, case-insensitive)
        if (filtros.fornecedor.value && filtros.fornecedor.value.trim() !== '') {
            const fornecedorBuscado = filtros.fornecedor.value.trim().toLowerCase();
            const nomeFantasia = (ordem.fornecedor.nomeFantasia || '').toLowerCase();
            const razaoSocial = (ordem.fornecedor.razaoSocial || '').toLowerCase();
            if (!nomeFantasia.includes(fornecedorBuscado) && !razaoSocial.includes(fornecedorBuscado)) {
                return false;
            }
        }
        
        // Filtro por produto (busca parcial, case-insensitive)
        if (filtros.produto.value && filtros.produto.value.trim() !== '') {
            const produtoBuscado = filtros.produto.value.trim().toLowerCase();
            const nomeProduto = (ordem.produto.nome || '').toLowerCase();
            const descricaoProduto = (ordem.produto.descricao || '').toLowerCase();
            if (!nomeProduto.includes(produtoBuscado) && !descricaoProduto.includes(produtoBuscado)) {
                return false;
            }
        }
        
        // Filtro por data
        if (filtros.dataInicio.value) {
            const dataInicio = new Date(filtros.dataInicio.value);
            const dataOrdem = new Date(ordem.dataCotacao);
            if (dataOrdem < dataInicio) return false;
        }
        
        if (filtros.dataFim.value) {
            const dataFim = new Date(filtros.dataFim.value);
            const dataOrdem = new Date(ordem.dataCotacao);
            if (dataOrdem > dataFim) return false;
        }
        
        // Filtro por status
        if (filtros.status.value && filtros.status.value !== '') {
            if (ordem.status !== filtros.status.value) return false;
        }
        
        // Filtro por valor mínimo
        if (filtros.valorMinimo.value && filtros.valorMinimo.value.trim() !== '') {
            const valorMinimo = parseFloat(filtros.valorMinimo.value);
            if (!isNaN(valorMinimo) && parseFloat(ordem.valorTotal) < valorMinimo) {
                return false;
            }
        }
        
        // Filtro por valor máximo
        if (filtros.valorMaximo.value && filtros.valorMaximo.value.trim() !== '') {
            const valorMaximo = parseFloat(filtros.valorMaximo.value);
            if (!isNaN(valorMaximo) && parseFloat(ordem.valorTotal) > valorMaximo) {
                return false;
            }
        }
        
        return true;
    });
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
        
        // ID do Pedido - só existe se o orçamento foi aprovado (tem dataGeracao)
        const idPedido = (ordem.status === 'Aprovado' || ordem.status === 'APROVADO') ? `PED-${ordem.idOrcamento}` : '-';
        
        tr.innerHTML = `
            <td>${idPedido}</td>
            <td>${ordem.idOrcamento}</td>
            <td>
                <strong>${ordem.nomeFornecedor}</strong><br>
                <small>${ordem.descricaoFornecedor || ''}</small>
            </td>
            <td class="date-column">${formatarData(ordem.dataEmissao)}</td>
            <td class="currency">R$ ${formatarMoeda(ordem.valorTotal)}</td>
            <td>
                <span class="status-badge status-${ordem.status}">
                    ${traduzirStatus(ordem.status)}
                </span>
            </td>
            <td>
                <button type="button" class="btn-action btn-detail" onclick="verDetalhes(${ordem.idOrcamento})" title="Ver Detalhes">
                    👁️ Ver
                </button>
                ${(ordem.status === 'Aprovado' || ordem.status === 'APROVADO') ? 
                    `<button type="button" class="btn-action btn-pdf" onclick="gerarPDF(${ordem.idOrcamento})" title="Gerar PDF">
                        📄 PDF
                    </button>` : ''
                }
            </td>
        `;
        resultsTableBody.appendChild(tr);
    });

    // Atualizar estatísticas
    const totalOrdens = ordens.length;
    const valorTotal = ordens.reduce((sum, ordem) => sum + parseFloat(ordem.valorTotal.toString().replace(',', '.')), 0);
    
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
        if (campo) {
            campo.value = '';
            campo.style.borderColor = '#d9d9d9';
        }
    });
    
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
                <td>${ordem.idOrcamento}</td>
                <td class="date-column">${formatarData(ordem.dataEmissao)}</td>
                <td>${ordem.nomeFornecedor}</td>
                <td>${ordem.nomeProduto}</td>
                <td>${ordem.quantidade} ${ordem.unidadeAbreviacao}</td>
                <td class="currency">R$ ${formatarMoeda(ordem.precoCompra)}</td>
                <td class="currency">R$ ${formatarMoeda(ordem.valorTotal)}</td>
                <td>${traduzirStatus(ordem.status)}</td>
            </tr>
        `;
    });
    
    const valorTotal = ordensCarregadas.reduce((sum, ordem) => sum + parseFloat(ordem.valorTotal.toString().replace(',', '.')), 0);
    
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
}

function formatarMoeda(valor) {
    // Converter string com vírgula para número
    let numeroValor = typeof valor === 'string' ? parseFloat(valor.replace(',', '.')) : valor;
    return new Intl.NumberFormat('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(numeroValor);
}

function formatarData(dataString) {
    if (!dataString) return '-';
    // Evitar problema de fuso horário tratando como data local
    const [ano, mes, dia] = dataString.split('T')[0].split('-');
    const data = new Date(ano, mes - 1, dia); // mes - 1 porque o mês no JS é 0-indexed
    return data.toLocaleDateString('pt-BR');
}

function traduzirStatus(status) {
    const traducoes = {
        'PENDENTE': 'Pendente',
        'APROVADO': 'Aprovado',
        'REPROVADO': 'Reprovado',
        'REJEITADO': 'Reprovado' // Compatibilidade
    };
    return traducoes[status] || status;
}

/**
 * Ver detalhes de um orçamento
 */
function verDetalhes(idOrcamento) {
    const ordem = ordensCarregadas.find(o => o.idOrcamento === idOrcamento);
    if (ordem) {
        alert(`Detalhes do Orçamento ${idOrcamento}:\n\n` +
              `Fornecedor: ${ordem.nomeFornecedor}\n` +
              `Produto: ${ordem.nomeProduto}\n` +
              `Quantidade: ${ordem.quantidade} ${ordem.unidadeAbreviacao}\n` +
              `Valor Unitário: R$ ${formatarMoeda(ordem.precoCompra)}\n` +
              `Valor Total: R$ ${formatarMoeda(ordem.valorTotal)}\n` +
              `Status: ${traduzirStatus(ordem.status)}\n` +
              `Data de Emissão: ${formatarData(ordem.dataEmissao)}\n` +
              `Garantia: ${ordem.garantia || 'N/A'}\n` +
              `Condições de Pagamento: ${ordem.condicoesPagamento || 'N/A'}`);
    }
}

/**
 * Gerar PDF de um orçamento específico
 */
function gerarPDF(idOrcamento) {
    window.open(`/api/ordens-de-compra/visualizar/${idOrcamento}`, '_blank');
}