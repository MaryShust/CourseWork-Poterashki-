description = "gateway for backend"
extra["springCloudVersion"] = "2025.0.0"

docker {
    dockerfile.set("docker/Dockerfile")
}
dockerCompose {

}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webmvc")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}