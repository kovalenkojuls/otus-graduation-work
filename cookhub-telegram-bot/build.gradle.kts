plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "ru.kovalenkojuls.telegrambot"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}


dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-devtools")

    // https://github.com/FasterXML/jackson-modules-base/discussions/239#discussioncomment-6526838
    implementation("javax.xml.bind:jaxb-api:2.3.1")

    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql")

    // Telegram Bot
    implementation("org.telegram:telegrambots:6.3.0")

    //Lombok
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}