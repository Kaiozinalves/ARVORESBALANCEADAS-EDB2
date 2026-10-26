package trees;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação completa da Árvore AVL (Adelson-Velsky e Landis).
 *
 * Propriedade de Balanceamento AVL:
 * Para qualquer nó N da árvore, a diferença entre as alturas de suas subárvores
 * esquerda e direita — chamada de Fator de Balanceamento (FB) — deve estar
 * no conjunto {-1, 0, +1}:
 *     FB(N) = altura(N.left) - altura(N.right), com |FB(N)| <= 1.
 *
 * Referências Bibliográficas:
 * - ADELSON-VELSKY, G.; LANDIS, E. M. An algorithm for the organization of information.
 *   Soviet Mathematics Doklady, v. 3, p. 1259-1263, 1962.
 * - WEISS, Mark Allen. Data Structures and Algorithm Analysis in Java. 3rd ed.
 *   Boston: Pearson, 2012. Cap. 4 (Seção 4.4 - AVL Trees, p. 122-135).
 * - GOODRICH, Michael T.; TAMASSIA, Roberto. Estruturas de Dados e Algoritmos em Java.
 *   5ª ed. Porto Alegre: Bookman, 2013. Cap. 10 (Seção 10.2 - Árvores AVL).
 */
public class AVLTree implements BalancedTree {

    /** Raiz da árvore AVL. */
    private AVLNode root;

    /** Quantidade total de elementos armazenados na árvore. */
    private int size;

    /** Contador de rotações simples à esquerda realizadas. */
    private long leftRotationCount;

    /** Contador de rotações simples à direita realizadas. */
    private long rightRotationCount;

    /** Contador de intervenções de rebalanceamento (quantas vezes |FB| > 1 foi corrigido). */
    private long balancingAdjustmentCount;

    /** Flag auxiliar para indicar se a última operação recursiva de insert/remove alterou a árvore. */
    private boolean operationSuccess;

    /**
     * Constrói uma Árvore AVL inicialmente vazia.
     */
    public AVLTree() {
        this.root = null;
        this.size = 0;
        this.leftRotationCount = 0;
        this.rightRotationCount = 0;
        this.balancingAdjustmentCount = 0;
    }

    // =========================================================================
    // 1. MÉTODOS AUXILIARES DE ALTURA E FATOR DE BALANCEAMENTO
    // =========================================================================

    /**
     * Retorna a altura de um nó, tratando referências nulas como altura 0.
     *
     * @param node Nó consultado.
     * @return Altura do nó (0 se null).
     */
    private int height(AVLNode node) {
        return (node == null) ? 0 : node.height;
    }

    /**
     * Recalcula e atualiza a altura de um nó com base na altura máxima de seus filhos.
     * Complexidade: O(1).
     *
     * @param node Nó cuja altura será atualizada.
     */
    private void updateHeight(AVLNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    /**
     * Calcula o Fator de Balanceamento (FB) de um nó.
     * Definição adotada: FB(N) = altura(esquerda) - altura(direita).
     * - FB > 0: subárvore esquerda mais alta.
     * - FB < 0: subárvore direita mais alta.
     * - FB == 0: subárvores com a mesma altura.
     *
     * @param node Nó avaliado.
     * @return Fator de balanceamento do nó.
     */
    public int getBalanceFactor(AVLNode node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    // =========================================================================
    // 2. OPERAÇÕES DE ROTAÇÃO (RESTAURAÇÃO DO BALANCEAMENTO)
    // =========================================================================

    /**
     * Rotação Simples à Direita (Right Rotation - Caso Esquerda-Esquerda / LL).
     * Aplicada quando um nó 'y' fica desbalanceado para a esquerda (FB = +2)
     * e seu filho esquerdo 'x' possui FB >= 0.
     *
     *        y (FB=+2)               x
     *       / \                     / \
     *      x   T3     ====>        z   y
     *     / \                         / \
     *    z   T2                      T2  T3
     *
     * Complexidade: O(1).
     *
     * @param y Raiz da subárvore desbalanceada.
     * @return Nova raiz 'x' da subárvore após a rotação.
     */
    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        // Realiza a rotação dos ponteiros
        x.right = y;
        y.left = T2;

        // Atualiza as alturas de baixo para cima (primeiro 'y', depois 'x')
        updateHeight(y);
        updateHeight(x);

        rightRotationCount++;
        return x;
    }

    /**
     * Rotação Simples à Esquerda (Left Rotation - Caso Direita-Direita / RR).
     * Aplicada quando um nó 'x' fica desbalanceado para a direita (FB = -2)
     * e seu filho direito 'y' possui FB <= 0.
     *
     *      x (FB=-2)                   y
     *     / \                         / \
     *    T1  y        ====>          x   z
     *       / \                     / \
     *      T2  z                   T1  T2
     *
     * Complexidade: O(1).
     *
     * @param x Raiz da subárvore desbalanceada.
     * @return Nova raiz 'y' da subárvore após a rotação.
     */
    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        // Realiza a rotação dos ponteiros
        y.left = x;
        x.right = T2;

        // Atualiza as alturas de baixo para cima (primeiro 'x', depois 'y')
        updateHeight(x);
        updateHeight(y);

        leftRotationCount++;
        return y;
    }

    /**
     * Verifica o Fator de Balanceamento de um nó e, caso esteja fora do intervalo [-1, +1],
     * aplica uma das 4 rotações clássicas da AVL (LL, LR, RR ou RL).
     * Seguindo Weiss (2012, Fig. 4.42), este método unifica o rebalanceamento
     * tanto para a inserção quanto para a remoção.
     *
     * @param node Nó atual no retorno da recursão.
     * @return Raiz da subárvore (potencialmente rotacionada) devidamente balanceada.
     */
    private AVLNode rebalance(AVLNode node) {
        updateHeight(node);
        int bf = getBalanceFactor(node);

        // Desbalanceamento para a ESQUERDA (FB = +2)
        if (bf > 1) {
            balancingAdjustmentCount++;
            // Caso 1: Esquerda-Esquerda (LL) -> Rotação Simples à Direita
            if (getBalanceFactor(node.left) >= 0) {
                return rotateRight(node);
            }
            // Caso 2: Esquerda-Direita (LR) -> Rotação Dupla (Esquerda no filho + Direita no nó)
            else {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
        }

        // Desbalanceamento para a DIREITA (FB = -2)
        if (bf < -1) {
            balancingAdjustmentCount++;
            // Caso 3: Direita-Direita (RR) -> Rotação Simples à Esquerda
            if (getBalanceFactor(node.right) <= 0) {
                return rotateLeft(node);
            }
            // Caso 4: Direita-Esquerda (RL) -> Rotação Dupla (Direita no filho + Esquerda no nó)
            else {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
        }

        return node;
    }

    // =========================================================================
    // 3. INSERÇÃO, BUSCA E REMOÇÃO
    // =========================================================================

    @Override
    public boolean insert(int key) {
        operationSuccess = false;
        root = insertRecursive(root, key);
        if (operationSuccess) {
            size++;
        }
        return operationSuccess;
    }

    /**
     * Insere recursivamente a chave na árvore AVL e rebalanceia os ancestrais no retorno.
     * Decisão de projeto: chaves duplicadas são ignoradas (operationSuccess permanece false).
     */
    private AVLNode insertRecursive(AVLNode current, int key) {
        if (current == null) {
            operationSuccess = true;
            return new AVLNode(key);
        }

        if (key < current.key) {
            current.left = insertRecursive(current.left, key);
        } else if (key > current.key) {
            current.right = insertRecursive(current.right, key);
        } else {
            // Chave duplicada: não insere e não altera a estrutura
            operationSuccess = false;
            return current;
        }

        // No retorno da recursão, atualiza altura e rebalanceia se necessário
        return rebalance(current);
    }

    @Override
    public boolean search(int key) {
        AVLNode current = root;
        while (current != null) {
            if (key == current.key) {
                return true;
            } else if (key < current.key) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return false;
    }

    @Override
    public boolean remove(int key) {
        operationSuccess = false;
        root = removeRecursive(root, key);
        if (operationSuccess) {
            size--;
        }
        return operationSuccess;
    }

    /**
     * Remove recursivamente uma chave da árvore AVL e restaura o balanceamento
     * de todos os nós ancestrais no retorno da recursão.
     */
    private AVLNode removeRecursive(AVLNode current, int key) {
        if (current == null) {
            // Elemento não encontrado
            return null;
        }

        if (key < current.key) {
            current.left = removeRecursive(current.left, key);
        } else if (key > current.key) {
            current.right = removeRecursive(current.right, key);
        } else {
            // Nó encontrado!
            operationSuccess = true;

            // Caso 1 e 2: Nó folha (sem filhos) ou com apenas 1 filho
            if (current.left == null || current.right == null) {
                AVLNode child = (current.left != null) ? current.left : current.right;
                return child; // Retorna null se for folha, ou o único filho existente
            }

            // Caso 3: Nó com 2 filhos
            // Substitui a chave pelo sucessor em-ordem (menor chave da subárvore direita)
            AVLNode successor = findMin(current.right);
            current.key = successor.key;
            // Remove o sucessor duplicado da subárvore direita
            current.right = removeRecursive(current.right, successor.key);
        }

        // Rebalanceia o nó atual na volta da recursão
        return rebalance(current);
    }

    /**
     * Localiza o nó de menor chave em uma subárvore (mais à esquerda).
     */
    private AVLNode findMin(AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    // =========================================================================
    // 4. PERCURSO EM ORDEM E VISUALIZAÇÃO DA ÁRVORE
    // =========================================================================

    @Override
    public List<Integer> inOrder() {
        List<Integer> result = new ArrayList<>(size);
        inOrderRecursive(root, result);
        return result;
    }

    private void inOrderRecursive(AVLNode node, List<Integer> result) {
        if (node != null) {
            inOrderRecursive(node.left, result);
            result.add(node.key);
            inOrderRecursive(node.right, result);
        }
    }

    @Override
    public void printInOrder() {
        List<Integer> elements = inOrder();
        if (elements.isEmpty()) {
            System.out.println("  [Árvore AVL vazia]");
        } else {
            System.out.println("  Percurso em ordem (crescente): " + elements);
        }
    }

    @Override
    public void printTree() {
        if (root == null) {
            System.out.println("  [Árvore AVL vazia]");
            return;
        }
        System.out.println("  Estrutura da Árvore AVL (formato: Chave [h=Altura, FB=Fator de Balanceamento]):");
        printTreeRecursive(root, "  ", true, "RAIZ");
    }

    private void printTreeRecursive(AVLNode node, String prefix, boolean isTail, String role) {
        if (node == null) {
            return;
        }
        int bf = getBalanceFactor(node);
        String bfStr = (bf > 0) ? "+" + bf : String.valueOf(bf);
        System.out.println(prefix + (isTail ? "└── " : "├── ")
                + "[" + role + "] " + node.key + " (h=" + node.height + ", FB=" + bfStr + ")");

        String childPrefix = prefix + (isTail ? "    " : "│   ");
        if (node.left != null || node.right != null) {
            if (node.left != null && node.right != null) {
                printTreeRecursive(node.left, childPrefix, false, "E");
                printTreeRecursive(node.right, childPrefix, true, "D");
            } else if (node.left != null) {
                printTreeRecursive(node.left, childPrefix, true, "E");
            } else {
                printTreeRecursive(node.right, childPrefix, true, "D");
            }
        }
    }

    // =========================================================================
    // 5. VALIDAÇÃO DAS PROPRIEDADES DA ÁRVORE AVL
    // =========================================================================

    @Override
    public boolean validateProperties() {
        return validateAVLRecursive(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Verifica recursivamente:
     * 1. Propriedade de Árvore Binária de Busca (min < node.key < max);
     * 2. Integridade do campo height (height == 1 + max(h(left), h(right)));
     * 3. Propriedade de balanceamento AVL (|FB| <= 1).
     */
    private boolean validateAVLRecursive(AVLNode node, long min, long max) {
        if (node == null) {
            return true;
        }
        // 1. Verifica ordenação de busca binária
        if (node.key <= min || node.key >= max) {
            return false;
        }
        // 2. Verifica se a altura armazenada está correta
        int expectedHeight = 1 + Math.max(height(node.left), height(node.right));
        if (node.height != expectedHeight) {
            return false;
        }
        // 3. Verifica se o Fator de Balanceamento está em {-1, 0, +1}
        int bf = getBalanceFactor(node);
        if (Math.abs(bf) > 1) {
            return false;
        }
        return validateAVLRecursive(node.left, min, node.key)
                && validateAVLRecursive(node.right, node.key, max);
    }

    // =========================================================================
    // 6. GETTERS DE MÉTRICAS E ESTADO
    // =========================================================================

    @Override
    public int getHeight() {
        return height(root);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
        resetMetrics();
    }

    @Override
    public long getLeftRotationCount() {
        return leftRotationCount;
    }

    @Override
    public long getRightRotationCount() {
        return rightRotationCount;
    }

    @Override
    public long getBalancingAdjustmentCount() {
        return balancingAdjustmentCount;
    }

    @Override
    public void resetMetrics() {
        this.leftRotationCount = 0;
        this.rightRotationCount = 0;
        this.balancingAdjustmentCount = 0;
    }

    @Override
    public String getTreeName() {
        return "Árvore AVL";
    }
}
