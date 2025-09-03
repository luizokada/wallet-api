# Wallet API

Uma API REST para gerenciamento de carteiras digitais e controle de despesas, desenvolvida com Spring Boot 3.3.3 e Java 21.

## 📋 Funcionalidades

- **Gestão de Usuários**: Criação, atualização, listagem e exclusão de usuários
- **Autenticação JWT**: Sistema de login seguro com tokens JWT
- **Carteiras Digitais**: Criação e gerenciamento de carteiras por usuário
- **Controle de Despesas**: Registro, atualização e exclusão de despesas
- **Categorias de Despesas**: Organização de despesas por categorias
- **Relatórios**: Consulta de despesas por período
- **Documentação Swagger**: Interface interativa para testes da API

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.3.3**
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados
- **Flyway** - Migração de banco de dados
- **JWT (Auth0)** - Tokens de autenticação
- **Swagger/OpenAPI** - Documentação da API
- **Lombok** - Redução de código boilerplate
- **Maven** - Gerenciamento de dependências
- **Docker Compose** - Containerização do ambiente

## ⚙️ Configuração do Ambiente

### Pré-requisitos

- Java 21 ou superior
- Maven 3.6+
- Docker e Docker Compose (para banco de dados)

### Variáveis de Ambiente (.env)

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
# Configurações do Banco de Dados PostgreSQL
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=wallet_db
POSTGRES_USER=wallet_user
POSTGRES_PASSWORD=wallet_password

# Configurações JWT
JWT_SECRET=sua_chave_secreta_jwt_muito_segura_aqui
JWT_EXPIRATION=7
```

### Descrição das Variáveis

| Variável            | Descrição                             | Exemplo                         |
| ------------------- | ------------------------------------- | ------------------------------- |
| `POSTGRES_HOST`     | Host do banco PostgreSQL              | `localhost`                     |
| `POSTGRES_PORT`     | Porta do banco PostgreSQL             | `5432`                          |
| `POSTGRES_DB`       | Nome do banco de dados                | `wallet_db`                     |
| `POSTGRES_USER`     | Usuário do banco                      | `wallet_user`                   |
| `POSTGRES_PASSWORD` | Senha do banco                        | `wallet_password`               |
| `JWT_SECRET`        | Chave secreta para assinar tokens JWT | `minha_chave_super_secreta_123` |
| `JWT_EXPIRATION`    | Tempo de expiração do token em dias   | `7`                             |

## 🚀 Como Executar o Projeto

### 1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd wallet-api
```

### 2. Configure o arquivo .env

Crie um arquivo `.env` na raiz do projeto com as variáveis mostradas na seção anterior:

```bash
# Crie o arquivo .env
touch .env

# Edite o arquivo e adicione as variáveis de ambiente
nano .env  # ou use seu editor preferido
```

### 3. Inicie o banco de dados com Docker

```bash
docker-compose up -d
```

### 4. Execute a aplicação com Maven

#### Opção A: Usando o Maven Wrapper (Recomendado)

```bash
# Linux/Mac
mvn spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

#### Opção B: Usando Maven instalado globalmente

```bash
mvn spring-boot:run
```

### 5. Acesse a aplicação

- **API Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html/`

## 📊 Estrutura do Banco de Dados

O projeto utiliza Flyway para versionamento do banco. As migrações são executadas automaticamente na inicialização.

### Principais Entidades:

- **Users**: Usuários do sistema
- **Wallets**: Carteiras dos usuários
- **Expenses**: Despesas registradas
- **Expense_Categories**: Categorias de despesas

## 🔗 Endpoints da API

### Autenticação

- `POST /login` - Fazer login e obter token JWT

### Usuários

- `POST /user/create-user` - Criar novo usuário
- `GET /user/me` - Obter dados do usuário logado
- `GET /user` - Listar todos os usuários
- `PATCH /user/{id}` - Atualizar usuário
- `DELETE /user/{id}` - Excluir usuário

### Carteiras

- `POST /wallet` - Criar carteira
- `POST /wallet/{id}` - Obter carteira com despesas por período
- `PATCH /wallet/{id}` - Atualizar saldo da carteira

### Despesas

- `POST /expense` - Criar nova despesa
- `GET /expense/{id}` - Obter despesa por ID
- `PATCH /expense/{id}` - Atualizar despesa
- `DELETE /expense/{id}` - Excluir despesa

### Categorias de Despesas

- `POST /expense-category` - Criar categoria
- `GET /expense-category` - Listar categorias
- `PATCH /expense-category/{id}` - Atualizar categoria

## 🔧 Comandos Maven Úteis

```bash
# Compilar o projeto
mvn compile

# Executar testes
mvn test

# Gerar pacote JAR
mvn package

# Limpar e compilar
mvn clean compile

# Executar com perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Pular testes durante o build
mvn package -DskipTests
```

## 🐳 Docker

### Executar apenas o banco de dados

```bash
docker-compose up postgres -d
```

### Parar os serviços

```bash
docker-compose down
```

### Ver logs do banco

```bash
docker-compose logs postgres
```

## 🔒 Segurança

- A API utiliza JWT para autenticação
- Endpoints públicos: `/login`, `/user/create-user`, `/swagger-ui/**`, `/api-docs`
- Todos os outros endpoints requerem autenticação
- Senhas são criptografadas com BCrypt

## 📝 Desenvolvimento

### Estrutura do Projeto

```
src/main/java/wallet/api/
├── controller/          # Controllers REST
├── domain/             # Entidades e lógica de negócio
│   ├── auth/          # Autenticação
│   ├── expense/       # Despesas
│   ├── expenseCategory/ # Categorias
│   ├── user/          # Usuários
│   └── wallet/        # Carteiras
├── errors/            # Tratamento de exceções
└── infra/             # Configurações de infraestrutura
    ├── config/        # Configurações Spring
    ├── jwt/           # Serviços JWT
    └── security/      # Configurações de segurança
```

### Hot Reload

O projeto está configurado com Spring Boot DevTools para reinicialização automática durante o desenvolvimento.

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request
