# Valkey POC with Micronaut Framework

A proof-of-concept application demonstrating Valkey (Redis-compatible) cache integration with Micronaut framework, including comprehensive observability, metrics, and monitoring capabilities.

## Features

- **Micronaut Framework**: Modern JVM-based framework with dependency injection and AOT compilation
- **Valkey Integration**: Redis-compatible cache using Lettuce client
- **RESTful API**: HTTP GET/PUT/DELETE operations for cache management
- **Observability**: Distributed tracing with Zipkin, metrics with Prometheus
- **Health Checks**: Built-in health indicators for Valkey connectivity
- **Monitoring**: Grafana dashboards for visualization
- **Docker Support**: Complete containerization with Docker Compose
- **Production Ready**: Separate configuration profiles and deployment options
- **Kotlin DSL**: Gradle build files using Kotlin DSL for type safety and IDE support

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client App    │───▶│  Micronaut API  │───▶│  Valkey Cache   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   Monitoring    │
                       │  (Prometheus,   │
                       │   Grafana,      │
                       │   Zipkin)       │
                       └─────────────────┘
```

## Prerequisites

- **Java 17+**
- **Docker & Docker Compose**
- **Gradle** (or use the included wrapper)

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/shesadri/valkey-poc.git
cd valkey-poc
```

### 2. Start Local Infrastructure

```bash
# Start Valkey and monitoring stack
docker-compose up -d

# Verify Valkey is running
docker-compose ps
```

### 3. Run the Application

```bash
# Using Gradle wrapper (Kotlin DSL)
./gradlew run

# Or run with development profile
./gradlew runLocal

# Or build and run JAR
./gradlew shadowJar
java -jar build/libs/valkey-poc-0.1-all.jar
```

### 4. Test the API

```bash
# Set a value
curl -X PUT http://localhost:8080/api/v1/cache/mykey \
  -H "Content-Type: text/plain" \
  -d "Hello, Valkey!"

# Get the value
curl http://localhost:8080/api/v1/cache/mykey

# Delete the value
curl -X DELETE http://localhost:8080/api/v1/cache/mykey
```

## Kotlin DSL Build Configuration

This project uses **Gradle Kotlin DSL** (`build.gradle.kts`) for enhanced type safety and IDE support:

### Key Benefits
- **Type Safety**: Compile-time checking of build script syntax
- **IDE Support**: Full IntelliJ/VS Code autocompletion and refactoring
- **Refactoring**: Safe renaming and code navigation
- **Modern Syntax**: Leverages Kotlin language features

### Build Tasks

```bash
# Standard tasks
./gradlew build                 # Build the project
./gradlew test                  # Run unit tests
./gradlew integrationTest       # Run integration tests
./gradlew shadowJar             # Create fat JAR

# Custom tasks
./gradlew runLocal              # Run with development profile
./gradlew dockerBuild           # Build Docker image
./gradlew dockerRun             # Build and run Docker container

# Gradle features
./gradlew --build-cache         # Use build cache
./gradlew --parallel            # Parallel execution
./gradlew --continuous test     # Continuous testing
```

### Build Configuration Highlights

```kotlin
// Enhanced Java compilation with Micronaut processing
tasks.named<JavaCompile>("compileJava") {
    options.compilerArgs.addAll(listOf(
        "-parameters",
        "-Amicronaut.processing.group=com.example",
        "-Amicronaut.processing.module=valkey-poc"
    ))
}

// Custom integration test task
tasks.register<Test>("integrationTest") {
    useJUnitPlatform {
        includeTags("integration")
    }
}

// Local development task
tasks.register<JavaExec>("runLocal") {
    mainClass.set("com.example.Application")
    jvmArgs = listOf("-Dmicronaut.environments=dev")
}
```

## API Documentation

### Endpoints

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/api/v1/cache/{keyId}` | Get value by key | 200: Value found<br>404: Key not found |
| PUT | `/api/v1/cache/{keyId}` | Set value for key | 201: Value set successfully |
| DELETE | `/api/v1/cache/{keyId}` | Delete key | 200: Deleted<br>404: Key not found |

### Management Endpoints

| Endpoint | Description |
|----------|-------------|
| `/health` | Application health status |
| `/metrics` | Prometheus metrics |
| `/info` | Application information |
| `/beans` | Spring beans information |
| `/env` | Environment properties |
| `/loggers` | Logging configuration |

### Example Usage

```bash
# Set a value with complex data
curl -X PUT "http://localhost:8080/api/v1/cache/session:user123" \
  -H "Content-Type: text/plain" \
  -d '{"userId": 123, "sessionId": "abc123", "expires": "2024-12-31T23:59:59Z"}'

# Get the value
curl "http://localhost:8080/api/v1/cache/session:user123"

# Check application health
curl "http://localhost:8080/health"

# View metrics
curl "http://localhost:8080/metrics"
```

## Local Development

### Development Setup

1. **Start Dependencies**:
   ```bash
   docker-compose up -d valkey
   ```

2. **Run Application in Dev Mode**:
   ```bash
   ./gradlew runLocal
   ```

3. **Access Services**:
   - Application: http://localhost:8080
   - Valkey Insight: http://localhost:8001
   - Health Check: http://localhost:8080/health
   - Metrics: http://localhost:8080/metrics

### Configuration

The application uses profile-based configuration:

- **Default Profile** (`application.yml`): Local development
- **Production Profile** (`application-prod.yml`): Production settings
- **Test Profile** (`application-test.yml`): Testing environment

#### Environment Variables

| Variable | Description | Default |
|----------|-------------|----------|
| `VALKEY_URL` | Valkey connection URI | `redis://localhost:6379` |
| `VALKEY_TIMEOUT` | Connection timeout | `30s` |
| `ZIPKIN_URL` | Zipkin server URL | `http://localhost:9411` |
| `MICRONAUT_ENVIRONMENTS` | Active profiles | `dev` |

### Development Features

- **Hot Reload**: Use `./gradlew run --continuous` for hot reloading
- **Debug Mode**: Enhanced logging with development profile
- **Build Cache**: Enabled for faster builds
- **Parallel Execution**: Gradle tasks run in parallel where possible

## Observability & Monitoring

### Metrics

The application exposes Prometheus metrics at `/metrics` including:

- **Custom Metrics**:
  - `cache_get_count` - Number of cache GET operations
  - `cache_get_time` - Time taken for GET operations
  - `cache_set_count` - Number of cache SET operations
  - `cache_set_time` - Time taken for SET operations
  - `cache_delete_count` - Number of cache DELETE operations
  - `cache_delete_time` - Time taken for DELETE operations

- **JVM Metrics**: Memory, GC, threads, etc.
- **HTTP Metrics**: Request counts, response times, status codes
- **Valkey Metrics**: Connection pool, operation counts

### Distributed Tracing

Zipkin integration provides distributed tracing:

- **Access Zipkin UI**: http://localhost:9411
- **Trace Context**: Automatic trace propagation across HTTP calls
- **Span Annotations**: Custom annotations for cache operations

### Health Checks

Comprehensive health checks available at `/health`:

```json
{
  "status": "UP",
  "details": {
    "valkey": {
      "status": "UP",
      "details": {
        "valkey.response": "PONG",
        "valkey.connected": true
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 250685575168,
        "free": 100123456789,
        "threshold": 10485760
      }
    }
  }
}
```

### Monitoring Stack

Start the complete monitoring stack:

```bash
docker-compose up -d
```

**Access Points**:
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Zipkin**: http://localhost:9411
- **Valkey Insight**: http://localhost:8001

#### Grafana Dashboards

Pre-configured dashboards include:
- Application performance metrics
- JVM monitoring
- Valkey cache statistics
- HTTP request analytics

## Production Deployment

### Using Docker

1. **Build Application Image**:
   ```bash
   # Using Gradle task
   ./gradlew dockerBuild
   
   # Or manually
   docker build -t valkey-poc:latest .
   ```

2. **Run with Production Profile**:
   ```bash
   docker run -d \
     --name valkey-poc \
     -p 8080:8080 \
     -e MICRONAUT_ENVIRONMENTS=prod \
     -e VALKEY_URL=redis://your-valkey-server:6379 \
     -e ZIPKIN_URL=http://your-zipkin-server:9411 \
     valkey-poc:latest
   ```

### Google Cloud Deployment

For Google Cloud with managed Valkey (MemoryStore):

1. **Set Environment Variables**:
   ```bash
   export VALKEY_URL="redis://your-memorystore-ip:6379"
   export MICRONAUT_ENVIRONMENTS="prod"
   ```

2. **Deploy to Cloud Run**:
   ```bash
   gcloud run deploy valkey-poc \
     --image gcr.io/PROJECT_ID/valkey-poc:latest \
     --platform managed \
     --region us-central1 \
     --set-env-vars="VALKEY_URL=${VALKEY_URL},MICRONAUT_ENVIRONMENTS=prod"
   ```

### Kubernetes Deployment

Example Kubernetes manifests:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: valkey-poc
spec:
  replicas: 3
  selector:
    matchLabels:
      app: valkey-poc
  template:
    metadata:
      labels:
        app: valkey-poc
    spec:
      containers:
      - name: valkey-poc
        image: valkey-poc:latest
        ports:
        - containerPort: 8080
        env:
        - name: MICRONAUT_ENVIRONMENTS
          value: "prod"
        - name: VALKEY_URL
          valueFrom:
            secretKeyRef:
              name: valkey-secret
              key: url
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

## Performance Tuning

### JVM Options

```bash
# Production JVM settings
java -server \
  -Xms512m -Xmx1g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=100 \
  -XX:+UseStringDeduplication \
  -jar valkey-poc-0.1-all.jar
```

### Valkey Connection Pool

Tune connection pool settings in `application-prod.yml`:

```yaml
redis:
  pool:
    max-active: 20
    max-idle: 10
    min-idle: 5
    max-wait: 30s
```

## Testing

### Unit Tests

```bash
./gradlew test
```

### Integration Tests

```bash
./gradlew integrationTest
```

### Continuous Testing

```bash
./gradlew test --continuous
```

### Load Testing

Example using `ab` (Apache Bench):

```bash
# Set some test data first
for i in {1..100}; do
  curl -X PUT "http://localhost:8080/api/v1/cache/key$i" \
    -H "Content-Type: text/plain" \
    -d "value$i"
done

# Load test GET operations
ab -n 10000 -c 100 "http://localhost:8080/api/v1/cache/key1"
```

## Troubleshooting

### Common Issues

1. **Valkey Connection Failed**:
   ```bash
   # Check Valkey status
   docker-compose ps valkey
   
   # View Valkey logs
   docker-compose logs valkey
   
   # Test connection
   docker exec -it valkey-poc valkey-cli ping
   ```

2. **Application Health Check Failing**:
   ```bash
   # Check application logs
   docker-compose logs app
   
   # Verify health endpoint
   curl -v http://localhost:8080/health
   ```

3. **Gradle Build Issues**:
   ```bash
   # Clean and rebuild
   ./gradlew clean build
   
   # Check Gradle daemon
   ./gradlew --status
   
   # Stop daemon if needed
   ./gradlew --stop
   ```

### Debugging

Enable debug logging:

```yaml
logging:
  level:
    com.example: DEBUG
    io.lettuce: DEBUG
    io.micronaut: DEBUG
```

Or use development logging configuration:

```bash
./gradlew runLocal
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Run `./gradlew check` to verify all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Resources

- [Micronaut Documentation](https://docs.micronaut.io/)
- [Valkey Documentation](https://valkey.io/)
- [Lettuce Redis Client](https://lettuce.io/)
- [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [Prometheus Metrics](https://prometheus.io/)
- [Zipkin Tracing](https://zipkin.io/)
- [Grafana Dashboards](https://grafana.com/)
