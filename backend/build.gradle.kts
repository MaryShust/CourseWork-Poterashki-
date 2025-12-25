import com.google.cloud.tools.jib.gradle.JibExtension
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    kotlin("jvm") version "2.1.21"
    `java-library`
    kotlin("plugin.spring") version "2.1.21" apply false
    id("org.springframework.boot") version "3.5.7" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.google.cloud.tools.jib") version "3.4.2" apply false
    id("org.openapi.generator") version "7.8.0" apply false
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
    apply(plugin="com.google.cloud.tools.jib")
    apply(plugin = "org.openapi.generator")
    apply(plugin = "application")

    // Общая конфигурация Jib для всех микросервисов
    plugins.withId("com.google.cloud.tools.jib") {
        extensions.configure(JibExtension::class.java) {
            from.image = "eclipse-temurin:17-jre-alpine"
            // Имя итогового образа: <имя_проекта>:local (совпадает с docker-compose)
            to.image = project.name
            to.tags = setOf("latest")

            // Для Spring Boot mainClass обычно определяется автоматически
            container.ports = listOf("8080")
            container.jvmFlags = listOf("-Xms256m", "-Xmx512m")
        }
    }

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
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0") // или актуальная 2.6.x


        // Kotlin
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        // Testing
        testImplementation("org.springframework.boot:spring-boot-starter-test")
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