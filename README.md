cat > README.md << 'EOF'
# Technostore

Application Spring Boot de gestion de produits technologiques.

## Technologies

- Spring Boot 3.x
- Thymeleaf
- MySQL
- H2 (dev)
- Docker

## Installation

### Prérequis
- Java 17+
- Maven 3.8+
- MySQL 8+ (ou Docker)

### Configuration

1. Copier le fichier d'exemple :
```bash
cp .env.example .env
```

2. Modifier les variables d'environnement dans `.env`

3. Lancer l'application :
```bash
./mvnw spring-boot:run
```

### Avec Docker
```bash
docker-compose up -d
```

## Accès

- Application : http://localhost:8080
- H2 Console : http://localhost:8080/h2-console
EOF

git add README.md
git commit -m "Add README with installation instructions"
