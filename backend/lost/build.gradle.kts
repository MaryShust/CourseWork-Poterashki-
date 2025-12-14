description = "lost service"
val springCloudAwsVersion = "3.4.0"


kotlin {
    sourceSets {
        val main by getting {
            kotlin.srcDir("$buildDir/generated/openapi/src/main/kotlin")
        }
    }
}

application {
    mainClass.set("com.maxim.poteryashki.lost.LostApplicationKt")
}

// Основная задача генерации кода
openApiGenerate {
    // Генератор Kotlin + Spring (серверные стабы/контроллеры)
    generatorName.set("kotlin-spring")

    // Путь к вашей спецификации OpenAPI
    // Например, положите файл src/main/resources/openapi.yaml
    inputSpec.set("$rootDir/lost/src/main/resources/openapi/contract.yaml")

    // Куда складывать сгенерированный код
    outputDir.set("$buildDir/generated/openapi")

    // Именование пакетов
    apiPackage.set("com.maxim.poteryashki.lost.api")
    modelPackage.set("com.maxim.poteryashki.lost.dto")

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
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("io.minio:minio:8.6.0")

}