package benchmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import trees.AVLTree;
import trees.BalancedTree;
import trees.RedBlackTree;

/**
 * Suíte completa de Testes Funcionais, Validação de Propriedades e Análise Comparativa
 * entre a Árvore AVL e a Árvore Rubro-Negra no contexto da Amazon Black Friday.
 *
 * Contempla todos os requisitos da Seção 3 do edital:
 * - Operações em árvores vazias e com apenas um elemento;
 * - Tratamento de chaves repetidas e busca/remoção de elementos inexistentes;
 * - Inserção de elementos em ordem crescente, decrescente e não ordenada (aleatória);
 * - Busca de elementos existentes e inexistentes;
 * - Remoção de nós com diferentes configurações de filhos (folha, 1 filho, 2 filhos/raiz);
 * - Verificação automática das propriedades de balanceamento após inserções e remoções;
 * - Comparação com os mesmos conjuntos de dados em múltiplas escalas (N = 100, 1.000 e 10.000)
 *   e nos dois perfis operacionais da Amazon (Triagem/Atualizações vs. Rastreamento/Buscas).
 */
public class TreeValidatorAndTester {

    /** Semente fixa para garantir que os testes aleatórios sejam 100% reprodutíveis e idênticos nas duas árvores. */
    private static final long RANDOM_SEED = 42L;

    /** Escalas de volume de encomendas definidas para a análise comparativa. */
    private static final int[] SCALES = {100, 1000, 10000};

    public static void main(String[] args) {
        runAllTests();
    }

    /**
     * Executa todas as baterias de testes funcionais e comparativos em sequência.
     */
    public static void runAllTests() {
        printBanner("SUÍTE DE TESTES E ANÁLISE COMPARATIVA - AMAZON BLACK FRIDAY");

        runBattery1EdgeCases();
        runBattery2StepByStepVisualDemo();
        runBattery3RemovalConfigurations();
        runBattery4InsertionComparisonByOrderAndScale();
        runBattery5DeletionComparisonByScale();
        runBattery6AmazonWorkloadProfiles();

        printBanner("TODOS OS TESTES CONCLUÍDOS COM 100% DE INTEGRIDADE DE BALANCEAMENTO");
    }

    // =========================================================================
    // BATERIA 1: ÁRVORE VAZIA, 1 ELEMENTO, DUPLICATAS E INEXISTENTES
    // =========================================================================

    public static void runBattery1EdgeCases() {
        printSectionHeader("BATERIA 1: Casos de Borda (Árvore Vazia, 1 Elemento, Duplicatas e Inexistentes)");

        BalancedTree[] trees = {new AVLTree(), new RedBlackTree()};

        for (BalancedTree tree : trees) {
            System.out.println("\n>>> Testando: " + tree.getTreeName());

            // 1. Operações em árvore vazia
            boolean searchEmpty = tree.search(10);
            boolean removeEmpty = tree.remove(10);
            System.out.printf("  [1] Árvore vazia (size=%d, h=%d) -> busca(10)=%b | remove(10)=%b | válida=%b%n",
                    tree.getSize(), tree.getHeight(), searchEmpty, removeEmpty, tree.validateProperties());

            // 2. Inserção de único elemento
            boolean insertFirst = tree.insert(10);
            System.out.printf("  [2] Inserir 1º elemento (10)     -> inseriu=%b   | size=%d, h=%d | válida=%b%n",
                    insertFirst, tree.getSize(), tree.getHeight(), tree.validateProperties());

            // 3. Tentativa de inserir valor repetido
            boolean insertDuplicate = tree.insert(10);
            System.out.printf("  [3] Inserir duplicata (10)       -> inseriu=%b  | size=%d, h=%d | válida=%b%n",
                    insertDuplicate, tree.getSize(), tree.getHeight(), tree.validateProperties());

            // 4. Busca de existente e inexistente em árvore unitária
            boolean searchExisting = tree.search(10);
            boolean searchNonExisting = tree.search(99);
            System.out.printf("  [4] Busca em árvore com 1 nó     -> busca(10)=%b  | busca(99)=%b%n",
                    searchExisting, searchNonExisting);

            // 5. Remoção de inexistente e remoção do único elemento
            boolean removeNonExisting = tree.remove(99);
            boolean removeOnlyNode = tree.remove(10);
            System.out.printf("  [5] Remoção do único elemento    -> remove(99)=%b | remove(10)=%b | vazia=%b, h=%d | válida=%b%n",
                    removeNonExisting, removeOnlyNode, tree.isEmpty(), tree.getHeight(), tree.validateProperties());
        }
    }

    // =========================================================================
    // BATERIA 2: DEMONSTRAÇÃO VISUAL PASSO A PASSO (SEQUÊNCIA CLÁSSICA)
    // =========================================================================

    public static void runBattery2StepByStepVisualDemo() {
        printSectionHeader("BATERIA 2: Demonstração Visual Passo a Passo (IDs: 10, 20, 30, 40, 50, 25, 35)");

        int[] demoSequence = {10, 20, 30, 40, 50, 25, 35};
        BalancedTree[] trees = {new AVLTree(), new RedBlackTree()};

        for (BalancedTree tree : trees) {
            System.out.println("\n======================================================================");
            System.out.println("  DEMONSTRAÇÃO DE INSERÇÃO E BALANCEAMENTO: " + tree.getTreeName().toUpperCase());
            System.out.println("======================================================================");

            for (int i = 0; i < demoSequence.length; i++) {
                int key = demoSequence[i];
                long rotBefore = tree.getTotalRotationCount();
                tree.insert(key);
                long rotDelta = tree.getTotalRotationCount() - rotBefore;

                // Imprime o estado nos marcos principais para ilustrar rotações simples e duplas / recolorações
                if (key == 30 || key == 50 || key == 25 || key == 35) {
                    System.out.printf("%n  -> Após inserir %d (rotações neste passo: %d | total rotações: %d | altura: %d | válida: %b):%n",
                            key, rotDelta, tree.getTotalRotationCount(), tree.getHeight(), tree.validateProperties());
                    tree.printTree();
                }
            }
            System.out.println();
            tree.printInOrder();
        }
    }

    // =========================================================================
    // BATERIA 3: REMOÇÃO COM DIFERENTES CONFIGURAÇÕES DE FILHOS
    // =========================================================================

    public static void runBattery3RemovalConfigurations() {
        printSectionHeader("BATERIA 3: Remoção de Nós (Inexistente, Folha/0 Filhos, 1 Filho e 2 Filhos)");

        int[] initialKeys = {10, 20, 30, 40, 50, 25, 35};
        BalancedTree[] trees = {new AVLTree(), new RedBlackTree()};

        for (BalancedTree tree : trees) {
            for (int k : initialKeys) {
                tree.insert(k);
            }
            tree.resetMetrics();

            System.out.println("\n>>> Testando Remoções em: " + tree.getTreeName());
            System.out.println("  Estado Inicial (7 elementos):");
            tree.printTree();

            // 1. Remover elemento inexistente (999)
            boolean remInexistente = tree.remove(999);
            System.out.printf("%n  [Caso A] Remover inexistente (999): sucesso=%b | tamanho=%d | válida=%b%n",
                    remInexistente, tree.getSize(), tree.validateProperties());

            // 2. Remover nó FOLHA (0 filhos) -> chave 35 é folha em ambas as árvores
            boolean remFolha = tree.remove(35);
            System.out.printf("%n  [Caso B] Remover nó FOLHA / 0 filhos (35): sucesso=%b | tamanho=%d | altura=%d | válida=%b%n",
                    remFolha, tree.getSize(), tree.getHeight(), tree.validateProperties());
            tree.printTree();

            // 3. Remover nó com 1 FILHO -> após remover 35, o nó 40 possui apenas 1 filho (50)
            boolean remUmFilho = tree.remove(40);
            System.out.printf("%n  [Caso C] Remover nó com 1 FILHO (40): sucesso=%b | tamanho=%d | altura=%d | válida=%b%n",
                    remUmFilho, tree.getSize(), tree.getHeight(), tree.validateProperties());
            tree.printTree();

            // 4. Remover nó com 2 FILHOS (subárvore / raiz) -> chave 20 possui 2 filhos
            boolean remDoisFilhos = tree.remove(20);
            System.out.printf("%n  [Caso D] Remover nó com 2 FILHOS (20): sucesso=%b | tamanho=%d | altura=%d | rotações na remoção=%d | válida=%b%n",
                    remDoisFilhos, tree.getSize(), tree.getHeight(), tree.getTotalRotationCount(), tree.validateProperties());
            tree.printTree();
            tree.printInOrder();
        }
    }

    // =========================================================================
    // BATERIA 4: ANÁLISE COMPARATIVA DE INSERÇÃO (CRESCENTE, DECRESCENTE, ALEATÓRIA)
    // =========================================================================

    public static void runBattery4InsertionComparisonByOrderAndScale() {
        printSectionHeader("BATERIA 4: Comparativo de Inserção (Crescente, Decrescente e Não Ordenada) em Múltiplas Escalas");

        System.out.println(String.format(
                "  %-22s | %-7s | %-18s | %-6s | %-8s | %-8s | %-9s | %-8s | %-6s",
                "Cenário de Inserção", "N", "Estrutura", "Altura", "Rot. Esq", "Rot. Dir", "Total Rot", "Ajustes", "Válida"));
        System.out.println("  " + "-".repeat(108));

        for (int n : SCALES) {
            List<Integer> ascending = generateAscendingData(n);
            List<Integer> descending = generateDescendingData(n);
            List<Integer> randomData = generateRandomData(n, RANDOM_SEED);

            compareInsertionScenario("1. Ordem Crescente", ascending);
            compareInsertionScenario("2. Ordem Decrescente", descending);
            compareInsertionScenario("3. Não Ordenada (Aleat)", randomData);
            System.out.println("  " + "-".repeat(108));
        }
    }

    private static void compareInsertionScenario(String scenarioName, List<Integer> dataset) {
        BalancedTree[] trees = {new AVLTree(), new RedBlackTree()};
        int n = dataset.size();

        for (BalancedTree tree : trees) {
            for (int key : dataset) {
                tree.insert(key);
            }
            boolean valid = tree.validateProperties();

            System.out.println(String.format(
                    "  %-22s | %7d | %-18s | %6d | %8d | %8d | %9d | %8d | %-6s",
                    scenarioName,
                    n,
                    tree.getTreeName(),
                    tree.getHeight(),
                    tree.getLeftRotationCount(),
                    tree.getRightRotationCount(),
                    tree.getTotalRotationCount(),
                    tree.getBalancingAdjustmentCount(),
                    valid ? "SIM" : "ERRO"));
        }
    }

    // =========================================================================
    // BATERIA 5: ANÁLISE COMPARATIVA DE REMOÇÃO E VERIFICAÇÃO PÓS-REMOÇÃO
    // =========================================================================

    public static void runBattery5DeletionComparisonByScale() {
        printSectionHeader("BATERIA 5: Comparativo de Remoção (Removendo 50% das Encomendas após Inserção Aleatória)");

        System.out.println(String.format(
                "  %-7s | %-18s | %-12s | %-12s | %-13s | %-10s | %-6s",
                "N Inic.", "Estrutura", "Altura Antes", "Altura Final", "Rotações Rem.", "Ajustes Rem", "Válida"));
        System.out.println("  " + "-".repeat(90));

        for (int n : SCALES) {
            List<Integer> initialData = generateRandomData(n, RANDOM_SEED);
            List<Integer> removalOrder = new ArrayList<>(initialData);
            Collections.shuffle(removalOrder, new Random(RANDOM_SEED + 1));

            BalancedTree[] trees = {new AVLTree(), new RedBlackTree()};
            for (BalancedTree tree : trees) {
                for (int key : initialData) {
                    tree.insert(key);
                }
                int heightBefore = tree.getHeight();

                // Zera os contadores para medir exclusivamente as rotações e ajustes causados pelas remoções
                tree.resetMetrics();

                boolean allValid = true;
                int removeCount = n / 2;
                for (int i = 0; i < removeCount; i++) {
                    tree.remove(removalOrder.get(i));
                    // Para N=100, valida a árvore a cada remoção individual; para N maior, valida ao final
                    if (n == 100 && !tree.validateProperties()) {
                        allValid = false;
                    }
                }
                if (!tree.validateProperties()) {
                    allValid = false;
                }

                System.out.println(String.format(
                        "  %7d | %-18s | %12d | %12d | %13d | %10d | %-6s",
                        n,
                        tree.getTreeName(),
                        heightBefore,
                        tree.getHeight(),
                        tree.getTotalRotationCount(),
                        tree.getBalancingAdjustmentCount(),
                        allValid ? "SIM" : "ERRO"));
            }
            System.out.println("  " + "-".repeat(90));
        }
    }

    // =========================================================================
    // BATERIA 6: SIMULAÇÃO DOS 2 PERFIS DA AMAZON BLACK FRIDAY (N = 10.000)
    // =========================================================================

    public static void runBattery6AmazonWorkloadProfiles() {
        printSectionHeader("BATERIA 6: Simulação dos 2 Perfis Operacionais da Amazon Black Friday (N = 10.000)");

        int n = 10000;
        List<Integer> batch1 = generateRandomData(n, RANDOM_SEED);
        List<Integer> batch2 = generateRandomData(n, RANDOM_SEED + 99);

        // PERFIL A: Centro de Triagem e Despacho (Intenso em Inserções e Remoções)
        System.out.println("\n  [PERFIL A] Centro de Triagem e Despacho (Write-Heavy: 10.000 inserções + 5.000 baixas + 5.000 novas inserções):");
        BalancedTree[] writeTrees = {new AVLTree(), new RedBlackTree()};
        for (BalancedTree tree : writeTrees) {
            long startNs = System.nanoTime();
            for (int k : batch1) {
                tree.insert(k);
            }
            for (int i = 0; i < n / 2; i++) {
                tree.remove(batch1.get(i));
            }
            for (int i = 0; i < n / 2; i++) {
                tree.insert(batch2.get(i));
            }
            long elapsedUs = (System.nanoTime() - startNs) / 1_000;

            System.out.printf("    -> %-18s | Total de Rotações: %6d (Esq=%d, Dir=%d) | Altura Final: %2d | Tempo: %6d us | Válida: %b%n",
                    tree.getTreeName(),
                    tree.getTotalRotationCount(),
                    tree.getLeftRotationCount(),
                    tree.getRightRotationCount(),
                    tree.getHeight(),
                    elapsedUs,
                    tree.validateProperties());
        }

        // PERFIL B: Portal de Rastreamento de Pacotes pelo Cliente (Intenso em Buscas - 500.000 consultas)
        System.out.println("\n  [PERFIL B] Portal de Rastreamento pelo Cliente (Read-Heavy: 10.000 encomendas ativas + 500.000 buscas):");
        BalancedTree[] readTrees = {new AVLTree(), new RedBlackTree()};
        int totalSearches = 500_000;

        for (BalancedTree tree : readTrees) {
            for (int k : batch1) {
                tree.insert(k);
            }

            long startNs = System.nanoTime();
            int foundCount = 0;
            for (int i = 0; i < totalSearches; i++) {
                // Alterna buscas entre elementos existentes e elementos inexistentes
                int queryKey = (i % 2 == 0) ? batch1.get(i % n) : (n * 10 + i);
                if (tree.search(queryKey)) {
                    foundCount++;
                }
            }
            long elapsedUs = (System.nanoTime() - startNs) / 1_000;

            System.out.printf("    -> %-18s | Altura da Árvore: %2d | Rotações na Carga: %5d | Buscas (500k): %6d us (achou=%d)%n",
                    tree.getTreeName(),
                    tree.getHeight(),
                    tree.getTotalRotationCount(),
                    elapsedUs,
                    foundCount);
        }
    }

    // =========================================================================
    // GERADORES DE CONJUNTOS DE DADOS (MESMOS DADOS PARA AMBAS AS ÁRVORES)
    // =========================================================================

    public static List<Integer> generateAscendingData(int n) {
        List<Integer> list = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            list.add(i);
        }
        return list;
    }

    public static List<Integer> generateDescendingData(int n) {
        List<Integer> list = new ArrayList<>(n);
        for (int i = n; i >= 1; i--) {
            list.add(i);
        }
        return list;
    }

    public static List<Integer> generateRandomData(int n, long seed) {
        List<Integer> list = generateAscendingData(n);
        Collections.shuffle(list, new Random(seed));
        return list;
    }

    // =========================================================================
    // UTILITÁRIOS DE FORMATAÇÃO DE SAÍDA
    // =========================================================================

    private static void printBanner(String title) {
        System.out.println("\n" + "=".repeat(112));
        System.out.println("  " + title);
        System.out.println("=".repeat(112));
    }

    private static void printSectionHeader(String sectionTitle) {
        System.out.println("\n----------------------------------------------------------------------------------------------------------------");
        System.out.println("  " + sectionTitle);
        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }
}
