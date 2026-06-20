# Duke Free Food Finder — Backend

A Spring Boot REST API that powers Duke Free Food Finder, a website for Duke students to find and give away free food, reducing food insecurity and waste.

## Features

- **Passwordless auth** — Email a 6-digit verification code via [Resend](https://resend.com) restricted to `@duke.edu` addresses.
- **Food postings** — Title, description, location (lat/lon), location details, expiration time, and an image. User can look through a food list or a map. 
- **Forums** — Comment section attached to each food post.

## Tech Stack

| Topic | Technology |
|---|---|
| Language / Runtime | Java 24 |
| Framework | Spring Boot |
| Database | PostgreSQL / H2 |
| Auth | JWT |
| Email | Resend |
| Image storage | AWS S3 |
| Build | Maven |
| Deploy | Docker → GitHub Actions -> AWS  |

## Project Structure

```
src/main/java/com/joshualiu/dukefreefoodfinderbackend/
├── auth/      # verification code, JWT
├── food/      
├── forum/     
├── user/      
├── storage/   # S3
├── health/    # health check for AWS
└── config/    # CORS
```

## Getting Started

### Prerequisites

- Java 24
- Docker
- Resend and AWS account

### Environment Variables

Reads environment variables from `src/main/resources/application.properties`

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL (defaults to `jdbc:postgresql://localhost:5432/free_food_finder`) |
| `SPRING_DATASOURCE_USERNAME` | DB username (defaults to `duke_admin`) |
| `SPRING_DATASOURCE_PASSWORD` | DB password |
| `JWT_SECRET` ||
| `JWT_EXPIRATION` ||
| `RESEND_API_KEY` ||
| `S3_BUCKET_NAME` ||
| `S3_REGION` | AWS region |

`docker-compose.yml` Postgres service uses `DB_USERNAME`, `DB_PASSWORD`, and `DB_NAME`.

### Running Locally

1. Start PostgreSQL:

   ```bash
   docker compose up -d
   ```

2. Export environment variables

3. Run the app:

   ```bash
   ./mvnw spring-boot:run
   ```

API will be available at `http://localhost:8080`.

### Running Tests

```bash
./mvnw test
```

## Deployment

Pushes to `main` trigger the GitHub Actions pipeline (`.github/workflows/deploy.yml`):

1. Run the test suite.
2. Build the Docker image and push it to AWS ECR.
3. Force a new deployment of the ECS service `duke-free-food-finder-service` in the `duke-free-food-finder-cluster`.
