# atv3_WebIII

Sistema de microserviço para gestão de auto peças e manutenção veicular, desenvolvido com Spring Boot, Spring Security e HATEOAS.

## 🚀 Como Executar

### Pré-requisitos
- Java 21
- Maven (ou use o wrapper incluído)

### Execução
```bash
cd automanager
.\mvnw spring-boot:run
```

O sistema estará disponível em: `http://localhost:8080`

## 🔐 Autenticação

O sistema utiliza **HTTP Basic Authentication** com e-mail e senha.

### Usuários de Teste Criados Automaticamente:

| Perfil | E-mail | Senha |
|--------|--------|-------|
| Administrador | admin@carservice.com.br | admin123 |
| Funcionário | funcionario@carservice.com.br | func123 |
| Fornecedor | fornecedor@lojadocarro.com.br | forn123 |
| Cliente | cliente@email.com.br | cli123 |

### Rotas Públicas (sem autenticação):
- `GET /mercadorias` - Listar todas as mercadorias
- `GET /mercadorias/{id}` - Buscar mercadoria específica
- `GET /servicos` - Listar todos os serviços
- `GET /servicos/{id}` - Buscar serviço específico

### Rotas Protegidas (requer autenticação):
Todas as demais rotas requerem autenticação HTTP Basic.

## 🔗 Links HATEOAS

Todas as respostas da API incluem links HATEOAS para navegação e descoberta de recursos. Os links disponíveis em cada resposta incluem:

### Links Básicos (em todas as entidades):
- **self** - Link para o próprio recurso
- **create** - Link para criar novo recurso
- **update** - Link para atualizar o recurso
- **delete** - Link para deletar o recurso
- **list** - Link para listar todos os recursos da entidade

### Links Específicos por Entidade:

#### Usuários (`/usuarios`):
- **usuarios.veiculos** - Vincular veículo ao usuário
- **usuarios.mercadorias** - Vincular mercadoria ao usuário
- **usuarios.vendas** - Vincular venda ao usuário

#### Empresas (`/empresas`):
- **empresas.usuarios** - Vincular usuário à empresa
- **empresas.mercadorias** - Vincular mercadoria à empresa
- **empresas.servicos** - Vincular serviço à empresa
- **empresas.vendas** - Vincular venda à empresa

## 📋 Endpoints da API

### 🚗 Veículos (`/veiculos`)
```bash
# Listar todos os veículos
GET /veiculos

# Buscar veículo específico
GET /veiculos/{id}

# Criar novo veículo
POST /veiculos
Content-Type: application/json

# Atualizar veículo
PUT /veiculos/{id}
Content-Type: application/json

# Deletar veículo
DELETE /veiculos/{id}
```

**Exemplo de criação de veículo:**
```json
{
  "tipo": "SUV",
  "modelo": "Corolla Cross",
  "placa": "ABC-1234",
  "empresa": {
    "id": 1
  }
}
```

### 📦 Mercadorias (`/mercadorias`)
```bash
# Listar todas as mercadorias (PÚBLICO)
GET /mercadorias

# Buscar mercadoria específica (PÚBLICO)
GET /mercadorias/{id}

# Criar nova mercadoria (PROTEGIDO)
POST /mercadorias
Content-Type: application/json

# Atualizar mercadoria (PROTEGIDO)
PUT /mercadorias/{id}
Content-Type: application/json

# Deletar mercadoria (PROTEGIDO)
DELETE /mercadorias/{id}
```

**Exemplo de criação de mercadoria:**
```json
{
  "nome": "Óleo de Motor 5W30",
  "quantidade": 50,
  "valor": 45.0,
  "descricao": "Óleo de motor sintético 5W30 para motores modernos",
  "validade": "2025-12-31",
  "fabricacao": "2024-01-01",
  "cadastro": "2024-01-01",
  "empresa": {
    "id": 1
  }
}
```

### 🔧 Serviços (`/servicos`)
```bash
# Listar todos os serviços (PÚBLICO)
GET /servicos

# Buscar serviço específico (PÚBLICO)
GET /servicos/{id}

# Criar novo serviço (PROTEGIDO)
POST /servicos
Content-Type: application/json

# Atualizar serviço (PROTEGIDO)
PUT /servicos/{id}
Content-Type: application/json

# Deletar serviço (PROTEGIDO)
DELETE /servicos/{id}
```

**Exemplo de criação de serviço:**
```json
{
  "nome": "Troca de Óleo",
  "valor": 80.0,
  "descricao": "Troca completa de óleo e filtro",
  "empresa": {
    "id": 1
  }
}
```

### 👥 Usuários (`/usuarios`)
```bash
# Listar todos os usuários
GET /usuarios

# Buscar usuário específico
GET /usuarios/{id}

# Criar novo usuário
POST /usuarios
Content-Type: application/json

# Atualizar usuário
PUT /usuarios/{id}
Content-Type: application/json

# Deletar usuário
DELETE /usuarios/{id}
```

**Exemplo de criação de usuário:**
```json
{
  "nome": "João Pedro Silva",
  "nomeSocial": "João",
  "email": "joao@email.com",
  "perfis": ["FUNCIONARIO"],
  "telefones": [
    {
      "ddd": "11",
      "numero": "987654321"
    }
  ],
  "endereco": {
    "estado": "São Paulo",
    "cidade": "São Paulo",
    "bairro": "Centro",
    "rua": "Rua das Flores",
    "numero": "123",
    "codigoPostal": "01234-567"
  },
  "documentos": [
    {
      "tipo": "CPF",
      "numero": "54545454544554"
    }
  ],
  "credenciais": [
    {
      "email": "joao@email.com",
      "senha": "senhas123",
      "criacao": "2024-01-01",
      "ultimoAcesso": "2024-01-01",
      "inativo": false
    }
  ],
  "empresa": {
    "id": 1
  }
}
```

#### 🔗 Rotas de Vinculação de Usuários
```bash
# Vincular veículo a usuário
POST /usuarios/{id}/veiculos
Content-Type: application/json
{
  "id": 1
}

# Desvincular veículo de usuário
DELETE /usuarios/{id}/veiculos/{veiculoId}

# Vincular mercadoria a fornecedor
POST /usuarios/{id}/mercadorias
Content-Type: application/json
{
  "id": 1
}

# Desvincular mercadoria de fornecedor
DELETE /usuarios/{id}/mercadorias/{mercadoriaId}

# Vincular venda a funcionário/gerente
POST /usuarios/{id}/vendas
Content-Type: application/json
{
  "id": 1
}

# Desvincular venda de funcionário/gerente
DELETE /usuarios/{id}/vendas/{vendaId}
```

### 💰 Vendas (`/vendas`)
Obs: Para fins de teste, somente a venda não é inicializada ao subir a aplicação

```bash
# Listar todas as vendas
GET /vendas

# Buscar venda específica
GET /vendas/{id}

# Criar nova venda
POST /vendas
Content-Type: application/json

# Deletar venda (apenas ADMINISTRADOR ou GERENTE)
DELETE /vendas/{id}
```

**Exemplo de criação de venda:**
```json
{
  "identificacao": "VDA-2024-001",
  "cadastro": "2024-01-01",
  "cliente": {
    "id": 1
  },
  "funcionario": {
    "id": 2
  },
  "veiculo": {
    "id": 1
  },
  "empresa": {
    "id": 1
  },
  "mercadorias": [
    {
      "id": 1
    }
  ],
  "servicos": [
    {
      "id": 1
    }
  ]
}
```

### 🏢 Empresas (`/empresas`)
```bash
# Listar todas as empresas
GET /empresas

# Buscar empresa específica
GET /empresas/{id}

# Criar nova empresa
POST /empresas
Content-Type: application/json

# Atualizar empresa
PUT /empresas/{id}
Content-Type: application/json

# Deletar empresa
DELETE /empresas/{id}
```

**Exemplo de criação de empresa:**
```json
{
  "razaoSocial": "Auto Center São José Ltda",
  "nomeFantasia": "Auto Center SJ",
  "cadastro": "2024-07-01T00:00:00.000Z",
  "telefones": [
    { "ddd": "12", "numero": "39451234" }
  ],
  "emails": [
    { "email": "contato@autocentersj.com.br" }
  ],
  "endereco": {
    "estado": "São Paulo",
    "cidade": "São José dos Campos",
    "bairro": "Centro",
    "rua": "Av. 9 de Julho",
    "numero": "1000",
    "codigoPostal": "12211-000",
    "informacoesAdicionais": "Próximo ao shopping"
  }
}
```

#### 🔗 Rotas de Vinculação de Empresas
```bash
# Vincular usuário a empresa
POST /empresas/{id}/usuarios
Content-Type: application/json
{
  "id": 1
}

# Desvincular usuário de empresa
DELETE /empresas/{id}/usuarios/{usuarioId}

# Vincular mercadoria a empresa
POST /empresas/{id}/mercadorias
Content-Type: application/json
{
  "id": 1
}

# Desvincular mercadoria de empresa
DELETE /empresas/{id}/mercadorias/{mercadoriaId}

# Vincular serviço a empresa
POST /empresas/{id}/servicos
Content-Type: application/json
{
  "id": 1
}

# Desvincular serviço de empresa
DELETE /empresas/{id}/servicos/{servicoId}

# Vincular venda a empresa
POST /empresas/{id}/vendas
Content-Type: application/json
{
  "id": 1
}

# Desvincular venda de empresa
DELETE /empresas/{id}/vendas/{vendaId}
```

## 🔧 Testando com Insomnia/Postman

### Configuração de Autenticação:
1. Selecione **Basic Auth** no tipo de autenticação
2. Username: `admin@carservice.com.br`
3. Password: `admin123`

### Exemplo de Requisição:
```bash
GET http://localhost:8080/usuarios
Authorization: Basic YWRtaW5AY2Fyc2VydmljZS5jb20uYnI6YWRtaW4xMjM=
```

## 📊 Perfis de Usuário

- **ADMINISTRADOR**: Acesso total ao sistema, pode deletar vendas
- **GERENTE**: Acesso gerencial, pode deletar vendas
- **FUNCIONARIO**: Acesso operacional, pode vincular vendas
- **FORNECEDOR**: Acesso para fornecedores, pode vincular mercadorias
- **CLIENTE**: Acesso limitado para clientes

## 🗄️ Banco de Dados

O sistema utiliza **H2 Database** em memória, que é inicializado automaticamente com dados de teste.

## 🔄 Sistema de Backup Histórico

O sistema implementa um **sistema de backup histórico** para preservar a integridade dos dados de vendas mesmo quando entidades relacionadas são excluídas:

### Como Funciona:
1. **Backup Automático**: Quando uma venda é criada, o sistema automaticamente faz backup dos dados das entidades relacionadas
2. **Backup na Exclusão**: Quando uma entidade (usuário, veículo, empresa, mercadoria, serviço) é excluída, o sistema:
   - Busca todas as vendas relacionadas
   - Faz backup dos dados da entidade que está sendo excluída
   - Remove o relacionamento ativo
   - Preserva os dados históricos na venda

### Dados Preservados:
- **Cliente**: ID, nome, email
- **Funcionário**: ID, nome, email  
- **Veículo**: ID, tipo, modelo, placa
- **Empresa**: ID, razão social, nome fantasia
- **Mercadorias**: ID, nome, valor, descrição
- **Serviços**: ID, nome, valor, descrição

### Estrutura do Backup:
```json
{
  "backup": {
    "cliente": {
      "id": 1,
      "nome": "João Silva",
      "email": "joao@email.com"
    },
    "funcionario": {
      "id": 2,
      "nome": "Pedro Santos", 
      "email": "pedro@email.com"
    },
    "veiculo": {
      "id": 1,
      "tipo": "SEDAN",
      "modelo": "Honda Civic",
      "placa": "ABC-1234"
    },
    "empresa": {
      "id": 1,
      "razaoSocial": "Auto Center Ltda",
      "nomeFantasia": "Auto Center"
    },
    "mercadorias": [
      {
        "id": 1,
        "nome": "Óleo de Motor",
        "valor": 45.0,
        "descricao": "Óleo sintético"
      }
    ],
    "servicos": [
      {
        "id": 1,
        "nome": "Troca de Óleo",
        "valor": 80.0,
        "descricao": "Troca completa"
      }
    ]
  }
}
```

### Vantagens:
- **Integridade Histórica**: Vendas permanecem intactas mesmo após exclusão de entidades relacionadas
- **Auditoria**: Mantém registro completo de todas as transações
- **Flexibilidade**: Permite exclusão de entidades sem quebrar o histórico de vendas
- **Transparência**: Dados originais sempre disponíveis no campo `backup`

## 📝 Notas Importantes

### 🔄 Nova Arquitetura de Referências:

**Entidades Independentes:**
- **Veículos**: Apenas tipo, modelo e placa
- **Mercadorias**: Informações do produto
- **Serviços**: Nome, valor e descrição

**Entidades com Referências:**
- **Usuários**: Podem referenciar veículos (proprietário) e mercadorias (fornecedor)
- **Vendas**: Referenciam cliente, funcionário, veículo, empresa, mercadorias e serviços
- **Empresas**: Referenciam usuários, mercadorias, serviços e vendas

### 🔗 Rotas de Vinculação:

**Para Usuários:**
- `POST /usuarios/{id}/veiculos` - Vincular veículo 
- `POST /usuarios/{id}/mercadorias` - Vincular mercadoria 
- `POST /usuarios/{id}/vendas` - Vincular venda 

**Para Empresas:**
- `POST /empresas/{id}/usuarios` - Vincular usuário
- `POST /empresas/{id}/mercadorias` - Vincular mercadoria
- `POST /empresas/{id}/servicos` - Vincular serviço
- `POST /empresas/{id}/vendas` - Vincular venda

### 📋 Como Funciona:
1. **Cadastre primeiro** as entidades independentes (veículos, mercadorias, serviços)
2. **Depois referencie** por ID nas entidades que fazem as relações
3. **Use as rotas de vinculação** para criar relacionamentos específicos
4. **Ao listar** entidades independentes, mostram apenas suas informações básicas
5. **Ao buscar** entidades com referências, mostram os vínculos completos
6. **O sistema preserva** automaticamente o histórico através do backup

## 🚀 Explore a API

1. Execute o sistema com `.\mvnw spring-boot:run`
2. Teste as rotas públicas primeiro
3. Use as credenciais fornecidas para testar rotas protegidas
4. Cadastre entidades independentes primeiro
5. Depois crie as relações usando IDs
6. Use as rotas de vinculação para criar relacionamentos específicos
7. Explore os links HATEOAS retornados nas respostas