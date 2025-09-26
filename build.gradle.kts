plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "2.1.0"
    kotlin("plugin.serialization") version "1.9.23"
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.zighang"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")


    // oauth 2.0
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    
    //security
    implementation("org.springframework.boot:spring-boot-starter-security")

    //jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.3.10")
    implementation("org.redisson:redisson:3.44.0")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // mysql
    runtimeOnly("mysql:mysql-connector-java:8.0.33")

    // cloud storage
    implementation ("com.amazonaws:aws-java-sdk-s3:1.12.281")
    implementation ("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

    // feign client
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // rabbitmq
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    //json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")

    // test
    testImplementation("io.mockk:mockk:1.13.12")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

ext {
    set("springCloudVersion", "2024.0.0")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
