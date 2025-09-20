// Estado da aplicação
let orcamentosPorProduto = {};
let selecoes = {};
let loading = false;

// Elementos do DOM
const loadingDiv = document.getElementById('loadingDiv');
const errorDiv = document.getElementById('errorDiv');
const successDiv = document.getElementById('successDiv');
const orcamentosContainer = document.getElementById('orcamentosContainer');
const actionsDiv = document.getElementById('actionsDiv');
const gerarOcBtn = document.getElementById('gerarOcBtn');
const statsDiv = document.getElementById('statsDiv');

// Carregamento inicial
document.addEventListener('DOMContentLoaded', function() {
    carregarOrcamentos();
});

// Configuração do botão de gerar ordens
gerarOcBtn.addEventListener('click', function() {
    if (!gerarOcBtn.disabled) {
        gerarOrdensDeCompra();
    }
});

/**
 * Carrega os orçamentos pendentes de aprovação
 */
async function carregarOrcamentos() {
    try {
        mostrarLoading(true);
        hideMessages();
        
        const response = await fetch('/api/orcamentos/pendentes', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Basic ' + btoa('admin:admin')
            }
        });

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status} - ${response.statusText}`);
        }

        const orcamentos = await response.json();
        
        if (!Array.isArray(orcamentos)) {
            throw new Error('Formato de resposta inválido');
        }

        processarOrcamentos(orcamentos);
        
    } catch (error) {
        console.error('Erro ao carregar orçamentos:', error);
        showError('Erro ao carregar orçamentos: ' + error.message);
    } finally {
        mostrarLoading(false);
    }
}

/**
 * Processa e agrupa os orçamentos por produto
 */
function processarOrcamentos(orcamentos) {
    if (!orcamentos || orcamentos.length === 0) {
        mostrarSemDados();
        return;
    }

    // Agrupar por produto
    orcamentosPorProduto = {};
    orcamentos.forEach(orcamento => {
        const produtoId = orcamento.produto.id;
        if (!orcamentosPorProduto[produtoId]) {
            orcamentosPorProduto[produtoId] = {
                produto: orcamento.produto,
                orcamentos: []
            };
        }
        orcamentosPorProduto[produtoId].orcamentos.push(orcamento);
    });

    // Ordenar orçamentos de cada produto por preço
    Object.values(orcamentosPorProduto).forEach(grupo => {
        grupo.orcamentos.sort((a, b) => a.valorUnitario - b.valorUnitario);
    });

    renderizarOrcamentos();
    atualizarEstatisticas();
    mostrarActionsDiv();
}

/**
 * Renderiza os orçamentos agrupados por produto
 */
function renderizarOrcamentos() {
    orcamentosContainer.innerHTML = '';

    Object.entries(orcamentosPorProduto).forEach(([produtoId, grupo]) => {
        const produtoDiv = document.createElement('div');
        produtoDiv.className = 'produto-group';
        produtoDiv.innerHTML = criarHtmlProduto(grupo);
        orcamentosContainer.appendChild(produtoDiv);
    });

    // Adicionar event listeners para os radio buttons
    document.querySelectorAll('input[type="radio"]').forEach(radio => {
        radio.addEventListener('change', function() {
            const produtoId = this.name.replace('produto_', '');
            const orcamentoId = this.value;
            selecoes[produtoId] = orcamentoId;
            
            atualizarVisualizacaoSelecao(produtoId, orcamentoId);
            atualizarBotaoGerar();
            atualizarEstatisticas();
        });
    });
}

/**
 * Cria o HTML para um grupo de produto
 */
function criarHtmlProduto(grupo) {
    const produto = grupo.produto;
    const orcamentos = grupo.orcamentos;

    let html = `
        <div class="produto-header">
            🛍️ ${produto.nome} (${produto.unimedida.sigla})
        </div>
        <div class="produto-info">
            <strong>Descrição:</strong> ${produto.descricao || 'Não informada'}<br>
            <strong>Quantidade de Cotações:</strong> ${orcamentos.length}
        </div>
        <table class="cotacoes-table">
            <thead>
                <tr>
                    <th class="radio-column">Selecionar</th>
                    <th>Fornecedor</th>
                    <th>Valor Unitário</th>
                    <th>Valor Total</th>
                    <th class="date-column">Data da Cotação</th>
                    <th>Observações</th>
                </tr>
            </thead>
            <tbody>
    `;

    orcamentos.forEach((orcamento, index) => {
        const isChecked = selecoes[produto.id] === orcamento.id.toString();
        const rowClass = isChecked ? 'selected' : '';
        
        html += `
            <tr class="${rowClass}" data-orcamento-id="${orcamento.id}">
                <td class="radio-column">
                    <input type="radio" 
                           name="produto_${produto.id}" 
                           value="${orcamento.id}"
                           ${isChecked ? 'checked' : ''}>
                </td>
                <td>
                    <strong>${orcamento.fornecedor.nomeFantasia}</strong><br>
                    <small>${orcamento.fornecedor.razaoSocial}</small>
                </td>
                <td class="currency">
                    R$ ${formatarMoeda(orcamento.valorUnitario)}
                </td>
                <td class="currency">
                    R$ ${formatarMoeda(orcamento.valorTotal)}
                </td>
                <td class="date-column">
                    ${formatarData(orcamento.dataCotacao)}
                </td>
                <td>
                    ${orcamento.observacoes || '-'}
                </td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
    `;

    return html;
}

/**
 * Atualiza a visualização da linha selecionada
 */
function atualizarVisualizacaoSelecao(produtoId, orcamentoId) {
    // Remove seleção anterior
    document.querySelectorAll(`input[name="produto_${produtoId}"]`).forEach(radio => {
        const row = radio.closest('tr');
        row.classList.remove('selected');
    });

    // Adiciona seleção atual
    const radioSelecionado = document.querySelector(`input[name="produto_${produtoId}"][value="${orcamentoId}"]`);
    if (radioSelecionado) {
        const row = radioSelecionado.closest('tr');
        row.classList.add('selected');
    }
}

/**
 * Atualiza o estado do botão de gerar ordens
 */
function atualizarBotaoGerar() {
    const totalProdutos = Object.keys(orcamentosPorProduto).length;
    const totalSelecionados = Object.keys(selecoes).length;
    
    gerarOcBtn.disabled = totalSelecionados < totalProdutos;
    
    if (totalSelecionados === totalProdutos) {
        gerarOcBtn.textContent = `Gerar ${totalSelecionados} Ordem${totalSelecionados > 1 ? 's' : ''} de Compra`;
    } else {
        gerarOcBtn.textContent = 'Gerar Ordens de Compra';
    }
}

/**
 * Atualiza as estatísticas do painel
 */
function atualizarEstatisticas() {
    const totalProdutos = Object.keys(orcamentosPorProduto).length;
    const totalCotacoes = Object.values(orcamentosPorProduto)
        .reduce((sum, grupo) => sum + grupo.orcamentos.length, 0);
    const totalSelecionados = Object.keys(selecoes).length;

    document.getElementById('statProdutos').textContent = totalProdutos;
    document.getElementById('statCotacoes').textContent = totalCotacoes;
    document.getElementById('statSelecionados').textContent = totalSelecionados;
}

/**
 * Gera as ordens de compra com base nas seleções
 */
async function gerarOrdensDeCompra() {
    if (loading) return;

    try {
        loading = true;
        gerarOcBtn.disabled = true;
        gerarOcBtn.textContent = 'Gerando Ordens...';
        hideMessages();

        const orcamentoIds = Object.values(selecoes).map(id => parseInt(id));

        const response = await fetch('/api/ordens-de-compra/gerar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Basic ' + btoa('admin:admin')
            },
            body: JSON.stringify(orcamentoIds)
        });

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status} - ${response.statusText}`);
        }

        // Verificar se a resposta é um arquivo ZIP
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/zip')) {
            // Download do arquivo ZIP
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'ordens-de-compra.zip';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);

            showSuccess('Ordens de compra geradas com sucesso! O download foi iniciado automaticamente.');
            
            // Recarregar os orçamentos após sucesso
            setTimeout(() => {
                carregarOrcamentos();
                selecoes = {};
            }, 2000);
        } else {
            const result = await response.json();
            showSuccess('Ordens de compra geradas: ' + JSON.stringify(result));
        }

    } catch (error) {
        console.error('Erro ao gerar ordens de compra:', error);
        showError('Erro ao gerar ordens de compra: ' + error.message);
    } finally {
        loading = false;
        atualizarBotaoGerar();
    }
}

/**
 * Utilitários de interface
 */
function mostrarLoading(show) {
    loadingDiv.style.display = show ? 'block' : 'none';
}

function mostrarActionsDiv() {
    actionsDiv.style.display = 'block';
    statsDiv.style.display = 'flex';
}

function mostrarSemDados() {
    orcamentosContainer.innerHTML = `
        <div class="no-data">
            📝 Não há orçamentos pendentes de aprovação no momento.
        </div>
    `;
}

function showError(message) {
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    successDiv.style.display = 'none';
}

function showSuccess(message) {
    successDiv.textContent = message;
    successDiv.style.display = 'block';
    errorDiv.style.display = 'none';
}

function hideMessages() {
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';
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