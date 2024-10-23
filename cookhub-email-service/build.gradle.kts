plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "ru.kovalenkojuls.emailservice"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starter
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // Spring Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // JSON Processing with Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // Spring Boot Email Support
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Lombok for reducing boilerplate code
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}