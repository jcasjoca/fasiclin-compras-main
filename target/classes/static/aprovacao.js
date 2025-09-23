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
// const statsDiv = document.getElementById('statsDiv'); // Removido se não existir

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
        
        // Removida a autenticação desnecessária
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

    // Ordenar orçamentos de cada produto por preço
    Object.values(orcamentosPorProduto).forEach(grupo => {
        grupo.orcamentos.sort((a, b) => a.precoCompra - b.precoCompra);
    });

    renderizarOrcamentos();
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
             ${produto.nome} (${produto.unidadeAbreviacao || 'N/A'})
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
                    <th>Preço Compra</th>
                    <th>Garantia</th>
                    <th class="date-column">Data Entrega</th>
                    <th>Cond. Pagamento</th>
                </tr>
            </thead>
            <tbody>
    `;

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
                <td>
                    <strong>${orcamento.nomeFornecedor || 'Não informado'}</strong>
                </td>
                <td class="currency">
                    ${formatarMoeda(orcamento.precoCompra)}
                </td>
                <td>
                    ${orcamento.garantia || '-'}
                </td>
                <td class="date-column">
                    ${formatarData(orcamento.dataEntrega)}
                </td>
                <td>
                    ${orcamento.condicoesPagamento || '-'}
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
 * ATUALIZADO: Gera as ordens de compra com base nas seleções
 */
async function gerarOrdensDeCompra() {
    if (loading) return;

    try {
        loading = true;
        gerarOcBtn.disabled = true;
        gerarOcBtn.textContent = 'Processando...';
        hideMessages();

        const orcamentoIds = Object.values(selecoes).map(id => parseInt(id));

        // CORREÇÃO: Chama o novo endpoint que apenas processa os dados
        const response = await fetch('/api/ordens-de-compra/processar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orcamentoIds)
        });

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status} - ${response.statusText}`);
        }

        // CORREÇÃO: Agora esperamos uma resposta JSON, não um arquivo
        const result = await response.json();
        showSuccess(result.message); // Exibe "Orçamentos processados com sucesso!"
        
        // Recarrega os orçamentos após o sucesso para os itens sumirem da tela
        setTimeout(() => {
            selecoes = {};
            carregarOrcamentos();
        }, 2000);

    } catch (error) {
        console.error('Erro ao processar ordens de compra:', error);
        showError('Erro ao processar ordens de compra: ' + error.message);
    } finally {
        loading = false;
        // O botão será atualizado quando a lista for recarregada
    }
}


// --- Funções Utilitárias (sem alterações necessárias) ---

function atualizarVisualizacaoSelecao(produtoId, orcamentoId) {
    document.querySelectorAll(`input[name="produto_${produtoId}"]`).forEach(radio => {
        radio.closest('tr').classList.remove('selected');
    });

    const radioSelecionado = document.querySelector(`input[name="produto_${produtoId}"][value="${orcamentoId}"]`);
    if (radioSelecionado) {
        radioSelecionado.closest('tr').classList.add('selected');
    }
}

function atualizarBotaoGerar() {
    const totalSelecionados = Object.keys(selecoes).length;
    
    gerarOcBtn.disabled = totalSelecionados === 0;
    
    if (totalSelecionados > 0) {
        gerarOcBtn.textContent = `Processar ${totalSelecionados} Seleção${totalSelecionados > 1 ? 'ões' : ''}`;
    } else {
        gerarOcBtn.textContent = 'Processar Seleções';
    }
}

function mostrarLoading(show) {
    if (loadingDiv) loadingDiv.style.display = show ? 'flex' : 'none';
}

function mostrarActionsDiv() {
    if (actionsDiv) actionsDiv.style.display = 'block';
    // if (statsDiv) statsDiv.style.display = 'flex';
}

function mostrarSemDados() {
    orcamentosContainer.innerHTML = `
        <div class="no-data">
            Não há orçamentos pendentes de aprovação no momento.
        </div>
    `;
    if (actionsDiv) actionsDiv.style.display = 'none';
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
    if (valor === null || valor === undefined) return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor);
}

function formatarData(dataString) {
    if (!dataString) return '-';
    const data = new Date(dataString + 'T00:00:00'); // Evita problemas de fuso
    return data.toLocaleDateString('pt-BR');
}
