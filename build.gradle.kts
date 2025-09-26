import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.kotlin.dsl.configure

plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "kz.nitec"
version = "0.0.1-SNAPSHOT"
description = "sdu-chat-bot-application"

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
    maven { url = uri("https://repo1.maven.org/maven2") }
}

dependencies {

    // spring core
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
//    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")


    // database
    implementation("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // lombok
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    // mapper
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // validation
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    // metamodels generator
    annotationProcessor("org.hibernate:hibernate-jpamodelgen:6.3.1.Final")

    // metrics
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // logs
    implementation("ch.qos.logback:logback-core:1.5.13")
    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    implementation("org.springframework.boot:spring-boot-starter")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.0.0")
    }
}
