import java.net.URI

plugins {
    kotlin("jvm") version "1.4.0"
    kotlin("plugin.spring") version "1.4.0"
    id("org.springframework.boot") version "2.3.3.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("io.gitlab.arturbosch.detekt") version "1.12.0"
}

group = "br.com.creditas"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    jcenter()

    maven {
        url = URI(ext.get("artifactory_contextUrl") as String + "/libs-snapshot")
        credentials {
            username = ext.get("artifactory_user") as String
            password = ext.get("artifactory_password") as String
        }
    }

    maven {
        url = URI(ext.get("artifactory_contextUrl") as String + "/libs-release")

        credentials {
            username = ext.get("artifactory_user") as String
            password = ext.get("artifactory_password") as String
        }
    }
}

val authLibVersion = "0.1.0-SNAPSHOT"
val detektVersion = "1.12.0"
val openapiVersion = "1.4.8"

dependencies {

//    Security
    api("com.auth0:java-jwt:3.3.0")
    implementation("org.bouncycastle:bcprov-jdk15on:1.61")
    implementation("org.springframework.security:spring-security-test")

//    Core
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9") {
        isTransitive = true
    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.3.9")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

//    Monitoring
    implementation("io.micrometer:micrometer-registry-prometheus:latest.release")
    implementation("com.newrelic.agent.java:newrelic-api:6.1.0")

//    Documentation
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
    implementation("net.logstash.logback:logstash-logback-encoder:6.4")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:$openapiVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$openapiVersion")

//    Linting
    detekt("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
    detekt("io.gitlab.arturbosch.detekt:detekt-cli:$detektVersion")

//    Test environment dependencies
    testImplementation("org.junit.jupiter:junit-jupiter") {
        isTransitive = true
    }
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.rest-assured:json-schema-validator:3.3.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.0.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.0.1")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:2.1.1.RELEASE")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    test {
        useJUnitPlatform()
    }
}

detekt {
    toolVersion = detektVersion
    input = files("./src")
    config = files("./detekt-config.yml")
    autoCorrect = true
}
