val javaVersion: String by project
val mapStructVersion: String by project
val springModulithVersion: String by project
val querydslVersion: String by project
val springDocVersion: String by project
val jakartaPersistenceApiVersion: String by project
val httpClientVersion: String by project
val p6spyVersion: String by project

plugins {
	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.tinyquest"
version = "0.0.1-SNAPSHOT"
description = "TinyQuert Platform Hub"

java {
	toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	// implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.modulith:spring-modulith-starter-core")
	implementation("org.springframework.modulith:spring-modulith-starter-jpa")

	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

    // MapStruct
    implementation("org.mapstruct:mapstruct:$mapStructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")

    // QueryDSL (Jakarta)
    implementation("com.querydsl:querydsl-jpa:${querydslVersion}:jakarta") // 정렬 화이트리스트 주의
    annotationProcessor("com.querydsl:querydsl-apt:${querydslVersion}:jakarta")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api:$jakartaPersistenceApiVersion")

    testAnnotationProcessor("com.querydsl:querydsl-apt:${querydslVersion}:jakarta")
    testAnnotationProcessor("jakarta.persistence:jakarta.persistence-api:$jakartaPersistenceApiVersion")

    testRuntimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.modulith:spring-modulith-starter-test")
	// testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")


    // Testcontainers
    testImplementation("org.springframework.boot:spring-boot-testcontainers") // Spring Boot 통합
    testImplementation("org.testcontainers:junit-jupiter")                    // JUnit 5 통합
    testImplementation("org.testcontainers:mariadb")                          // MariaDB 모듈

    // MVC 기준 (WebFlux면 -webflux-ui)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:$springDocVersion")

    implementation ("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")

    // Logging
    implementation("p6spy:p6spy:$p6spyVersion")

}

dependencyManagement {
	imports {
		mavenBom("org.springframework.modulith:spring-modulith-bom:$springModulithVersion")
	}
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(javaVersion.toInt())
}

tasks.withType<Test> {
    useJUnitPlatform()

    doFirst {
        val agentJar = configurations.testRuntimeClasspath.get().files
            .firstOrNull { it.name.contains("byte-buddy-agent") }

        if (agentJar != null) {
            jvmArgs("-javaagent:${agentJar.absolutePath}")
            println("✅ Mockito agent attached: ${agentJar.name}")
        } else {
            println("⚠️ Byte Buddy Agent not found in classpath.")
        }
    }
}