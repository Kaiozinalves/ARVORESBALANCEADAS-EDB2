package trees;

import java.util.List;

/**
 * Interface comum para Árvores de Busca Binária Balanceadas.
 * Define o contrato de operações fundamentais e de coleta de métricas
 * para comparação justa entre a Árvore AVL e a Árvore Rubro-Negra
 * no contexto do sistema de logística da Amazon (Black Friday).
 */
public interface BalancedTree {

    /**
     * Insere um novo identificador de encomenda na árvore.
     * Caso a chave já exista, a inserção é rejeitada (sem duplicatas)
     * e a árvore permanece inalterada.
     *
     * @param key Identificador numérico da encomenda.
     * @return true se o elemento foi inserido; false se já existia na árvore.
     */
    boolean insert(int key);

    /**
     * Remove um identificador de encomenda da árvore, caso exista,
     * restaurando as propriedades de balanceamento da estrutura.
     *
     * @param key Identificador numérico da encomenda a ser removido.
     * @return true se o elemento foi encontrado e removido; false se não existia.
     */
    boolean remove(int key);

    /**
     * Busca um identificador de encomenda na árvore.
     *
     * @param key Identificador numérico procurado.
     * @return true se o elemento está presente na árvore; false caso contrário.
     */
    boolean search(int key);

    /**
     * Retorna uma lista com todos os elementos da árvore em percurso em-ordem (in-order),
     * apresentando os identificadores em ordem estritamente crescente.
     *
     * @return Lista de chaves em ordem crescente.
     */
    List<Integer> inOrder();

    /**
     * Exibe no console os elementos da árvore em ordem crescente.
     */
    void printInOrder();

    /**
     * Imprime no console a representação visual/hierárquica da estrutura da árvore.
     * Na AVL, exibe a altura e o Fator de Balanceamento (FB) de cada nó.
     * Na Rubro-Negra, exibe a cor ([V] Vermelho ou [P] Preto) de cada nó.
     */
    void printTree();

    /**
     * Retorna a altura atual da árvore.
     * Convenção adotada:
     * - Árvore vazia: altura = -1 (ou 0 em contagem de níveis; aqui usamos altura em arestas/níveis documentada)
     * - Árvore com apenas a raiz (folha): altura = 0 (ou 1 nó).
     * Padronizamos: árvore vazia = 0, árvore com 1 nó (raiz) = 1,
     * para facilitar a comparação direta de níveis entre AVL e Rubro-Negra.
     *
     * @return Altura da árvore em número de níveis (0 se vazia).
     */
    int getHeight();

    /**
     * Retorna a quantidade total de elementos (nós válidos) presentes na árvore.
     *
     * @return Número de elementos na árvore.
     */
    int getSize();

    /**
     * Verifica se a árvore está vazia.
     *
     * @return true se não houver elementos; false caso contrário.
     */
    boolean isEmpty();

    /**
     * Remove todos os elementos da árvore e reinicia as métricas.
     */
    void clear();

    /**
     * Verifica recursivamente se todas as propriedades de Árvore Binária de Busca (ABB)
     * e as invariantes específicas de balanceamento (AVL ou Rubro-Negra) estão íntegras.
     *
     * @return true se a árvore é 100% válida; false se alguma propriedade foi violada.
     */
    boolean validateProperties();

    /**
     * @return Total de rotações simples à esquerda realizadas.
     */
    long getLeftRotationCount();

    /**
     * @return Total de rotações simples à direita realizadas.
     */
    long getRightRotationCount();

    /**
     * @return Soma total de rotações simples (esquerda + direita) realizadas.
     *         Nota: uma rotação dupla (LR ou RL) equivale a 2 rotações simples.
     */
    default long getTotalRotationCount() {
        return getLeftRotationCount() + getRightRotationCount();
    }

    /**
     * @return Quantidade de ajustes de balanceamento realizados
     *         (ex.: recolorações na Rubro-Negra ou intervenções de rebalanceamento na AVL).
     */
    long getBalancingAdjustmentCount();

    /**
     * Reinicia apenas os contadores de rotações e ajustes para uma nova medição.
     */
    void resetMetrics();

    /**
     * @return Nome descritivo da estrutura (ex.: "Árvore AVL" ou "Árvore Rubro-Negra").
     */
    String getTreeName();
}
