description = "gateway for backend"
extra["springCloudVersion"] = "2025.0.0"

// Настройка Jib: сборка в локальный Docker-демон через задачу jibDockerBuild
jib {
    from {
        image = "eclipse-temurin:21-jre-alpine"
    }
    to {
        // имя образа совпадает с docker-compose (service-a:local)
        image = "gateway"
        tags = setOf("latest")
    }
    container {
        // Для Spring Boot Jib сам найдёт main-класс
        ports = listOf("8080") // контейнерный порт
        jvmFlags = listOf("-Xms256m", "-Xmx512m")
        // Переменные БД прилетят из .env через docker-compose, здесь их не дублируем
    }
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webmvc")
    testImplementation("org.springframework.security:spring-security-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}