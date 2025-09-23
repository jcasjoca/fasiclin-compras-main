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
    if(gerarOcBtn) {
        gerarOcBtn.addEventListener('click', function() {
            if (!gerarOcBtn.disabled) {
                gerarOrdensDeCompra();
            }
        });
    }
});

/**
 * Carrega os orçamentos pendentes de aprovação
 */
async function carregarOrcamentos() {
    try {
        mostrarLoading(true);
        hideMessages();
        
        const response = await fetch('/api/orcamentos/pendentes');

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
        orcamentosContainer.innerHTML = ''; // Limpa a tela em caso de erro
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

    orcamentosPorProduto = {};
    orcamentos.forEach(orcamento => {
        const produtoId = orcamento.idProduto;
        if (!orcamentosPorProduto[produtoId]) {
            orcamentosPorProduto[produtoId] = {
                produto: {
                    id: orcamento.idProduto,
                    nome: orcamento.nomeProduto,
                    descricao: orcamento.descricaoProduto,
                    unidadeAbreviacao: orcamento.unidadeAbreviacao
                },
                orcamentos: []
            };
        }
        orcamentosPorProduto[produtoId].orcamentos.push(orcamento);
    });

    Object.values(orcamentosPorProduto).forEach(grupo => {
        grupo.orcamentos.sort((a, b) => a.precoCompra - b.precoCompra);
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

    // CORREÇÃO: Cabeçalho da tabela atualizado com as novas colunas.
    let html = `
        <div class="produto-header">
            🛍️ ${produto.nome} (${produto.unidadeAbreviacao || ''})
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
                    <th class="date-column">Data de Entrega</th>
                    <th>Cond. Pagamento</th>
                    <th>Garantia</th>
                    <th>Quantidade</th>
                    <th>Valor Total</th>
                </tr>
            </thead>
            <tbody>
    `;

    // CORREÇÃO: Linhas da tabela atualizadas para corresponder aos novos cabeçalhos.
    orcamentos.forEach((orcamento) => {
        const isChecked = selecoes[produto.id] === orcamento.idOrcamento.toString();
        const rowClass = isChecked ? 'selected' : '';
        
        html += `
            <tr class="${rowClass}" data-orcamento-id="${orcamento.idOrcamento}">
                <td class="radio-column">
                    <input type="radio" 
                           name="produto_${produto.id}" 
                           value="${orcamento.idOrcamento}"
                           ${isChecked ? 'checked' : ''}>
                </td>
                <td><strong>${orcamento.nomeFornecedor || '-'}</strong></td>
                <td class="currency">R$ ${formatarMoeda(orcamento.precoCompra)}</td>
                <td class="date-column">${formatarData(orcamento.dataEntrega)}</td>
                <td>${orcamento.condicoesPagamento || '-'}</td>
                <td>${orcamento.garantia || '-'}</td>
                <td>${orcamento.quantidade} ${orcamento.unidadeAbreviacao || ''}</td>
                <td class="currency">R$ ${formatarMoeda(orcamento.valorTotal)}</td>
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
    document.querySelectorAll(`input[name="produto_${produtoId}"]`).forEach(radio => {
        radio.closest('tr').classList.remove('selected');
    });

    const radioSelecionado = document.querySelector(`input[name="produto_${produtoId}"][value="${orcamentoId}"]`);
    if (radioSelecionado) {
        radioSelecionado.closest('tr').classList.add('selected');
    }
}

/**
 * Atualiza o estado do botão de gerar ordens
 */
function atualizarBotaoGerar() {
    const totalProdutos = Object.keys(orcamentosPorProduto).length;
    const totalSelecionados = Object.keys(selecoes).length;
    
    if(gerarOcBtn) {
        gerarOcBtn.disabled = totalSelecionados < totalProdutos;
        if (totalSelecionados === totalProdutos && totalSelecionados > 0) {
            gerarOcBtn.textContent = `Aprovar ${totalSelecionados} Orçamento${totalSelecionados > 1 ? 's' : ''}`;
        } else {
            gerarOcBtn.textContent = 'Aprovar Orçamentos';
        }
    }
}

/**
 * Atualiza as estatísticas do painel
 */
function atualizarEstatisticas() {
    if(!statsDiv) return;
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
    if (loading || !gerarOcBtn) return;

    try {
        loading = true;
        gerarOcBtn.disabled = true;
        gerarOcBtn.textContent = 'Processando...';
        hideMessages();

        const orcamentoIds = Object.values(selecoes).map(id => parseInt(id));

        const response = await fetch('/api/ordens-de-compra/processar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(orcamentoIds)
        });

        if (!response.ok) {
             const errorData = await response.json().catch(() => ({ message: `Erro HTTP: ${response.status}` }));
             throw new Error(errorData.message);
        }
        
        showSuccess('Orçamentos aprovados com sucesso! A página será atualizada.');
        
        setTimeout(() => {
            selecoes = {};
            carregarOrcamentos();
        }, 2000);

    } catch (error) {
        console.error('Erro ao processar orçamentos:', error);
        showError('Erro ao processar orçamentos: ' + error.message);
    } finally {
        loading = false;
        atualizarBotaoGerar();
    }
}

/**
 * Utilitários de interface
 */
function mostrarLoading(show) {
    if(loadingDiv) loadingDiv.style.display = show ? 'flex' : 'none';
}

function mostrarActionsDiv() {
    if(actionsDiv) actionsDiv.style.display = 'block';
    if(statsDiv) statsDiv.style.display = 'flex';
}

function mostrarSemDados() {
    orcamentosContainer.innerHTML = `
        <div class="no-data">
            📝 Não há orçamentos pendentes de aprovação no momento.
        </div>
    `;
    if(actionsDiv) actionsDiv.style.display = 'none';
    if(statsDiv) statsDiv.style.display = 'none';
}

function showError(message) {
    if(errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    }
    if(successDiv) successDiv.style.display = 'none';
}

function showSuccess(message) {
    if(successDiv) {
        successDiv.textContent = message;
        successDiv.style.display = 'block';
    }
    if(errorDiv) errorDiv.style.display = 'none';
}

function hideMessages() {
    if(errorDiv) errorDiv.style.display = 'none';
    if(successDiv) successDiv.style.display = 'none';
}

function formatarMoeda(valor) {
    if (valor === null || valor === undefined) return '0,00';
    return new Intl.NumberFormat('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(valor);
}

function formatarData(dataString) {
    if (!dataString) return '-';
    const data = new Date(dataString + 'T00:00:00');
    // CORREÇÃO: Removido o "Date" duplicado.
    return data.toLocaleDateString('pt-BR');
}

