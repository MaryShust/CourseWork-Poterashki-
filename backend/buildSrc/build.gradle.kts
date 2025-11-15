plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
}
gradlePlugin {
    plugins {
        create("docker-plugin") {
            id = "com.maxim.docker-plugin"
            implementationClass = "com.maxim.DockerPlugin"
        }

        create("docker-compose-plugin") {
            id = "com.maxim.docker-compose-plugin"
            implementationClass = "com.maxim.DockerComposePlugin"
        }
    }
}
