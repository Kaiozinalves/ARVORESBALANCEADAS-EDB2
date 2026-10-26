import benchmark.TreeValidatorAndTester;
import java.util.Scanner;
import trees.AVLTree;
import trees.BalancedTree;
import trees.RedBlackTree;

/**
 * Classe principal (Ponto de Entrada) do Sistema de Logística da Amazon (Black Friday).
 *
 * Permite ao usuário escolher, em tempo de execução, qual estrutura de dados utilizar:
 * 1. Árvore AVL
 * 2. Árvore Rubro-Negra
 * 3. Executar a Suíte Completa de Testes e Análise Comparativa
 */
public class Main {

    public static void main(String[] args) {
        // Permite rodar diretamente a suíte de testes se passar "--test" como argumento
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            TreeValidatorAndTester.runAllTests();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMainHeader();
            System.out.println("  Escolha o modo de operação:");
            System.out.println("    [1] Utilizar Árvore AVL");
            System.out.println("    [2] Utilizar Árvore Rubro-Negra");
            System.out.println("    [3] Executar Suíte de Testes e Análise Comparativa (AVL vs. Rubro-Negra)");
            System.out.println("    [0] Encerrar o sistema");
            System.out.print("\n  Opção desejada: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    runTreeMenu(scanner, new AVLTree());
                    break;
                case "2":
                    runTreeMenu(scanner, new RedBlackTree());
                    break;
                case "3":
                    TreeValidatorAndTester.runAllTests();
                    break;
                case "0":
                    System.out.println("\n  Encerrando o sistema de logística da Amazon. Até logo!");
                    running = false;
                    break;
                default:
                    System.out.println("\n  [!] Opção inválida. Digite 1, 2, 3 ou 0.");
            }
        }

        scanner.close();
    }

    /**
     * Submenu interativo de operações para a árvore escolhida pelo usuário (AVL ou Rubro-Negra).
     */
    private static void runTreeMenu(Scanner scanner, BalancedTree tree) {
        boolean inSubMenu = true;

        while (inSubMenu) {
            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.printf("  ESTRUTURA ATIVA: %s | Elementos: %d | Altura: %d | Rotações: %d (Esq=%d, Dir=%d)%n",
                    tree.getTreeName().toUpperCase(),
                    tree.getSize(),
                    tree.getHeight(),
                    tree.getTotalRotationCount(),
                    tree.getLeftRotationCount(),
                    tree.getRightRotationCount());
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("  [1] Inserir um identificador de encomenda (ID)");
            System.out.println("  [2] Inserir vários identificadores em lote (ex: 10 20 30 40 50 25 35)");
            System.out.println("  [3] Remover um identificador de encomenda (ID)");
            System.out.println("  [4] Buscar um identificador de encomenda (ID)");
            System.out.println("  [5] Percurso em ordem (exibir elementos em ordem crescente)");
            System.out.println("  [6] Visualizar estrutura da árvore");
            System.out.println("  [7] Verificar propriedades de balanceamento e métricas detalhadas");
            System.out.println("  [8] Limpar árvore (reiniciar)");
            System.out.println("  [0] Voltar ao menu principal (trocar de árvore)");
            System.out.print("\n  Escolha uma operação: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleSingleInsert(scanner, tree);
                    break;
                case "2":
                    handleBatchInsert(scanner, tree);
                    break;
                case "3":
                    handleRemove(scanner, tree);
                    break;
                case "4":
                    handleSearch(scanner, tree);
                    break;
                case "5":
                    System.out.println();
                    tree.printInOrder();
                    break;
                case "6":
                    System.out.println();
                    tree.printTree();
                    break;
                case "7":
                    printDetailedMetrics(tree);
                    break;
                case "8":
                    tree.clear();
                    System.out.println("\n  [OK] Árvore limpa e contadores reiniciados.");
                    break;
                case "0":
                    inSubMenu = false;
                    break;
                default:
                    System.out.println("\n  [!] Operação inválida. Escolha uma opção de 0 a 8.");
            }
        }
    }

    private static void handleSingleInsert(Scanner scanner, BalancedTree tree) {
        System.out.print("  Digite o ID da encomenda (inteiro) para INSERIR: ");
        Integer key = readInt(scanner);
        if (key == null) {
            return;
        }

        long rotBefore = tree.getTotalRotationCount();
        boolean inserted = tree.insert(key);
        long rotAfter = tree.getTotalRotationCount();

        if (inserted) {
            System.out.printf("  [OK] Encomenda ID %d inserida com sucesso! (Rotações realizadas nesta operação: %d)%n",
                    key, (rotAfter - rotBefore));
            tree.printTree();
        } else {
            System.out.printf("  [AVISO] Encomenda ID %d já existe na árvore! Inserção de valor repetido ignorada.%n", key);
        }
    }

    private static void handleBatchInsert(Scanner scanner, BalancedTree tree) {
        System.out.print("  Digite os IDs separados por espaço ou vírgula (ex: 10 20 30 40 50 25 35): ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("  [!] Nenhum valor informado.");
            return;
        }

        String[] tokens = line.split("[\\s,;]+");
        int insertedCount = 0;
        int duplicateCount = 0;
        long rotBefore = tree.getTotalRotationCount();

        for (String token : tokens) {
            try {
                int key = Integer.parseInt(token);
                if (tree.insert(key)) {
                    insertedCount++;
                } else {
                    duplicateCount++;
                    System.out.printf("  [AVISO] ID %d repetido (ignorado).%n", key);
                }
            } catch (NumberFormatException e) {
                System.out.printf("  [!] Valor inválido ignorado: '%s'%n", token);
            }
        }

        long rotDelta = tree.getTotalRotationCount() - rotBefore;
        System.out.printf("%n  [OK] Lote processado: %d inseridos, %d duplicados ignorados, %d rotações realizadas.%n",
                insertedCount, duplicateCount, rotDelta);
        tree.printTree();
    }

    private static void handleRemove(Scanner scanner, BalancedTree tree) {
        if (tree.isEmpty()) {
            System.out.println("  [AVISO] Operação cancelada: a árvore está vazia!");
            return;
        }
        System.out.print("  Digite o ID da encomenda (inteiro) para REMOVER: ");
        Integer key = readInt(scanner);
        if (key == null) {
            return;
        }

        long rotBefore = tree.getTotalRotationCount();
        boolean removed = tree.remove(key);
        long rotAfter = tree.getTotalRotationCount();

        if (removed) {
            System.out.printf("  [OK] Encomenda ID %d removida com sucesso! (Rotações realizadas nesta remoção: %d)%n",
                    key, (rotAfter - rotBefore));
            tree.printTree();
        } else {
            System.out.printf("  [AVISO] Encomenda ID %d não encontrada na árvore! Nenhuma alteração realizada.%n", key);
        }
    }

    private static void handleSearch(Scanner scanner, BalancedTree tree) {
        if (tree.isEmpty()) {
            System.out.println("  [AVISO] A árvore está vazia. Qualquer busca retornará falso.");
            return;
        }
        System.out.print("  Digite o ID da encomenda (inteiro) para BUSCAR: ");
        Integer key = readInt(scanner);
        if (key == null) {
            return;
        }

        boolean found = tree.search(key);
        if (found) {
            System.out.printf("  [ENCONTRADO] A encomenda ID %d está registrada no sistema!%n", key);
        } else {
            System.out.printf("  [NÃO ENCONTRADO] A encomenda ID %d não existe no sistema.%n", key);
        }
    }

    private static void printDetailedMetrics(BalancedTree tree) {
        System.out.println("\n  === Relatório de Estado e Propriedades (" + tree.getTreeName() + ") ===");
        System.out.println("  - Quantidade de elementos : " + tree.getSize());
        System.out.println("  - Altura atual da árvore  : " + tree.getHeight());
        System.out.println("  - Rotações à esquerda     : " + tree.getLeftRotationCount());
        System.out.println("  - Rotações à direita      : " + tree.getRightRotationCount());
        System.out.println("  - Total de rotações       : " + tree.getTotalRotationCount());
        System.out.println("  - Ajustes / Recolorações  : " + tree.getBalancingAdjustmentCount());
        System.out.println("  - Propriedades íntegras?  : " + (tree.validateProperties() ? "SIM (100% Válida)" : "NÃO (Violada)"));
    }

    private static Integer readInt(Scanner scanner) {
        String text = scanner.nextLine().trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.out.println("  [!] Entrada inválida. Por favor, digite um número inteiro válido.");
            return null;
        }
    }

    private static void printMainHeader() {
        System.out.println("\n================================================================================");
        System.out.println("   AMAZON LOGISTICS ENGINEERING - SISTEMA DE ENCOMENDAS (BLACK FRIDAY)");
        System.out.println("   Protótipo Comparativo: Árvore AVL vs. Árvore Rubro-Negra (IMD0039 - EDB2)");
        System.out.println("================================================================================");
    }
}
