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
        // CORREÇÃO: Usar o ID do produto da estrutura plana do DTO
        const produtoId = orcamento.idProduto; 
        if (!orcamentosPorProduto[produtoId]) {
            orcamentosPorProduto[produtoId] = {
                // CORREÇÃO: Recriar um objeto 'produto' com os dados do DTO
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
        // CORREÇÃO: O campo de preço no DTO é 'precoCompra'
        grupo.orcamentos.sort((a, b) => a.precoCompra - b.precoCompra);
    });

    renderizarOrcamentos();
    // Funções de estatísticas podem precisar de ajuste se os elementos HTML não existirem
    // atualizarEstatisticas(); 
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
            // atualizarEstatisticas();
        });
    });
}

/**
 * Cria o HTML para um grupo de produto
 */
function criarHtmlProduto(grupo) {
    const produto = grupo.produto;
    const orcamentos = grupo.orcamentos;

    // CORREÇÃO: Várias referências foram ajustadas para ler a estrutura plana do DTO
    let html = `
        <div class="produto-header">
            🛍️ ${produto.nome} (${produto.unidadeAbreviacao || 'N/A'})
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
                    R$ ${formatarMoeda(orcamento.precoCompra)}
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
    
    // Habilita o botão se pelo menos uma seleção for feita
    gerarOcBtn.disabled = totalSelecionados === 0;
    
    if (totalSelecionados > 0) {
        gerarOcBtn.textContent = `Gerar ${totalSelecionados} Ordem${totalSelecionados > 1 ? 'ns' : ''} de Compra`;
    } else {
        gerarOcBtn.textContent = 'Gerar Ordens de Compra';
    }
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
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orcamentoIds)
        });

        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status} - ${response.statusText}`);
        }
        
        const blob = await response.blob();
        const header = response.headers.get('Content-Disposition');
        const parts = header.split(';');
        const filename = parts[1].split('=')[1].replace(/"/g, '');

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename || 'ordens-de-compra.zip';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        showSuccess('Ordens de compra geradas com sucesso! O download foi iniciado automaticamente.');
        
        // Recarregar os orçamentos após sucesso
        setTimeout(() => {
            selecoes = {};
            carregarOrcamentos();
        }, 2000);

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
    loadingDiv.style.display = show ? 'flex' : 'none'; // 'flex' para centralizar
}

function mostrarActionsDiv() {
    actionsDiv.style.display = 'block';
    // statsDiv.style.display = 'flex'; // Comentei caso não exista
}

function mostrarSemDados() {
    orcamentosContainer.innerHTML = `
        <div class="no-data">
            📝 Não há orçamentos pendentes de aprovação no momento.
        </div>
    `;
    actionsDiv.style.display = 'none';
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
    if (valor === null || valor === undefined) return '0,00';
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor);
}

function formatarData(dataString) {
    if (!dataString) return '-';
    // Adiciona T00:00:00 para evitar problemas de fuso horário
    const data = new Date(dataString + 'T00:00:00');
    return data.toLocaleDateString('pt-BR');
}