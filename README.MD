# 📡 Servidor de Aplicações - Catálogo de Filmes

## 🧠 Visão Geral
Projeto de servidor cliente-servidor utilizando sockets em Java, com suporte a múltiplos clientes e comunicação baseada em JSON.

## Comandos para executar os códigos 

Exemplos:
```BASH Servidor
mvn exec:java -D exec.mainClass="ServidorAplicacao"
```

```BASH Cliente
mvn exec:java -D exec.mainClass="ClienteAplicacao"
```


---

## 🚀 Principais Evoluções

### 🔄 1. Migração de Protocolo
- Antes: mensagens em texto (`TIPO;param`)
- Agora: comunicação em **JSON estruturado**
  
Exemplo:
```JSON
{"tipo":"DETALHAR_FILME","id":1}
```

### 🧩 2. Padronização de Respostas
- Formato único de resposta:

```JSON
{
  "status": "OK",
  "mensagem": "Sucesso",
  "dados": {...}
}
```

### 🏗️ 3. Refatoração do CatalogoService
- Remoção de retorno em String
- Agora retorna objetos reais:
List<Filme>
Filme
- Permite serialização automática via Jackson

### ⚙️ 4. Protocolo Mais Robusto
- Validação de campos obrigatórios (tipo, id)
- Tratamento separado de erros:
- JSON inválido
- erro de regra de negócio
- erro interno

### 🔌 5. Cliente com Conexão Persistente
- Conexão aberta durante toda a sessão
- Múltiplas requisições sem reconectar
- Melhor desempenho e realismo

### 🎯 6. Interface do Cliente (UX)
- Menu baseado em números
- Usuário não precisa conhecer JSON
- JSON gerado internamente

### 📊 7. Exibição de Dados
- Respostas formatadas no cliente
- Lista de filmes exibida em tabela
- Melhor legibilidade no terminal

### 🌐 8. Preparação para Multiplataforma
- Comunicação via JSON permite:
- Cliente Java ✔
- Cliente Python (futuro) ✔

### 🧵 9. Controle de Conexões no Servidor
- Sessões persistentes
- Timeout para clientes inativos (Precisa ser melhorado)

### 🔮 Próximos Passos:
- Cliente em Python
