// Estado da aplicação
let ordensCarregadas = [];

// Elementos do DOM
const loadingDiv = document.getElementById('loadingDiv');
const errorDiv = document.getElementById('errorDiv');
const resultsDiv = document.getElementById('resultsDiv');
const noResultsDiv = document.getElementById('noResultsDiv');
const consultarBtn = document.getElementById('consultarBtn');
const limparFiltrosBtn = document.getElementById('limparFiltros');
const resultsTableBody = document.getElementById('resultsTableBody');
const baixarBtn = document.getElementById('baixarBtn');
const selectAllCheckbox = document.getElementById('selectAllCheckbox');
const resultsCount = document.getElementById('resultsCount');

// Campos de filtro
const filtros = {
    idOrcamento: document.getElementById('idPedido'),
    dataInicial: document.getElementById('dataInicio'),
    dataFinal: document.getElementById('dataFim'),
    fornecedor: document.getElementById('fornecedor'), 
    produto: document.getElementById('produto'),
    status: document.getElementById('status')
};

// Event listeners
document.addEventListener('DOMContentLoaded', configurarEventListeners);

function configurarEventListeners() {
    consultarBtn.addEventListener('click', consultarOrdens);
    limparFiltrosBtn.addEventListener('click', limparFiltros);
    if (baixarBtn) baixarBtn.addEventListener('click', baixarSelecionados);
    if (selectAllCheckbox) selectAllCheckbox.addEventListener('change', selecionarTodos);
}

/**
 * Ponto 2: Validação de datas
 */
function validarDatas() {
    const dataInicio = filtros.dataInicial.value;
    const dataFim = filtros.dataFinal.value;
    if (dataInicio && dataFim && new Date(dataInicio) > new Date(dataFim)) {
        alert("Erro: A data de início não pode ser posterior à data de fim.");
        return false;
    }
    return true;
}

/**
 * Consulta as ordens de compra com base nos filtros
 */
async function consultarOrdens() {
    if (!validarDatas()) {
        return; 
    }
    mostrarLoading(true);
    hideMessages();
    resultsTableBody.innerHTML = '';
    if (baixarBtn) baixarBtn.disabled = true;
    if (selectAllCheckbox) selectAllCheckbox.checked = false;

    // Ponto 1: Filtros são independentes
    const params = new URLSearchParams();
    if (filtros.dataInicial.value) params.append('dataInicial', filtros.dataInicial.value);
    if (filtros.dataFinal.value) params.append('dataFinal', filtros.dataFinal.value);
    if (filtros.idOrcamento.value) params.append('idOrcamento', filtros.idOrcamento.value);
    if (filtros.fornecedor.value) params.append('fornecedorNome', filtros.fornecedor.value); 
    if (filtros.produto.value) params.append('produtoNome', filtros.produto.value);
    // Só envia o status se não for 'Todos' (string vazia)
    if (filtros.status.value) params.append('status', filtros.status.value);

    try {
        const response = await fetch(`/api/ordens-de-compra?${params.toString()}`);
        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        const ordens = await response.json();
        ordensCarregadas = ordens;
        exibirResultados(ordens);

    } catch (error) {
        console.error("Erro ao consultar ordens:", error);
        showError("Falha ao buscar os dados. Verifique o console para mais detalhes.");
        mostrarSemResultados();
    } finally {
        mostrarLoading(false);
    }
}

/**
 * Exibe os resultados da consulta na tabela
 */
function exibirResultados(ordens) {
    if (ordens.length === 0) {
        mostrarSemResultados();
        return;
    }

    ordens.forEach(ordem => {
        const tr = document.createElement('tr');
        const valorTotal = (ordem.precoCompra || 0) * (ordem.quantidade || 0);
        
        // CORREÇÃO: Compara o status em minúsculas para habilitar a checkbox e as ações
        const isAprovado = (ordem.status || '').toLowerCase() === 'aprovado';

        tr.innerHTML = `
            <td>
                <input type="checkbox" class="orcamento-checkbox" value="${ordem.idOrcamento}" ${!isAprovado ? 'disabled' : ''}>
            </td>
            <td>${ordem.idOrcamento}</td>
            <td>${formatarData(ordem.dataGeracao || ordem.dataEmissao)}</td>
            <td>${ordem.nomeFornecedor || 'N/A'}</td>
            <td>${ordem.nomeProduto || 'N/A'}</td>
            <td>${ordem.quantidade} ${ordem.unidadeAbreviacao || ''}</td>
            <td class="currency">${formatarMoeda(valorTotal)}</td>
            <td><span class="status-badge status-${(ordem.status || '').toLowerCase().replace(/\s/g, '')}">${ordem.status}</span></td>
            <td class="actions-cell">
                ${isAprovado ? 
                    `<button class="btn-icon" title="Visualizar PDF" onclick="visualizarPdf(${ordem.idOrcamento})">📄</button>` 
                    : ''
                }
            </td>
        `;
        resultsTableBody.appendChild(tr);
    });

    resultsCount.textContent = `${ordens.length} registro(s) encontrado(s).`;

    document.querySelectorAll('.orcamento-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', atualizarEstadoDownload);
    });

    mostrarResultados();
}

/**
 * Ponto 3: Abre o PDF de um único pedido em uma nova aba para visualização
 */
function visualizarPdf(id) {
    // CORREÇÃO: A URL agora inclui /api/
    window.open(`/api/ordens-de-compra/download?ids=${id}`, '_blank');
}

/**
 * Ponto 4: Baixa um arquivo ZIP com os PDFs dos itens selecionados
 */
function baixarSelecionados() {
    const checkboxes = document.querySelectorAll('.orcamento-checkbox:checked');
    const idsParaBaixar = Array.from(checkboxes).map(cb => cb.value);

    if (idsParaBaixar.length === 0) {
        alert("Por favor, selecione pelo menos uma ordem de compra APROVADA para baixar.");
        return;
    }

    const params = new URLSearchParams();
    idsParaBaixar.forEach(id => params.append('ids', id));
    
    // CORREÇÃO: A URL agora inclui /api/ e usa uma técnica mais robusta para download
    const url = `/api/ordens-de-compra/download?${params.toString()}`;
    
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Não foi possível baixar o arquivo.');
            }
            const header = response.headers.get('Content-Disposition');
            const parts = header.split(';');
            const filename = parts.length > 1 ? parts[1].split('=')[1].replace(/"/g, '') : 'OrdensDeCompra.zip';
            return Promise.all([response.blob(), filename]);
        })
        .then(([blob, filename]) => {
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(link.href);
        })
        .catch(error => {
            console.error('Erro no download:', error);
            alert('Não foi possível baixar o arquivo. Nenhum arquivo foi encontrado para os IDs selecionados.');
        });
}

/**
 * Controla a checkbox "Selecionar Todos"
 */
function selecionarTodos() {
    const isChecked = selectAllCheckbox.checked;
    document.querySelectorAll('.orcamento-checkbox').forEach(checkbox => {
        if (!checkbox.disabled) {
            checkbox.checked = isChecked;
        }
    });
    atualizarEstadoDownload();
}

/**
 * Habilita/desabilita o botão de download se alguma checkbox estiver marcada
 */
function atualizarEstadoDownload() {
    const algumSelecionado = document.querySelectorAll('.orcamento-checkbox:checked').length > 0;
    if (baixarBtn) baixarBtn.disabled = !algumSelecionado;
}


// --- Funções Utilitárias ---
function limparFiltros() {
    Object.values(filtros).forEach(campo => { if (campo) campo.value = ''; });
    resultsTableBody.innerHTML = '';
    if (baixarBtn) baixarBtn.disabled = true;
    if (selectAllCheckbox) selectAllCheckbox.checked = false;
    hideResults();
    hideMessages();
}
function mostrarLoading(show) { if (loadingDiv) loadingDiv.style.display = show ? 'flex' : 'none'; }
function mostrarResultados() { if(resultsDiv) resultsDiv.style.display = 'block'; if(noResultsDiv) noResultsDiv.style.display = 'none'; }
function mostrarSemResultados() { if(resultsDiv) resultsDiv.style.display = 'none'; if(noResultsDiv) noResultsDiv.style.display = 'block'; }
function hideResults() { if(resultsDiv) resultsDiv.style.display = 'none'; if(noResultsDiv) noResultsDiv.style.display = 'none'; }
function showError(message) { if(errorDiv) { errorDiv.textContent = message; errorDiv.style.display = 'block'; } }
function hideMessages() { if(errorDiv) errorDiv.style.display = 'none'; }
function formatarMoeda(valor) { if (valor === null || valor === undefined) return 'R$ 0,00'; return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor); }
function formatarData(dataString) { if (!dataString) return '-'; const data = new Date(dataString + 'T00:00:00'); return data.toLocaleDateString('pt-BR'); }

