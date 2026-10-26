# Amazon Engineering Challenge: Árvores Balanceadas (AVL e Rubro-Negra)

> **Projeto da Unidade II — Estruturas de Dados Básicas II (IMD0039)**  
> **Instituto Metrópole Digital — Universidade Federal do Rio Grande do Norte (UFRN)**  
> **Cenário:** Simulação e benchmark comparativo para o sistema de triagem e rastreamento de encomendas da Black Friday.

---

## 1. Identificação do Autor

- **Nome:** [Seu Nome Completo]
- **Matrícula:** [Sua Matrícula]
- **E-mail:** [Seu E-mail Institucional ou Pessoal]
- **Disciplina:** IMD0039 - Estruturas de Dados Básicas II
- **Docente:** Prof. [Nome do Docente]

---

## 2. Descrição do Sistema

Durante o pico da Black Friday, o ecossistema logístico da Amazon processa milhões de pacotes em tempo real. Este projeto implementa de ponta a ponta, em **Java puro** (sem uso de pacotes de balanceamento da biblioteca padrão ou externas), as duas estruturas de dados fundamentais para busca e indexação eficiente em memória:

1. **Árvore AVL (Adelson-Velsky e Landis, 1962):** Árvore binária de busca rigidamente balanceada pela altura, onde a diferença entre as alturas das subárvores de qualquer nó (Fator de Balanceamento, $FB = h_{esq} - h_{dir}$) é mantida estritamente em $\{-1, 0, 1\}$.
2. **Árvore Rubro-Negra Clássica (Cormen, Leiserson, Rivest e Stein - CLRS Cap. 13):** Árvore binária de busca com balanceamento relaxado baseado em coloração de nós (`RED`/`BLACK`), nós sentinela `NIL` e ponteiros para pai (`parent`), garantindo que nenhum caminho da raiz a uma folha seja mais que o dobro do comprimento de qualquer outro.

### Principais Funcionalidades:
- **Interface Polimórfica (`BalancedTree`):** Contrato comum para operações de inserção, remoção, busca e extração de métricas de rotação e altura.
- **Instrumentação e Métricas Detalhadas:** Contabilização precisa de rotações simples à esquerda, rotações simples à direita, rotações duplas (registradas como 2 rotações simples) e ajustes de recoloração/balanceamento.
- **Visualizador Hierárquico no Terminal:** Renderização gráfica em ASCII com fatores de balanceamento e altura (na AVL) e identificadores de cor (na Rubro-Negra).
- **Validador Formal de Propriedades:** Verificação recursiva de integridade após cada operação (FB na AVL; 5 propriedades de Cormen na Rubro-Negra).
- **Menu Interativo (CLI) e Bateria de Benchmarks Automatizados:** Permite testes manuais e simulações com grandes volumes de dados ($N = 100, 1.000, 10.000$ e até $500.000$ consultas).

---

## 3. Estrutura do Repositório

```text
ARVORESBALANCEADAS-EDB2/
├── src/                               # Código-fonte Java
│   ├── Main.java                      # Ponto de entrada (CLI interativo e flag --test)
│   ├── trees/                         # Implementação das Árvores Balanceadas
│   │   ├── BalancedTree.java          # Interface comum
│   │   ├── AVLNode.java               # Nó da Árvore AVL
│   │   ├── AVLTree.java               # Implementação completa da Árvore AVL
│   │   ├── Color.java                 # Enum de cores (RED, BLACK)
│   │   ├── RBNode.java                # Nó da Árvore Rubro-Negra
│   │   └── RedBlackTree.java          # Implementação clássica de Cormen (CLRS)
│   └── benchmark/                     # Suíte de Testes e Validação
│       └── TreeValidatorAndTester.java # 6 baterias de testes e perfis de carga
├── bin/                               # Bytecodes compilados (.class)
├── AmazonTrees.jar                    # Executável autocontido pronto para uso
├── .gitignore                         # Configuração de arquivos ignorados
└── README.md                          # Este documento de documentação
```

---

## 4. Instruções de Compilação e Execução

### Pré-requisitos
- **Java Development Kit (JDK):** Versão 11 ou superior (testado e homologado no OpenJDK 17 e OpenJDK 21). Nenhuma dependência externa é requerida.

---

### Opção A: Executar diretamente o arquivo `.jar` (Recomendado)
O projeto já conta com o executável compilado na raiz:

```bash
# Executa o Menu Interativo CLI
java -jar AmazonTrees.jar

# Ou executa diretamente toda a bateria de testes automatizados
java -jar AmazonTrees.jar --test
```

---

### Opção B: Executar a partir dos arquivos compilados em `bin/`
```bash
# Executa o Menu Interativo
java -cp bin Main

# Executa diretamente a Suíte de Benchmarks
java -cp bin benchmark.TreeValidatorAndTester
```

---

### Opção C: Recompilar do zero a partir do código-fonte (`src/`)

**No Linux / macOS:**
```bash
# Cria o diretório de destino e compila todas as classes
mkdir -p bin
javac -d bin src/trees/*.java src/benchmark/*.java src/Main.java

# Executa
java -cp bin Main
```

**No Windows (PowerShell ou Prompt de Comando):**
```powershell
mkdir bin
javac -d bin src\trees\*.java src\benchmark\*.java src\Main.java
java -cp bin Main
```

---

## 5. Decisões de Implementação Adotadas

Em conformidade com as diretrizes do projeto, foram adotadas as seguintes decisões técnicas de engenharia:

### 5.1. Tratamento de Elementos Duplicados
- **Comportamento Adotado:** **Rejeição explícita.** A tentativa de inserção de uma chave já existente retorna `false` e encerra a operação imediatamente, sem modificar a estrutura da árvore, sem duplicar nós e sem disparar rotações.
- **Justificativa Técnica:** No domínio logístico da Amazon, o identificador de encomenda (`Package ID` / `Tracking ID`) representa uma chave primária estritamente única. Admitir chaves duplicadas comprometeria a integridade do rastreamento de pacotes e geraria ambiguidade na entrega.

### 5.2. Tratamento de Remoção de Elementos Inexistentes
- **Comportamento Adotado:** **Encerramento seguro com retorno booleano.** Se o nó a ser removido não for encontrado durante o percurso de busca binária, o método retorna `false` de forma elegante, sem lançar exceções e sem executar qualquer rotação de rebalanceamento.
- **Justificativa Técnica:** Em sistemas distribuídos de alta concorrência sujeitos a retentativas de requisições (como cancelamentos concorrentes de pedidos), operações idempotentes que falham silenciosamente e retornam status evitam o overhead desnecessário de inspeção da árvore e garantem alta disponibilidade.

### 5.3. Tratamento da Remoção de Nós com Dois Filhos
- **Comportamento Adotado:** **Substituição pelo Sucessor em Ordem Simétrica.** Quando um nó interno possui ambos os filhos esquerdo e direito, seu valor/posição é substituído pelo menor nó da subárvore direita (`findMin(node.right)` na AVL e `treeMinimum(node.right)` na Rubro-Negra). Em seguida, a remoção física prossegue no sucessor original (que possui no máximo um filho).
- **Justificativa Técnica:** Trata-se da convenção canônica consolidada na literatura clássica (Cormen et al. e Weiss). A escolha determinística do sucessor preserva rigorosamente a propriedade de busca binária (todos os elementos à esquerda menores e à direita maiores), mantém o custo amortizado $O(\log N)$ e garante previsibilidade nos testes unitários e nos encadeamentos de rotações.

---

## 6. Metodologia de Testes e Cenários Investigados

A suíte automatizada (`TreeValidatorAndTester.java`) submete ambas as estruturas a 6 baterias de testes rigorosos sob condições idênticas de entrada:

1. **Bateria 1 — Casos de Borda:** Testes com árvore vazia, inserção do primeiro elemento, tentativa de duplicação, buscas por chaves presentes/ausentes e esvaziamento total.
2. **Bateria 2 — Demonstração Didática:** Inserção sequencial do conjunto `[10, 20, 30, 40, 50, 25, 35]`, auditando cada rotação simples e dupla, altura e percurso em-ordem.
3. **Bateria 3 — Casos Topológicos de Remoção:** Remoção de nós folha (0 filhos), nós com 1 filho e nós com 2 filhos (incluindo a raiz), verificando a integridade das propriedades estruturais.
4. **Bateria 4 — Análise em Diferentes Escalas ($N = 100, 1.000, 10.000$):**
   - **Ordem Crescente:** Pior caso para árvores binárias não balanceadas (degenera para lista).
   - **Ordem Decrescente:** Pior caso invertido.
   - **Ordem Aleatória (Não Ordenada):** Simulação de carga realista gerada com semente determinística (`Random(42)`).
5. **Bateria 5 — Remoção em Massa (50% do Volume):** Avaliação do comportamento e número de rotações sob remoção intensa de metade dos nós após carga aleatória.
6. **Bateria 6 — Simulação dos Perfis Operacionais da Amazon Black Friday ($N = 10.000$):**
   - **Perfil A (Centro de Triagem / Write-Heavy):** $10.000$ inserções iniciais $\rightarrow 5.000$ baixas/despachos $\rightarrow 5.000$ novos pacotes inseridos.
   - **Perfil B (Portal de Rastreio do Cliente / Read-Heavy):** $10.000$ pacotes ativos mantidos em memória $\rightarrow 500.000$ consultas concorrentes de status.

---

## 7. Tabela Síntese dos Resultados Experimentais

| Cenário de Teste | Tamanho ($N$) | Estrutura | Altura Final | Rotações Totais | Ajustes de Balanceamento | Integridade Válida? |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **Crescente** | 100 | AVL | **7** | 98 | 98 | SIM |
| **Crescente** | 100 | Rubro-Negra | 11 | **89** | 176 | SIM |
| **Crescente** | 1.000 | AVL | **10** | 990 | 990 | SIM |
| **Crescente** | 1.000 | Rubro-Negra | 17 | **983** | 1.961 | SIM |
| **Crescente** | 10.000 | AVL | **14** | 9.986 | 9.986 | SIM |
| **Crescente** | 10.000 | Rubro-Negra | 24 | **9.976** | 19.947 | SIM |
| **Aleatório** | 100 | AVL | **8** | 70 | 50 | SIM |
| **Aleatório** | 100 | Rubro-Negra | **8** | **60** (-14,3%) | 86 | SIM |
| **Aleatório** | 1.000 | AVL | **12** | 713 | 476 | SIM |
| **Aleatório** | 1.000 | Rubro-Negra | **12** | **612** (-14,2%) | 922 | SIM |
| **Aleatório** | 10.000 | AVL | **16** | 6.991 | 4.642 | SIM |
| **Aleatório** | 10.000 | Rubro-Negra | **16** | **5.848** (-16,3%) | 8.974 | SIM |
| **Remoção 50%** | 10.000 | AVL | **15** | 1.888 | 1.347 | SIM |
| **Remoção 50%** | 10.000 | Rubro-Negra | 16 | **1.780** (-5,7%) | 3.355 | SIM |
| **Perfil A (Triagem)** | 10.000 ops | AVL | **16** | 10.624 | 7.336 | SIM |
| **Perfil A (Triagem)** | 10.000 ops | Rubro-Negra | **16** | **9.154** (-13,8%) | 15.684 | SIM |
| **Perfil B (Rastreio)** | 500k buscas | AVL | **16** | N/A (Leitura) | **21,2 ms** (13% mais rápido) | SIM |
| **Perfil B (Rastreio)** | 500k buscas | Rubro-Negra | **16** | N/A (Leitura) | 24,3 ms | SIM |

---

## 8. Recomendação Técnica para a Black Friday

Com base nos dados empíricos observados e na fundamentação teórica da literatura:

1. **Para os Centros de Distribuição e Triagem (Cargas Write-Heavy / Inserções e Despachos Contínuos):**
   - **Estrutura Recomendada:** **Árvore Rubro-Negra**.
   - **Justificativa:** A Árvore Rubro-Negra realiza aproximadamente **16,3% menos rotações estruturais** em dados aleatórios e **13,8% menos rotações no Perfil A**, além de possuir limites teóricos rígidos de no máximo **2 rotações na inserção** e **3 rotações na remoção**. A maior parte de suas correções ocorre via recoloração em memória (ajustes de bits de cor), reduzindo a reescrita de ponteiros e minimizando a contenção de locks em cenários concorrentes.

2. **Para o Portal de Rastreamento de Pacotes pelo Consumidor (Cargas Read-Heavy / Altíssimo Volume de Consultas):**
   - **Estrutura Recomendada:** **Árvore AVL**.
   - **Justificativa:** O fator de balanceamento estrito ($\le 1$) garante que a altura da árvore seja sempre minimizada (em sequências crescentes/ordenadas, atingiu altura 14 contra 24 da Rubro-Negra). Com menor profundidade média de percurso e localidade de referência favorável, as 500.000 buscas foram executadas em tempo inferior, tornando-a ideal para catálogos estáticos e indexações em memória dominadas por leitura.

---

## 9. Declaração sobre o Uso de Inteligência Artificial Generativa

Em cumprimento às diretrizes de integridade acadêmica do projeto, declara-se a utilização de ferramentas de Inteligência Artificial Generativa (**Google Antigravity / Gemini**) com as seguintes finalidades explícitas:

- **Estruturação Visual e Diagramação:** Auxílio na geração dos esquemas gráficos das árvores, fluxogramas de rotação (LL, RR, LR, RL e Casos 1, 2 e 3 da Rubro-Negra) e organização estética das tabelas comparativas para a apresentação de slides.
- **Automação de Cenários de Benchmark:** Apoio no desenho e formatação dos cenários de teste automatizados em escala ($N = 100, 1.000, 10.000$) e modelagem dos perfis operacionais da Black Friday (Perfil A e Perfil B).
- **Apoio Redacional e Síntese de Slides:** Suporte na formatação das referências bibliográficas segundo a norma ABNT e na revisão textual das justificativas das decisões de projeto.

> *Nota: Toda a lógica algorítmica, arquitetura de classes, implementação das rotações em Java e validação matemática das propriedades foram rigorosamente conferidas, testadas e validadas pelo(a) autor(a).*

---

## 10. Referências Consultadas

1. **ADELSON-VELSKY, G. M.; LANDIS, E. M.** An algorithm for the organization of information. *Soviet Mathematics Doklady*, v. 3, p. 1259–1263, 1962.
2. **CORMEN, T. H.; LEISERSON, C. E.; RIVEST, R. L.; STEIN, C.** *Algoritmos: Teoria e Prática*. 3. ed. Rio de Janeiro: Elsevier, 2012. Capítulo 13: Árvores Rubro-Negras.
3. **WEISS, M. A.** *Data Structures and Algorithm Analysis in Java*. 3. ed. Boston: Pearson, 2012. Capítulo 4: Trees (AVL Trees).
4. **GOODRICH, M. T.; TAMASSIA, R.** *Estruturas de Dados e Algoritmos em Java*. 5. ed. Porto Alegre: Bookman, 2013.
5. **ORACLE CORPORATION.** *Class TreeMap\<K,V\> (Java Platform SE 17)*. Oracle Java Documentation, 2021. Disponível em: <https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/TreeMap.html>.
6. **GALLISTEIN, D.** *Red-Black Tree & AVL Tree Visualizer*. University of San Francisco (USFCA). Disponível em: <https://www.cs.usfca.edu/~galles/visualization/Algorithms.html>.
