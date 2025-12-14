description = "gateway for backend"
extra["springCloudVersion"] = "2025.0.0"

kotlin {
    sourceSets {
        val main by getting {
            kotlin.srcDir("$buildDir/generated/openapi/src/main/kotlin")
        }
    }
}

application {
    mainClass.set("com.maxim.poteryashki.auth.LostApplicationKt")
}

// Основная задача генерации кода
openApiGenerate {
    // Генератор Kotlin + Spring (серверные стабы/контроллеры)
    generatorName.set("kotlin-spring")

    inputSpec.set("$rootDir/auth/src/main/resources/openapi/openapi.yaml")

    // Куда складывать сгенерированный код
    outputDir.set("$buildDir/generated/openapi")

    // Именование пакетов
    apiPackage.set("com.maxim.poteryashki.auth.api")
    modelPackage.set("com.maxim.poteryashki.auth.dto")

    configOptions.set(
        mapOf(
            "delegatePattern" to "true",
            "useTags" to "true",
            "useSpringBoot3" to "true",
            "interfaceOnly" to "false",
            "reactive" to "false",         // для MVC, не WebFlux
            "useBeanValidation" to "true",
            "dateLibrary" to "java8",
            "serializableModel" to "true",
            "sourceFolder" to "src/main/kotlin",
            "skipDefaultInterface" to "true", // не создавать лишний ApiUtil
            "returnResponse" to "true",
        )
    )
}

tasks.named("compileKotlin") {
    dependsOn("openApiGenerate")
}

dependencies {
    //Database
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.postgresql:postgresql")

    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webmvc")
    testImplementation("org.springframework.security:spring-security-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}