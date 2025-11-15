import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    kotlin("jvm") version "2.1.21"
    `java-library`
    kotlin("plugin.spring") version "2.1.21" apply false
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    id("com.maxim.docker-plugin") apply false
    id("com.maxim.docker-compose-plugin") apply false
}

allprojects {
    group = "com.maxim.poteryashki"
    version = "1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    // Применяем Spring Boot и Kotlin плагины ко всем подпроектам
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.maxim.docker-plugin")
    apply(plugin = "com.maxim.docker-compose-plugin")

    // Общая конфигурация Java для всех подпроектов
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    dependencies {
        // Spring Boot starters
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-actuator")

        // Kotlin
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        // Тестирование
        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    kotlin {
        jvmToolchain(21)
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    // Spring Boot конфигурация
    tasks.withType<BootJar> {
        enabled = true
    }

    tasks.withType<BootRun> {
        systemProperties(System.getProperties().mapKeys { it.key.toString() })
    }
}

// Альтернативный вариант - если хотите запускать dockerUp для всех подпроектов
val subprojectsUp = tasks.register("dockerUpAll") {
    dependsOn(subprojects.map { ":${it.name}:dockerUp" })
    group = "docker"
    description = "Starts all Docker containers for all subprojects"
}

val subprojectsDown = tasks.register("dockerDownAll") {
    dependsOn(subprojects.map { ":${it.name}:dockerDown" })
    group = "docker"
    description = "Stops all Docker containers for all subprojects"
}