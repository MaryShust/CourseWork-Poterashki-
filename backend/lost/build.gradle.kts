description = "lost service"
val springCloudAwsVersion = "3.4.0"

dependencies {
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:${springCloudAwsVersion}"))
//    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")

//    testImplementation("org.springframework.boot:spring-boot-starter-data-elasticsearch-test")
}