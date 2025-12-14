description = "gateway for backend"
extra["springCloudVersion"] = "2025.0.0"

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