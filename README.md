# Amazon Engineering Challenge: Árvores Balanceadas (AVL e Rubro-Negra)

Trabalho prático da Unidade II da disciplina de Estruturas de Dados Básicas II (IMD0039) - UFRN.

O objetivo do projeto é a implementação em Java de duas árvores balanceadas (AVL e Rubro-Negra) para gerenciar identificadores de encomendas no contexto de alto volume da Black Friday.

---

## 1. Identificação do Autor

- **Nome:** Kaio Victor Alves da Rocha
- **Matrícula:** 20240031116
- **Disciplina:** IMD0039 - Estruturas de Dados Básicas II

---

## 2. Descrição das Funcionalidades e Exemplos de Uso

O sistema permite gerenciar chaves numéricas de pacotes utilizando as estruturas AVL ou Rubro-Negra, implementadas sem bibliotecas externas de balanceamento.

### Funcionalidades:
- **Inserção:** adiciona chaves individualmente ou em lote via array.
- **Busca:** localiza a chave na árvore em tempo $O(\log N)$.
- **Remoção:** remove o elemento mantendo as regras de balanceamento da árvore.
- **Visualização Gráfica:** exibe no terminal a hierarquia da árvore em formato textual, com altura e fator de balanceamento (na AVL) e identificação de cores (na Rubro-Negra).
- **Métricas:** contadores de rotações (simples à esquerda, simples à direita e duplas) e cálculo de altura.
- **Validação de Propriedades:** rotina interna que checa se as propriedades da árvore continuam válidas após cada modificação.

### Exemplos de Uso:

**Visualização no terminal (AVL):**
```text
Estrutura da Árvore AVL (Chave [h=Altura, FB=Fator de Balanceamento]):
└── [RAIZ] 30 (h=3, FB=0)
    ├── [E] 20 (h=2, FB=0)
    │   ├── [E] 10 (h=1, FB=0)
    │   └── [D] 25 (h=1, FB=0)
    └── [D] 40 (h=2, FB=0)
        ├── [E] 35 (h=1, FB=0)
        └── [D] 50 (h=1, FB=0)
```

**Visualização no terminal (Rubro-Negra):**
```text
Estrutura da Árvore Rubro-Negra:
└── [RAIZ] 20 (PRETO)
    ├── [E] 10 (PRETO)
    └── [D] 40 (VERMELHO)
        ├── [E] 30 (PRETO)
        │   └── [E] 25 (VERMELHO)
        └── [D] 50 (PRETO)
```

**Busca e Remoção:**
```text
Digite o ID do pacote para busca: 25
-> Encomenda ID 25 encontrada no sistema!

Digite o ID do pacote para remoção: 20
-> Encomenda ID 20 removida com sucesso.
```

---

## 3. Instruções de Execução e Dependências

### Dependências
- **Java JDK:** versão 11 ou superior (desenvolvido em Java padrão, sem dependências externas).

### Como Executar:

**Opção 1: Executar o arquivo `.jar` (Recomendado)**
```bash
java -jar AmazonTrees.jar
```

**Opção 2: Executar pelas classes compiladas na pasta `bin/`**
```bash
java -cp bin Main
```

**Opção 3: Recompilar o código-fonte manualmente**
```bash
# No Linux ou macOS:
mkdir -p bin
javac -d bin src/trees/*.java src/benchmark/*.java src/Main.java
java -cp bin Main

# No Windows:
mkdir bin
javac -d bin src\trees\*.java src\benchmark\*.java src\Main.java
java -cp bin Main
```

---

## 4. Comentários sobre o Funcionamento

### Árvore AVL
A árvore mantém o fator de balanceamento ($FB = h_{esq} - h_{dir}$) de cada nó estritamente no intervalo $\{-1, 0, 1\}$. Caso uma inserção ou remoção viole esse critério, são aplicadas rotações simples (LL ou RR) ou duplas (LR ou RL) para restaurar o equilíbrio.

### Árvore Rubro-Negra
A árvore segue a abordagem clássica de Cormen et al. (CLRS), utilizando ponteiro para o nó pai (`parent`), nós sentinela `NIL` e nós coloridos de vermelho ou preto. O balanceamento é restaurado através de recoloração (caso o tio seja vermelho) ou rotações acompanhadas de troca de cores (caso o tio seja preto).

### Decisões de Implementação:
1. **Elementos Duplicados:** a tentativa de inserção de chave já existente retorna `false` e a árvore não é modificada. Justificativa: cada pacote na Amazon possui um ID de rastreamento único.
2. **Remoção de Elementos Inexistentes:** a busca não encontra o elemento e retorna `false` de forma segura, sem disparar rotações desnecessárias.
3. **Remoção de Nó com Dois Filhos:** o nó a ser removido é substituído pelo seu **sucessor em ordem** (o menor elemento da subárvore direita), mantendo a integridade da ordem da árvore de busca.

---

## 5. Estrutura do Repositório

```text
ARVORESBALANCEADAS-EDB2/
├── src/                  # Código-fonte completo (.java)
│   ├── Main.java         # Ponto de entrada e menu interativo
│   ├── trees/            # Implementações das árvores (AVL e Rubro-Negra)
│   └── benchmark/        # Validador e testes automatizados
├── bin/                  # Versão compilada das classes (.class)
├── AmazonTrees.jar       # Versão compilada executável (.jar)
└── README.md             # Documentação do projeto
```
