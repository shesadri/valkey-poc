plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.4.2"
    id("io.micronaut.aot") version "4.4.2"
}

version = "0.1"
group = "com.example"

repositories {
    mavenCentral()
}

dependencies {
    // Micronaut BOM for version management
    implementation(platform("io.micronaut.platform:micronaut-platform:4.4.2"))
    
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    
    // Redis/Valkey client
    implementation("io.micronaut.redis:micronaut-redis-lettuce")
    
    // Observability and Metrics
    implementation("io.micronaut.micrometer:micronaut-micrometer-core")
    implementation("io.micronaut.micrometer:micronaut-micrometer-registry-prometheus")
    implementation("io.micronaut:micronaut-management")
    
    // Tracing - Updated for Micronaut 4.x
    implementation("io.micronaut.tracing:micronaut-tracing-brave")
    implementation("io.micronaut.tracing:micronaut-tracing-brave-http")
    
    // Validation - Updated for Micronaut 4.x
    implementation("io.micronaut.validation:micronaut-validation")
    
    // Logging
    implementation("ch.qos.logback:logback-classic")
    
    // OpenAPI/Swagger
    implementation("io.micronaut.openapi:micronaut-openapi")
    implementation("io.swagger.core.v3:swagger-annotations")
    
    runtimeOnly("org.yaml:snakeyaml")
    
    // Test dependencies
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

application {
    mainClass.set("com.example.Application")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

graalvmNative {
    toolchainDetection.set(false)
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
    aot {
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    systemProperty("redis.uri", System.getProperty("redis.uri", "redis://localhost:6379"))
}

tasks.named<JavaCompile>("compileJava") {
    options.compilerArgs.addAll(listOf(
        "-parameters",
        "-Amicronaut.processing.group=com.example",
        "-Amicronaut.processing.module=valkey-poc"
    ))
}

tasks.named<JavaCompile>("compileTestJava") {
    options.compilerArgs.addAll(listOf(
        "-parameters",
        "-Amicronaut.processing.group=com.example",
        "-Amicronaut.processing.module=valkey-poc"
    ))
}

// Custom task for integration tests
tasks.register<Test>("integrationTest") {
    description = "Runs integration tests"
    group = "verification"
    
    useJUnitPlatform {
        includeTags("integration")
    }
    
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    
    shouldRunAfter("test")
    
    systemProperty("redis.uri", System.getProperty("redis.uri", "redis://localhost:6379"))
}

// Task to run application locally
tasks.register<JavaExec>("runLocal") {
    description = "Run the application locally with development profile"
    group = "application"
    
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.example.Application")
    
    jvmArgs = listOf(
        "-Dmicronaut.environments=dev",
        "-Dlogback.configurationFile=src/main/resources/logback-dev.xml"
    )
}

// Docker build task
tasks.register<Exec>("dockerBuild") {
    description = "Build Docker image"
    group = "docker"
    
    dependsOn("shadowJar")
    
    commandLine("docker", "build", "-t", "valkey-poc:latest", ".")
}

// Docker run task
tasks.register<Exec>("dockerRun") {
    description = "Run Docker container"
    group = "docker"
    
    dependsOn("dockerBuild")
    
    commandLine("docker", "run", "-p", "8080:8080", "valkey-poc:latest")
}
