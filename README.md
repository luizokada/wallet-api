# Wallet API

A REST API for digital wallet management and expense control, developed with Spring Boot 3.3.3 and Java 21.

## 📋 Features

- **User Management**: Create, update, list and delete users
- **Profile Picture**: Upload the user avatar to a Supabase Storage bucket
- **JWT Authentication**: Secure login system with JWT tokens
- **Digital Wallets**: Create and manage wallets per user
- **Expense Control**: Record, update and delete expenses
- **Expense Categories**: Organize expenses by categories
- **Reports**: Query expenses by period
- **Swagger Documentation**: Interactive interface for API testing

## 🛠️ Technologies Used

- **Java 21**
- **Spring Boot 3.3.3**
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Database
- **Flyway** - Database migration
- **JWT (Auth0)** - Authentication tokens
- **Swagger/OpenAPI** - API documentation
- **Lombok** - Boilerplate code reduction
- **Maven** - Dependency management
- **Docker Compose** - Environment containerization

## ⚙️ Environment Setup

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker and Docker Compose (for database)

### Environment Variables (.env)

Create a `.env` file in the project root with the following variables:

```env
# PostgreSQL Database Settings
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=wallet_db
POSTGRES_USER=wallet_user
POSTGRES_PASSWORD=wallet_password

# JWT Settings
JWT_SECRET=your_very_secure_jwt_secret_key_here
JWT_EXPIRATION=7

# Mail (Gmail SMTP) — MAIL_PASSWORD is a Google "App Password"
# (Google Account > Security > 2-Step Verification > App passwords)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-char-app-password

# Link sent in the password recovery email (front-end page)
PASSWORD_RESET_URL=http://localhost:5173/reset-password

# Google OAuth client (reserved for a future Gmail API flow — not used yet)
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret

# Supabase Storage (user avatar) — keys come from Storage > S3 Connection
SUPABASE_BUCKET_NAME=wallet
SUPABASE_BUCKET_URL=https://your-project.supabase.co/storage/v1/object/public/
SUPABASE_BUCKET_ACCESS_KEY=your-s3-access-key
SUPABASE_BUCKET_SECRET_KEY=your-s3-secret-key
SUPABASE_S3_ENDPOINT=https://your-project.supabase.co/storage/v1/s3
SUPABASE_S3_REGION=us-east-1
```

### Variable Description

| Variable                     | Description                              | Example                                                    |
| ---------------------------- | ---------------------------------------- | ---------------------------------------------------------- |
| `POSTGRES_HOST`              | PostgreSQL database host                 | `localhost`                                                |
| `POSTGRES_PORT`              | PostgreSQL database port                 | `5432`                                                     |
| `POSTGRES_DB`                | Database name                            | `wallet_db`                                                |
| `POSTGRES_USER`              | Database user                            | `wallet_user`                                              |
| `POSTGRES_PASSWORD`          | Database password                        | `wallet_password`                                          |
| `JWT_SECRET`                 | Secret key to sign JWT tokens            | `my_super_secret_key_123`                                  |
| `JWT_EXPIRATION`             | Token expiration time in days            | `7`                                                        |
| `SUPABASE_BUCKET_NAME`       | Storage bucket that holds the avatars    | `wallet`                                                   |
| `SUPABASE_BUCKET_URL`        | Public base URL of the bucket            | `https://xxx.supabase.co/storage/v1/object/public/`        |
| `SUPABASE_BUCKET_ACCESS_KEY` | S3 access key (Storage > S3 Connection)  | `314effb8...`                                              |
| `SUPABASE_BUCKET_SECRET_KEY` | S3 secret key (Storage > S3 Connection)  | `a65c4ce8...`                                              |
| `SUPABASE_S3_ENDPOINT`       | S3-compatible endpoint of the project    | `https://xxx.supabase.co/storage/v1/s3`                    |
| `SUPABASE_S3_REGION`         | Project region, used to sign the request | `us-east-1`                                                |

## 🚀 How to Run the Project

### 1. Clone the repository

```bash
git clone <repository-url>
cd wallet-api
```

### 2. Configure the .env file

Create a `.env` file in the project root with the variables shown in the previous section:

```bash
# Create the .env file
touch .env

# Edit the file and add the environment variables
nano .env  # or use your preferred editor
```

### 3. Start the database with Docker

```bash
docker-compose up -d
```

### 4. Run the application with Maven

#### Option A: Using Maven Wrapper (Recommended)

```bash
# Linux/Mac
mvn spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

#### Option B: Using globally installed Maven

```bash
mvn spring-boot:run
```

### 5. Access the application

- **API Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html/`

## 📊 Database Structure

The project uses Flyway for database versioning. Migrations are executed automatically on startup.

### Main Entities:

- **Users**: System users
- **Wallets**: User wallets
- **Expenses**: Recorded expenses
- **Expense_Categories**: Expense categories

## 🔗 API Endpoints

### Authentication

- `POST /login` - Login and get JWT token

### Users

- `POST /user/create-user` - Create new user
- `GET /user/me` - Get logged user data
- `GET /user` - List all users
- `PATCH /user/{id}` - Update user
- `POST /user/{id}/avatar` - Upload profile picture (multipart field `file`, JPEG/PNG/WEBP up to 2 MB)
- `DELETE /user/{id}/avatar` - Remove profile picture
- `DELETE /user/{id}` - Delete user

### Wallets

- `POST /wallet` - Create wallet
- `POST /wallet/{id}` - Get wallet with expenses by period
- `PATCH /wallet/{id}` - Update wallet balance

### Expenses

- `POST /expense` - Create new expense
- `GET /expense/{id}` - Get expense by ID
- `PATCH /expense/{id}` - Update expense
- `DELETE /expense/{id}` - Delete expense

### Expense Categories

- `POST /expense-category` - Create category
- `GET /expense-category` - List categories
- `PATCH /expense-category/{id}` - Update category

## 🔧 Useful Maven Commands

```bash
# Compile the project
mvn compile

# Run tests
mvn test

# Generate JAR package
mvn package

# Clean and compile
mvn clean compile

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Skip tests during build
mvn package -DskipTests
```

## 🐳 Docker

The project is fully containerized and can be run using Docker and Docker Compose, including both the PostgreSQL database and the Spring Boot application.

### Docker Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+

### Initial Setup

1. **Create the `.env` file** in the project root:

```bash
# Copy and paste in terminal to create the .env file
cat > .env << 'EOF'
# PostgreSQL Database Settings
POSTGRES_HOST=postgres
POSTGRES_PORT=5432
POSTGRES_DB=wallet_db
POSTGRES_USER=wallet_user
POSTGRES_PASSWORD=wallet_password

# JWT Settings
JWT_SECRET=your_very_secure_jwt_secret_key_here_change_this
JWT_EXPIRATION=7

# Mail (Gmail SMTP) — use a Google "App Password", not your account password
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-char-app-password
PASSWORD_RESET_URL=http://localhost:5173/reset-password

# Google OAuth client (reserved for a future Gmail API flow — not used yet)
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret

# Supabase Storage (user avatar) — keys come from Storage > S3 Connection
SUPABASE_BUCKET_NAME=wallet
SUPABASE_BUCKET_URL=https://your-project.supabase.co/storage/v1/object/public/
SUPABASE_BUCKET_ACCESS_KEY=your-s3-access-key
SUPABASE_BUCKET_SECRET_KEY=your-s3-secret-key
SUPABASE_S3_ENDPOINT=https://your-project.supabase.co/storage/v1/s3
SUPABASE_S3_REGION=us-east-1
EOF
```

### Run Complete Project (Recommended)

```bash
# Build and run all services (database + API)
docker-compose up --build

# Or run in background (detached mode)
docker-compose up --build -d
```

This command will:

- Build the application Docker image
- Start the PostgreSQL container
- Wait for the database to become healthy (healthcheck)
- Start the Spring Boot application
- Automatically connect the API to the database

### Run Services Individually

```bash
# Run only the database
docker-compose up postgres -d

# Run only the API (make sure the database is running)
docker-compose up wallet-api

# Run the API in background
docker-compose up wallet-api -d
```

### Useful Commands

```bash
# View logs of all services
docker-compose logs

# View logs only of the API
docker-compose logs wallet-api

# View logs only of the database
docker-compose logs postgres

# View logs in real time (follow)
docker-compose logs -f wallet-api

# Check containers status
docker-compose ps

# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes database data)
docker-compose down -v

# Rebuild only the API (after code changes)
docker-compose build wallet-api
docker-compose up wallet-api -d

# Enter the API container for debugging
docker-compose exec wallet-api sh

# Enter the PostgreSQL container
docker-compose exec postgres psql -U wallet_user -d wallet_db
```

### Access the Application

After running `docker-compose up --build`, the application will be available at:

- **API Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **PostgreSQL**: `localhost:5432` (for external connections)

### Docker Structure

The project uses:

- **Multi-stage Dockerfile**: Optimized with Alpine Linux for smaller images
- **Docker Compose**: Orchestrates database and application with dependencies
- **Health Checks**: Ensures database is ready before starting the API
- **Persistent volumes**: PostgreSQL data is maintained between restarts
- **Isolated network**: Secure communication between containers

### Docker Troubleshooting

```bash
# If the API doesn't connect to the database, check logs
docker-compose logs wallet-api

# Restart only the API
docker-compose restart wallet-api

# Clear Docker cache (if there are build issues)
docker system prune -a

# Check if environment variables are correct
docker-compose config
```

### Development with Docker

For active development, you can:

1. **Use only the database via Docker**:

```bash
docker-compose up postgres -d
```

2. **Run the application locally**:

```bash
mvn spring-boot:run
```

This allows hot reload during development while keeping the database containerized.

## 🔒 Security

- The API uses JWT for authentication
- Public endpoints: `/login`, `/user/create-user`, `/swagger-ui/**`, `/api-docs`
- All other endpoints require authentication
- Passwords are encrypted with BCrypt

## 📝 Development

### Project Structure

```
src/main/java/wallet/api/
├── controller/          # REST Controllers
├── domain/             # Entities and business logic
│   ├── auth/          # Authentication
│   ├── expense/       # Expenses
│   ├── expenseCategory/ # Categories
│   ├── user/          # Users
│   └── wallet/        # Wallets
├── errors/            # Exception handling
└── infra/             # Infrastructure configurations
    ├── config/        # Spring configurations
    ├── jwt/           # JWT services
    └── security/      # Security configurations
```

### Hot Reload

The project is configured with Spring Boot DevTools for automatic restart during development (only when running locally).

## 🤝 Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
