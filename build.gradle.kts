plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "digits"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot Web
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Spring Data JPA for persistence
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// H2 Database for development (in-memory)
	runtimeOnly("com.h2database:h2")

	// Kotlin dependencies
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Spring Boot DevTools (for live reloading during development)
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
