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
    exportPdfBtn.addEventListener('click', () => exportarPDFsSelecionados());
    
    // Checkbox "Selecionar Todos"
    const selectAllCheckbox = document.getElementById('selectAllCheckbox');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', toggleSelecionarTodos);
    }
}

/**
 * Função para marcar/desmarcar todas as checkboxes de pedidos aprovados
 */
function toggleSelecionarTodos() {
    const selectAllCheckbox = document.getElementById('selectAllCheckbox');
    const checkboxes = document.querySelectorAll('.pedido-checkbox');
    
    checkboxes.forEach(checkbox => {
        checkbox.checked = selectAllCheckbox.checked;
    });
    
    atualizarSelecao();
}

/**
 * Atualiza o estado da checkbox "Selecionar Todos" e do botão exportar
 */
function atualizarSelecao() {
    const selectAllCheckbox = document.getElementById('selectAllCheckbox');
    const checkboxes = document.querySelectorAll('.pedido-checkbox');
    const checkboxesMarcadas = document.querySelectorAll('.pedido-checkbox:checked');
    
    // Atualizar estado da checkbox "Selecionar Todos"
    if (checkboxesMarcadas.length === 0) {
        selectAllCheckbox.indeterminate = false;
        selectAllCheckbox.checked = false;
    } else if (checkboxesMarcadas.length === checkboxes.length) {
        selectAllCheckbox.indeterminate = false;
        selectAllCheckbox.checked = true;
    } else {
        selectAllCheckbox.indeterminate = true;
        selectAllCheckbox.checked = false;
    }
    
    // Habilitar/desabilitar botão de exportar
    exportPdfBtn.disabled = checkboxesMarcadas.length === 0;
}

/**
 * Exporta PDFs dos pedidos selecionados
 */
function exportarPDFsSelecionados() {
    const checkboxesMarcadas = document.querySelectorAll('.pedido-checkbox:checked');
    
    if (checkboxesMarcadas.length === 0) {
        showError('Selecione pelo menos um pedido para exportar.');
        return;
    }
    
    const idsPedidos = Array.from(checkboxesMarcadas).map(cb => cb.value);
    
    if (idsPedidos.length === 1) {
        // Um único PDF - abrir diretamente
        window.open(`/api/pedidos-agrupados/pdf/${idsPedidos[0]}`, '_blank');
    } else {
        // Múltiplos PDFs - baixar em sequência
        baixarMultiplosPDFs(idsPedidos);
    }
}

/**
 * Baixa múltiplos PDFs em sequência
 */
function baixarMultiplosPDFs(idsPedidos) {
    let contador = 0;
    const total = idsPedidos.length;
    
    // Mostrar progresso
    const mensagemOriginal = exportPdfBtn.textContent;
    exportPdfBtn.disabled = true;
    
    function baixarProximo() {
        if (contador >= total) {
            exportPdfBtn.textContent = mensagemOriginal;
            exportPdfBtn.disabled = false;
            return;
        }
        
        const idPedido = idsPedidos[contador];
        exportPdfBtn.textContent = `Baixando ${contador + 1}/${total}...`;
        
        // Criar link de download temporário
        const link = document.createElement('a');
        link.href = `/api/pedidos-agrupados/pdf/${idPedido}`;
        link.download = `Pedido_${idPedido}.pdf`;
        link.target = '_blank';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        contador++;
        setTimeout(baixarProximo, 1000); // Aguardar 1 segundo entre downloads
    }
    
    baixarProximo();
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
        const valor = idPedido.trim();
        
        // Aceitar formato PED-X-YYYYMMDD-XXX ou números simples
        const formatoPedido = /^PED-\d+-\d{8}-\d{3}$/;
        const numeroSimples = /^\d+$/;
        
        if (!formatoPedido.test(valor) && !numeroSimples.test(valor)) {
            showError('O ID do pedido deve ser um número ou seguir o formato PED-X-YYYYMMDD-XXX.');
            filtros.idPedido.style.borderColor = '#e74c3c';
            return false;
        }
        
        // Se for número simples, validar se é positivo
        if (numeroSimples.test(valor)) {
            const id = parseInt(valor);
            if (id <= 0) {
                showError('O ID do pedido deve ser um número positivo válido.');
                filtros.idPedido.style.borderColor = '#e74c3c';
                return false;
            }
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
            const idPedido = filtros.idPedido.value.trim();
            // Se for um ID de pedido (formato PED-X-YYYYMMDD-XXX), usar como idPedido
            // Se for um número, usar como idOrcamento para compatibilidade
            if (idPedido.startsWith('PED-')) {
                params.append('idPedido', idPedido);
            } else {
                const id = parseInt(idPedido);
                if (!isNaN(id) && id > 0) {
                    params.append('idOrcamento', id.toString());
                }
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

        // NOVO: Usar endpoint de pedidos agrupados para gerar IDs automáticos
        const url = `/api/pedidos-agrupados${params.toString() ? '?' + params.toString() : ''}`;
        
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
function exibirResultados(pedidosAgrupados) {
    if (pedidosAgrupados.length === 0) {
        mostrarSemResultados();
        return;
    }

    resultsTableBody.innerHTML = '';
    
    pedidosAgrupados.forEach(pedido => {
        const tr = document.createElement('tr');
        
        // Usar os novos dados do sistema de pedidos agrupados
        const isAprovado = (pedido.status === 'Aprovado' || pedido.status === 'APROVADO');
        tr.innerHTML = `
            <td>
                ${isAprovado ? 
                    `<input type="checkbox" class="pedido-checkbox" value="${pedido.idPedido}" onchange="atualizarSelecao()">` : 
                    '<span style="width: 16px; display: inline-block;"></span>'
                }
            </td>
            <td><strong>${pedido.idPedido}</strong></td>
            <td>${Array.isArray(pedido.idOrcamentos) ? pedido.idOrcamentos.join(', ') : pedido.idOrcamentos}</td>
            <td>${Array.isArray(pedido.nomesProdutos) ? pedido.nomesProdutos.join(', ') : (pedido.nomesProdutos || 'N/A')}</td>
            <td><strong>${pedido.nomeFornecedor}</strong></td>
            <td class="date-column">${pedido.rangeDataEmissao}</td>
            <td class="currency">R$ ${formatarMoeda(pedido.valorTotal)}</td>
            <td>
                <span class="status-badge status-${pedido.status}">
                    ${traduzirStatus(pedido.status)}
                </span>
            </td>
            <td>
                ${isAprovado ? 
                    `<button type="button" class="btn-action btn-pdf" onclick="gerarPDFPedido('${pedido.idPedido}')" title="Gerar PDF">
                        📄 PDF
                    </button>` : 
                    '<span style="color: #999;">Não disponível</span>'
                }
            </td>
        `;
        resultsTableBody.appendChild(tr);
    });

    // Atualizar estatísticas para pedidos agrupados
    const totalPedidos = pedidosAgrupados.length;
    const totalOrcamentos = pedidosAgrupados.reduce((sum, pedido) => {
        return sum + (Array.isArray(pedido.idOrcamentos) ? pedido.idOrcamentos.length : 1);
    }, 0);
    const valorTotal = pedidosAgrupados.reduce((sum, pedido) => sum + parseFloat(pedido.valorTotal), 0);
    
    resultsCount.innerHTML = `
        ${totalPedidos} pedido${totalPedidos !== 1 ? 's' : ''} agrupado${totalPedidos !== 1 ? 's' : ''} encontrado${totalPedidos !== 1 ? 's' : ''} 
        • ${totalOrcamentos} orçamento${totalOrcamentos !== 1 ? 's' : ''}
        • Valor total: R$ ${formatarMoeda(valorTotal)}
    `;
    
    lastUpdate.textContent = `Última atualização: ${new Date().toLocaleString('pt-BR')}`;
    
    mostrarResultados();
    habilitarExportacao(true);
    
    // Inicializar estado das checkboxes
    atualizarSelecao();
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
 * Ver detalhes de um pedido agrupado
 */
function verDetalhesPedido(idPedido) {
    const pedido = ordensCarregadas.find(p => p.idPedido === idPedido);
    if (pedido) {
        const orcamentosStr = Array.isArray(pedido.idOrcamentos) ? pedido.idOrcamentos.join(', ') : pedido.idOrcamentos;
        const produtosStr = Array.isArray(pedido.nomesProdutos) ? pedido.nomesProdutos.join(', ') : (pedido.produtosDescricao || 'N/A');
        
        alert(`Detalhes do Pedido ${idPedido}:\n\n` +
              `Fornecedor: ${pedido.nomeFornecedor} (ID: ${pedido.idFornecedor})\n` +
              `Orçamentos: ${orcamentosStr}\n` +
              `Produtos: ${produtosStr}\n` +
              `Quantidade de Produtos: ${pedido.quantidadeProdutos || 'N/A'}\n` +
              `Valor Total: R$ ${formatarMoeda(pedido.valorTotal)}\n` +
              `Status: ${traduzirStatus(pedido.status)}\n` +
              `Período de Emissão: ${pedido.rangeDataEmissao}\n` +
              `Data de Geração: ${formatarData(pedido.dataGeracao)}\n` +
              `Usuário Aprovador: ${pedido.nomeUsuarioAprovador || 'N/A'}`);
    }
}

/**
 * Gerar PDF de um pedido agrupado
 */
function gerarPDFPedido(idPedido) {
    // Usar o novo endpoint específico para pedidos agrupados
    window.open(`/api/pedidos-agrupados/pdf/${idPedido}`, '_blank');
}

/**
 * Ver detalhes de um orçamento (função legada mantida para compatibilidade)
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
 * Gerar PDF de um orçamento específico (função legada mantida para compatibilidade)
 */
function gerarPDF(idOrcamento) {
    window.open(`/api/ordens-de-compra/visualizar/${idOrcamento}`, '_blank');
}