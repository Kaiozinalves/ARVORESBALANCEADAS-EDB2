package trees;

/**
 * Representa um nó de uma Árvore Rubro-Negra (Red-Black Tree).
 *
 * Decisão de Implementação (baseada em Cormen et al., Cap. 13, Seção 13.1):
 * Cada nó armazena 5 atributos:
 * - key: identificador da encomenda;
 * - color: Color.RED ou Color.BLACK;
 * - left: filho esquerdo;
 * - right: filho direito;
 * - parent: referência para o nó pai (essencial para subir na árvore em O(1)
 *   durante os algoritmos iterativos RB-INSERT-FIXUP e RB-DELETE-FIXUP,
 *   acessando diretamente o pai, o tio e o avô).
 *
 * Referência Bibliográfica:
 * - CORMEN, T. H.; LEISERSON, C. E.; RIVEST, R. L.; STEIN, C.
 *   Algoritmos: Teoria e Prática. 3ª/4ª ed. Rio de Janeiro: Elsevier, Cap. 13.
 */
public class RBNode {

    /** Identificador numérico da encomenda. */
    public int key;

    /** Cor do nó (RED ou BLACK). */
    public Color color;

    /** Filho à esquerda. */
    public RBNode left;

    /** Filho à direita. */
    public RBNode right;

    /** Ponteiro para o nó pai. */
    public RBNode parent;

    /**
     * Construtor utilizado para criar o nó sentinela T.nil (sempre PRETO)
     * ou nós regulares antes de vinculá-los ao sentinela.
     *
     * @param key   Chave do nó.
     * @param color Cor inicial do nó.
     */
    public RBNode(int key, Color color) {
        this.key = key;
        this.color = color;
        this.left = null;
        this.right = null;
        this.parent = null;
    }
}
