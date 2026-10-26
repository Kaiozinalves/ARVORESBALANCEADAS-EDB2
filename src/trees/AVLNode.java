package trees;

/**
 * Representa um nó de uma Árvore AVL.
 *
 * Referências Bibliográficas:
 * - ADELSON-VELSKY, G.; LANDIS, E. M. An algorithm for the organization of information.
 *   Soviet Mathematics Doklady, v. 3, p. 1259-1263, 1962.
 * - WEISS, Mark Allen. Data Structures and Algorithm Analysis in Java. 3rd ed.
 *   Boston: Pearson, 2012. Cap. 4 (Seção 4.4 - AVL Trees).
 */
public class AVLNode {

    /** Identificador numérico da encomenda (chave de busca). */
    public int key;

    /**
     * Altura do nó na árvore.
     * Convenção: um nó recém-criado (folha) possui height = 1
     * (e um ponteiro nulo possui height = 0).
     */
    public int height;

    /** Referência para o filho à esquerda (chaves menores). */
    public AVLNode left;

    /** Referência para o filho à direita (chaves maiores). */
    public AVLNode right;

    /**
     * Constrói um novo nó folha da Árvore AVL com a chave informada.
     *
     * @param key Identificador da encomenda.
     */
    public AVLNode(int key) {
        this.key = key;
        this.height = 1; // Nó folha inicia no nível/altura 1 (ponteiros null têm altura 0)
        this.left = null;
        this.right = null;
    }
}
