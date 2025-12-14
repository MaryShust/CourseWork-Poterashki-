description = "lost service"

val generateLostApi = createOpenApiTask(
    taskName = "lost",
    specFile = file("$rootDir/lost/src/main/resources/openapi/lost.yaml"),
    apiPackage = "com.maxim.poteryashki.lost.api",
    modelPackage = "com.maxim.poteryashki.lost.dto",
    apiSuffix = "LostApi"
)

val generateAuthApi = createOpenApiTask(
    taskName = "auth",
    specFile = file("$rootDir/lost/src/main/resources/openapi/auth.yaml"),
    apiPackage = "com.maxim.poteryashki.auth.api",
    modelPackage = "com.maxim.poteryashki.auth.dto",
    apiSuffix = "AuthApi"
)

// Добавляем обе задачи как зависимости для compileKotlin
tasks.named("compileKotlin") {
    dependsOn(generateLostApi, generateAuthApi)
}

// Подключаем сгенерированные исходники
kotlin {
    sourceSets {
        val main by getting {
            kotlin.srcDir("$buildDir/generated/openapi/lost/src/main/kotlin")
            kotlin.srcDir("$buildDir/generated/openapi/auth/src/main/kotlin")
        }
    }
}

application {
    mainClass.set("com.maxim.poteryashki.lost.LostApplicationKt")
}

dependencies {
    // Web + Validation для контроллеров и валидации
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // SpringDoc для Spring Boot 3 (если нужен OpenAPI UI/модели)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // Elasticsearch
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

    // Postgres
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.postgresql:postgresql")

    // MinIO
    implementation("io.minio:minio:8.6.0")
}

fun createOpenApiTask(
    taskName: String,
    specFile: File,
    apiPackage: String,
    modelPackage: String,
    apiSuffix: String
) = tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>(taskName) {
    generatorName.set("kotlin-spring")
    inputSpec.set(specFile.absolutePath)
    outputDir.set("$buildDir/generated/openapi/$taskName")

    this.apiPackage.set(apiPackage)
    this.modelPackage.set(modelPackage)

    // Опции генератора
    configOptions.set(
        mapOf(
            "delegatePattern" to "true",
            "useTags" to "true",
            "useSpringBoot3" to "true",
            "interfaceOnly" to "false",
            "reactive" to "false",
            "useBeanValidation" to "true",
            "dateLibrary" to "java8",
            "serializableModel" to "true",
            "sourceFolder" to "src/main/kotlin",
            "skipDefaultInterface" to "true",
            "returnResponse" to "true",
            "apiNameSuffix" to apiSuffix
        )
    )

    // Генерируем только apis и models, без supporting files и без доков/тестов
    globalProperties.set(
        mapOf(
            "models" to "",
            "apis" to "",
            "supportingFiles" to "false",
            "apiDocs" to "false",
            "modelDocs" to "false",
            "apiTests" to "false",
            "modelTests" to "false"
        )
    )
}