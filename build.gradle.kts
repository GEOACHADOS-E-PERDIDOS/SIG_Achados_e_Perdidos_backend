plugins {
	java
	id("org.springframework.boot") version "4.1.0-M2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ifb.edu.br"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.locationtech.jts:jts-core")
	implementation("org.hibernate.orm:hibernate-spatial")
    implementation("com.graphhopper.external:jackson-datatype-jts:2.21.0")
	implementation ("org.springframework.boot:spring-boot-starter-security")
    implementation ("io.jsonwebtoken:jjwt-api:0.13.0")

	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly ("io.jsonwebtoken:jjwt-jackson:0.13.0")

	annotationProcessor("org.projectlombok:lombok")

	testImplementation ("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
