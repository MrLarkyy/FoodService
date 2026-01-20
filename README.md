# Brand REST API (Kotlin AWS Lambda)

This is a reference project for a Kotlin-based AWS Lambda service using http4k and Exposed.

## Architecture
- **HTTP Layer**: Built with [http4k](https://www.http4k.org/). Uses Lenses for type-safe request parsing and `kotlinx.serialization` for JSON.
- **Service Layer**: Explicit dependency injection. No reflection or "magic" frameworks.
- **Persistence**: [Exposed DSL](https://github.com/JetBrains/Exposed) for type-safe SQL construction over PostgreSQL.
- **Database**: PostgreSQL
- **Compatibility**: Fully compatible with GraalVM Native Image.

## Configuration
The application uses environment variables for configuration. For local development, we use `.env` files managed by Gradle.

| Variable      | Description            | Default (Local)                             |
|:--------------|:-----------------------|:--------------------------------------------|
| `DB_URL`      | JDBC Connection String | `jdbc:postgresql://localhost:5432/postgres` |
| `DB_USER`     | Database Username      | `postgres`                                  |
| `DB_PASSWORD` | Database Password      | `password`                                  |


### Local Development
For convenience, local development uses the `co.uzzu.dotenv.gradle` plugin.
1. Copy `.env.example` to `.env`.
2. Fill in your local database credentials.
3. Run `./gradlew run`.

## Build & Deployment

### Local Execution
1. Ensure a PostgreSQL instance is running.
2. Set the environment variables listed above.
3. Run `./gradlew run` or execute the `main` function in `Main.kt`.
4. The API will be available at `http://localhost:8080/brands`.

### Local Testing
Execute the test suite (uses an H2 in-memory database):
`./gradlew test`

### GitHub Actions (Recommended)
This project is configured to build automatically via GitHub Actions. Every push to `main` generates two deployment artifacts:
1.  **Standard Lambda**: A Fat JAR optimized for Java 21 runtime.
2.  **Native Lambda**: A Linux binary built with GraalVM for ultra-fast cold starts.

### Manual AWS Lambda Deployment
If building manually, use the following Gradle tasks:

| Task                             | Output File                        | AWS Runtime                |
|:---------------------------------|:-----------------------------------|:---------------------------|
| `./gradlew buildLambdaZip`       | `build/dist/deployment.zip`        | Java 21                    |
| `./gradlew buildNativeLambdaZip` | `build/dist/native-deployment.zip` | Amazon Linux 2023 (Custom) |

#### Handler Configuration
Depending on your AWS trigger, use one of these handlers:
- **API Gateway (HTTP API v2)**: `gg.aquatic.foodservice.LambdaHandler`
- **Application Load Balancer (ALB)**: `gg.aquatic.foodservice.AlbLambdaHandler`

## API Specification
**Endpoint**: `GET /brands`

**Query Parameters:**
- `name` (Optional): Partial match, case-insensitive.
- `externalId` (Optional): Exact match.
- `sortBy` (Optional): `name` | `last_updated`.
- `sortDirection` (Optional): `asc` | `desc`.

**Example Request:**
`GET /brands?name=apple&sortBy=last_updated&sortDirection=desc`
