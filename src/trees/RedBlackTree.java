package trees;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação completa da Árvore Rubro-Negra (Red-Black Tree)
 * seguindo fielmente os algoritmos de Cormen et al. (CLRS, Capítulo 13).
 *
 * As 5 Propriedades Rubro-Negras (CLRS, Seção 13.1):
 * 1. Todo nó é vermelho (RED) ou preto (BLACK).
 * 2. A raiz é preta.
 * 3. Toda folha (sentinela T.nil) é preta.
 * 4. Se um nó é vermelho, então ambos os seus filhos são pretos (não há dois vermelhos seguidos).
 * 5. Para cada nó, todos os caminhos simples do nó até folhas descendentes
 *    contêm o mesmo número de nós pretos (altura negra / black-height).
 *
 * Referência Bibliográfica:
 * - CORMEN, Thomas H.; LEISERSON, Charles E.; RIVEST, Ronald L.; STEIN, Clifford.
 *   Algoritmos: Teoria e Prática (Introduction to Algorithms). 3ª ed.
 *   Rio de Janeiro: Elsevier, 2012. Capítulo 13 (Árvores Rubro-Negras), Seções 13.1 a 13.4.
 */
public class RedBlackTree implements BalancedTree {

    /**
     * Nó sentinela T.nil (CLRS, Seção 13.1).
     * Representa todas as folhas externas (substituindo null) e o pai da raiz.
     * Sua cor é permanentemente BLACK.
     */
    private final RBNode NIL;

    /** Raiz da Árvore Rubro-Negra. Inicialmente aponta para NIL. */
    private RBNode root;

    /** Quantidade total de elementos (nós internos reais) na árvore. */
    private int size;

    /** Contador de rotações simples à esquerda (LEFT-ROTATE). */
    private long leftRotationCount;

    /** Contador de rotações simples à direita (RIGHT-ROTATE). */
    private long rightRotationCount;

    /** Contador de ajustes de balanceamento (etapas de fixup / recolorações na inserção e remoção). */
    private long balancingAdjustmentCount;

    /** Códigos ANSI para destacar nós vermelhos no terminal (mantendo também o rótulo textual). */
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    /**
     * Constrói uma Árvore Rubro-Negra vazia inicializando o sentinela T.nil.
     */
    public RedBlackTree() {
        this.NIL = new RBNode(0, Color.BLACK);
        this.NIL.left = this.NIL;
        this.NIL.right = this.NIL;
        this.NIL.parent = this.NIL;

        this.root = this.NIL;
        this.size = 0;
        this.leftRotationCount = 0;
        this.rightRotationCount = 0;
        this.balancingAdjustmentCount = 0;
    }

    // =========================================================================
    // 1. ROTAÇÕES (CLRS, SEÇÃO 13.2 - ROTATIONS)
    // =========================================================================

    /**
     * Procedimento LEFT-ROTATE(T, x) conforme Cormen et al. (Seção 13.2).
     * Supõe que x.right != T.nil. Pivoteia ao redor da ligação de x para y = x.right.
     *
     *       |                       |
     *       x                       y
     *      / \     LEFT-ROTATE     / \
     *     a   y    ===========>   x   g
     *        / \                 / \
     *       b   g               a   b
     *
     * Complexidade: O(1).
     *
     * @param x Nó em torno do qual a rotação à esquerda será executada.
     */
    private void leftRotate(RBNode x) {
        RBNode y = x.right;
        x.right = y.left;

        if (y.left != NIL) {
            y.left.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == NIL) {
            this.root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.left = x;
        x.parent = y;

        leftRotationCount++;
    }

    /**
     * Procedimento RIGHT-ROTATE(T, y) simétrico ao LEFT-ROTATE (CLRS, Seção 13.2).
     * Supõe que y.left != T.nil. Pivoteia ao redor da ligação de y para x = y.left.
     *
     *         |                   |
     *         y                   x
     *        / \   RIGHT-ROTATE  / \
     *       x   g  ===========> a   y
     *      / \                     / \
     *     a   b                   b   g
     *
     * Complexidade: O(1).
     *
     * @param y Nó em torno do qual a rotação à direita será executada.
     */
    private void rightRotate(RBNode y) {
        RBNode x = y.left;
        y.left = x.right;

        if (x.right != NIL) {
            x.right.parent = y;
        }

        x.parent = y.parent;

        if (y.parent == NIL) {
            this.root = x;
        } else if (y == y.parent.right) {
            y.parent.right = x;
        } else {
            y.parent.left = x;
        }

        x.right = y;
        y.parent = x;

        rightRotationCount++;
    }

    // =========================================================================
    // 2. INSERÇÃO E RB-INSERT-FIXUP (CLRS, SEÇÃO 13.3 - INSERTION)
    // =========================================================================

    /**
     * Procedimento RB-INSERT(T, z) conforme Cormen et al. (Seção 13.3).
     * Insere um novo nó inicialmente com a cor VERMELHA (RED) e, em seguida,
     * invoca RB-INSERT-FIXUP para restaurar possíveis violações das propriedades 2 ou 4.
     *
     * Decisão de projeto: Chaves duplicadas são rejeitadas sem alterar a árvore.
     */
    @Override
    public boolean insert(int key) {
        RBNode y = NIL;
        RBNode x = this.root;

        // Desce na árvore procurando o ponto de inserção (ou detectando duplicata)
        while (x != NIL) {
            y = x;
            if (key == x.key) {
                // Chave repetida: rejeita inserção sem alterar a estrutura
                return false;
            } else if (key < x.key) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        // Cria o novo nó 'z' inicialmente VERMELHO apontando seus filhos para o sentinela NIL
        RBNode z = new RBNode(key, Color.RED);
        z.parent = y;
        z.left = NIL;
        z.right = NIL;

        if (y == NIL) {
            this.root = z; // Árvore estava vazia
        } else if (z.key < y.key) {
            y.left = z;
        } else {
            y.right = z;
        }

        size++;
        rbInsertFixup(z);
        return true;
    }

    /**
     * Procedimento RB-INSERT-FIXUP(T, z) conforme Cormen et al. (Seção 13.3).
     * Corrige a violação da Propriedade 4 (pai de z também é VERMELHO) através de 3 casos:
     * - Caso 1: O tio 'y' de z é VERMELHO -> Recolore pai, tio e avô; sobe z para o avô.
     * - Caso 2: O tio 'y' de z é PRETO e z forma um "zigue-zague" (joelho) -> Rotaciona o pai
     *           para alinhar e transformar no Caso 3.
     * - Caso 3: O tio 'y' de z é PRETO e z está alinhado -> Recolore pai e avô e rotaciona o avô.
     *
     * Observação teórica importante: RB-INSERT-FIXUP realiza no máximo 2 rotações!
     */
    private void rbInsertFixup(RBNode z) {
        while (z.parent.color == Color.RED) {
            if (z.parent == z.parent.parent.left) {
                RBNode y = z.parent.parent.right; // 'y' é o tio de 'z'

                // CASO 1: O tio 'y' é VERMELHO (apenas recoloração)
                if (y.color == Color.RED) {
                    z.parent.color = Color.BLACK;
                    y.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    z = z.parent.parent;
                    balancingAdjustmentCount++;
                } else {
                    // CASO 2: O tio 'y' é PRETO e 'z' é filho à direita (joelho Esquerda-Direita)
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    // CASO 3: O tio 'y' é PRETO e 'z' é filho à esquerda (linha Esquerda-Esquerda)
                    z.parent.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    rightRotate(z.parent.parent);
                    balancingAdjustmentCount++;
                }
            } else {
                // Simétrico: o pai de 'z' é filho à DIREITA do avô
                RBNode y = z.parent.parent.left; // 'y' é o tio de 'z'

                // CASO 1 (Simétrico): O tio 'y' é VERMELHO (apenas recoloração)
                if (y.color == Color.RED) {
                    z.parent.color = Color.BLACK;
                    y.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    z = z.parent.parent;
                    balancingAdjustmentCount++;
                } else {
                    // CASO 2 (Simétrico): O tio 'y' é PRETO e 'z' é filho à esquerda (joelho Direita-Esquerda)
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    // CASO 3 (Simétrico): O tio 'y' é PRETO e 'z' é filho à direita (linha Direita-Direita)
                    z.parent.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    leftRotate(z.parent.parent);
                    balancingAdjustmentCount++;
                }
            }
        }
        // Restaura a Propriedade 2 (a raiz é sempre PRETA)
        this.root.color = Color.BLACK;
    }

    // =========================================================================
    // 3. BUSCA (ITERATIVE-TREE-SEARCH - CLRS, SEÇÃO 12.2)
    // =========================================================================

    @Override
    public boolean search(int key) {
        return searchNode(key) != NIL;
    }

    /**
     * Localiza e retorna o nó correspondente à chave informada, ou NIL se não existir.
     */
    private RBNode searchNode(int key) {
        RBNode current = this.root;
        while (current != NIL) {
            if (key == current.key) {
                return current;
            } else if (key < current.key) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return NIL;
    }

    // =========================================================================
    // 4. REMOÇÃO E RB-DELETE-FIXUP (CLRS, SEÇÃO 13.4 - DELETION)
    // =========================================================================

    /**
     * Procedimento RB-TRANSPLANT(T, u, v) conforme Cormen et al. (Seção 13.4).
     * Substitui a subárvore enraizada no nó 'u' pela subárvore enraizada no nó 'v'.
     */
    private void rbTransplant(RBNode u, RBNode v) {
        if (u.parent == NIL) {
            this.root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }

    /**
     * Procedimento TREE-MINIMUM(x) (CLRS, Seção 12.2).
     * Retorna o nó de menor chave na subárvore enraizada em 'x'.
     */
    private RBNode treeMinimum(RBNode x) {
        while (x.left != NIL) {
            x = x.left;
        }
        return x;
    }

    /**
     * Procedimento RB-DELETE(T, z) conforme Cormen et al. (Seção 13.4).
     * Remove o nó 'z' da árvore. Se a cor original do nó removido/movido ('y')
     * era PRETA (BLACK), invoca RB-DELETE-FIXUP(T, x) para restaurar a altura negra.
     */
    @Override
    public boolean remove(int key) {
        RBNode z = searchNode(key);
        if (z == NIL) {
            // Elemento não existe na árvore
            return false;
        }

        RBNode y = z;
        Color yOriginalColor = y.color;
        RBNode x;

        if (z.left == NIL) {
            // Caso A: 'z' não tem filho à esquerda
            x = z.right;
            rbTransplant(z, z.right);
        } else if (z.right == NIL) {
            // Caso B: 'z' tem filho à esquerda, mas não à direita
            x = z.left;
            rbTransplant(z, z.left);
        } else {
            // Caso C: 'z' possui 2 filhos -> usa o sucessor em-ordem 'y'
            y = treeMinimum(z.right);
            yOriginalColor = y.color;
            x = y.right;

            if (y.parent == z) {
                x.parent = y; // Essencial caso 'x' seja a sentinela NIL
            } else {
                rbTransplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }

            rbTransplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }

        size--;

        // Se o nó removido/movido era PRETO, pode ter violado as Propriedades 2, 4 ou 5
        if (yOriginalColor == Color.BLACK) {
            rbDeleteFixup(x);
        }

        return true;
    }

    /**
     * Procedimento RB-DELETE-FIXUP(T, x) conforme Cormen et al. (Seção 13.4).
     * Restaura a altura negra (Propriedade 5) tratando o nó 'x' como portador de um
     * "preto extra" através de 4 casos baseados na cor do irmão 'w' e dos filhos de 'w':
     * - Caso 1: O irmão 'w' de x é VERMELHO -> recolore e rotaciona para converter em Caso 2, 3 ou 4.
     * - Caso 2: O irmão 'w' é PRETO e ambos os filhos de 'w' são PRETOS -> recolore 'w' de VERMELHO e sobe 'x'.
     * - Caso 3: O irmão 'w' é PRETO, filho interno de 'w' é VERMELHO e filho externo é PRETO ->
     *           recolore e rotaciona 'w' para converter no Caso 4.
     * - Caso 4: O irmão 'w' é PRETO e filho externo de 'w' é VERMELHO -> recolore, rotaciona o pai de 'x' e encerra.
     *
     * Observação teórica importante: RB-DELETE-FIXUP realiza no máximo 3 rotações!
     */
    private void rbDeleteFixup(RBNode x) {
        while (x != this.root && x.color == Color.BLACK) {
            if (x == x.parent.left) {
                RBNode w = x.parent.right; // 'w' é o irmão de 'x'

                // CASO 1: O irmão 'w' é VERMELHO
                if (w.color == Color.RED) {
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                    balancingAdjustmentCount++;
                }

                // CASO 2: O irmão 'w' é PRETO e ambos os seus filhos são PRETOS
                if (w.left.color == Color.BLACK && w.right.color == Color.BLACK) {
                    w.color = Color.RED;
                    x = x.parent;
                    balancingAdjustmentCount++;
                } else {
                    // CASO 3: O irmão 'w' é PRETO, filho esquerdo de 'w' é VERMELHO e direito é PRETO
                    if (w.right.color == Color.BLACK) {
                        w.left.color = Color.BLACK;
                        w.color = Color.RED;
                        rightRotate(w);
                        w = x.parent.right;
                        balancingAdjustmentCount++;
                    }
                    // CASO 4: O irmão 'w' é PRETO e o filho direito de 'w' é VERMELHO
                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.right.color = Color.BLACK;
                    leftRotate(x.parent);
                    x = this.root;
                    balancingAdjustmentCount++;
                }
            } else {
                // Simétrico: 'x' é filho à DIREITA de seu pai
                RBNode w = x.parent.left; // 'w' é o irmão de 'x'

                // CASO 1 (Simétrico): O irmão 'w' é VERMELHO
                if (w.color == Color.RED) {
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    rightRotate(x.parent);
                    w = x.parent.left;
                    balancingAdjustmentCount++;
                }

                // CASO 2 (Simétrico): O irmão 'w' é PRETO e ambos os seus filhos são PRETOS
                if (w.right.color == Color.BLACK && w.left.color == Color.BLACK) {
                    w.color = Color.RED;
                    x = x.parent;
                    balancingAdjustmentCount++;
                } else {
                    // CASO 3 (Simétrico): O irmão 'w' é PRETO, filho direito é VERMELHO e esquerdo é PRETO
                    if (w.left.color == Color.BLACK) {
                        w.right.color = Color.BLACK;
                        w.color = Color.RED;
                        leftRotate(w);
                        w = x.parent.left;
                        balancingAdjustmentCount++;
                    }
                    // CASO 4 (Simétrico): O irmão 'w' é PRETO e o filho esquerdo de 'w' é VERMELHO
                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.left.color = Color.BLACK;
                    rightRotate(x.parent);
                    x = this.root;
                    balancingAdjustmentCount++;
                }
            }
        }
        x.color = Color.BLACK;
        NIL.color = Color.BLACK;
    }

    // =========================================================================
    // 5. PERCURSO EM ORDEM E VISUALIZAÇÃO COM CORES
    // =========================================================================

    @Override
    public List<Integer> inOrder() {
        List<Integer> result = new ArrayList<>(size);
        inOrderRecursive(this.root, result);
        return result;
    }

    private void inOrderRecursive(RBNode node, List<Integer> result) {
        if (node != NIL) {
            inOrderRecursive(node.left, result);
            result.add(node.key);
            inOrderRecursive(node.right, result);
        }
    }

    @Override
    public void printInOrder() {
        List<Integer> elements = inOrder();
        if (elements.isEmpty()) {
            System.out.println("  [Árvore Rubro-Negra vazia]");
        } else {
            System.out.println("  Percurso em ordem (crescente): " + elements);
        }
    }

    @Override
    public void printTree() {
        if (this.root == NIL) {
            System.out.println("  [Árvore Rubro-Negra vazia]");
            return;
        }
        System.out.println("  Estrutura da Árvore Rubro-Negra (Cores: [PRETO] / [VERMELHO]):");
        printTreeRecursive(this.root, "  ", true, "RAIZ");
    }

    private void printTreeRecursive(RBNode node, String prefix, boolean isTail, String role) {
        if (node == NIL) {
            return;
        }
        String colorTag = (node.color == Color.RED)
                ? ANSI_RED + "VERMELHO" + ANSI_RESET
                : "PRETO";

        System.out.println(prefix + (isTail ? "└── " : "├── ")
                + "[" + role + "] " + node.key + " (" + colorTag + ")");

        String childPrefix = prefix + (isTail ? "    " : "│   ");
        if (node.left != NIL || node.right != NIL) {
            if (node.left != NIL && node.right != NIL) {
                printTreeRecursive(node.left, childPrefix, false, "E");
                printTreeRecursive(node.right, childPrefix, true, "D");
            } else if (node.left != NIL) {
                printTreeRecursive(node.left, childPrefix, true, "E");
            } else {
                printTreeRecursive(node.right, childPrefix, true, "D");
            }
        }
    }

    // =========================================================================
    // 6. VALIDAÇÃO DAS 5 PROPRIEDADES RUBRO-NEGRAS (CLRS, SEÇÃO 13.1)
    // =========================================================================

    @Override
    public boolean validateProperties() {
        // Propriedade 3: Sentinela NIL deve ser PRETO
        if (NIL.color != Color.BLACK) {
            return false;
        }
        if (this.root == NIL) {
            return true;
        }
        // Propriedade 2: A raiz deve ser PRETA
        if (this.root.color != Color.BLACK) {
            return false;
        }
        // Verifica ABB, Propriedade 4 (sem dois vermelhos seguidos) e Propriedade 5 (altura negra igual)
        int blackHeight = validateRBRecursive(this.root, Long.MIN_VALUE, Long.MAX_VALUE);
        return blackHeight != -1;
    }

    /**
     * Retorna a quantidade de nós pretos nos caminhos até NIL (black-height),
     * ou -1 caso qualquer propriedade de ABB ou Rubro-Negra seja violada.
     */
    private int validateRBRecursive(RBNode node, long min, long max) {
        if (node == NIL) {
            return 1; // Folha NIL conta como 1 nó preto na base do caminho
        }

        // 1. Verifica ordenação de Árvore Binária de Busca
        if (node.key <= min || node.key >= max) {
            return -1;
        }

        // 2. Propriedade 1: Cor válida
        if (node.color != Color.RED && node.color != Color.BLACK) {
            return -1;
        }

        // 3. Propriedade 4: Se o nó é VERMELHO, seus dois filhos devem ser PRETOS
        if (node.color == Color.RED) {
            if (node.left.color == Color.RED || node.right.color == Color.RED) {
                return -1;
            }
        }

        // 4. Propriedade 5: Altura negra da subárvore esquerda deve ser igual à da direita
        int leftBlackHeight = validateRBRecursive(node.left, min, node.key);
        int rightBlackHeight = validateRBRecursive(node.right, node.key, max);

        if (leftBlackHeight == -1 || rightBlackHeight == -1 || leftBlackHeight != rightBlackHeight) {
            return -1;
        }

        return leftBlackHeight + (node.color == Color.BLACK ? 1 : 0);
    }

    // =========================================================================
    // 7. GETTERS DE ALTURA, TAMANHO E MÉTRICAS
    // =========================================================================

    @Override
    public int getHeight() {
        return computeHeightRecursive(this.root);
    }

    /**
     * Calcula a altura em níveis de nós reais (ignorando o sentinela NIL),
     * mantendo exatamente a mesma escala de medição da Árvore AVL:
     * - Árvore vazia (root == NIL): altura = 0
     * - Árvore apenas com a raiz: altura = 1
     */
    private int computeHeightRecursive(RBNode node) {
        if (node == NIL) {
            return 0;
        }
        return 1 + Math.max(computeHeightRecursive(node.left), computeHeightRecursive(node.right));
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.root == NIL;
    }

    @Override
    public void clear() {
        this.root = this.NIL;
        this.size = 0;
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
        return "Árvore Rubro-Negra";
    }
}
