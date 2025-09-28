import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.6" apply false
}

allprojects {
    group = "com.synclearn"
    version = "0.0.1"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")

    // Spring Dependency Management 플러그인으로 BOM을 가져와 버전을 중앙 관리한다
    configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.6")
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
//        implementation("org.springframework.boot:spring-boot-starter-actuator")
//        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//        implementation("org.springframework.boot:spring-boot-starter-jdbc")
//        implementation("org.springframework.boot:spring-boot-starter-jooq")
//        implementation("org.springframework.boot:spring-boot-starter-restclient")
//        implementation("org.springframework.boot:spring-boot-starter-web")
//        developmentOnly("org.springframework.boot:spring-boot-docker-compose")
//        runtimeOnly("com.h2database:h2")
//        runtimeOnly("org.postgresql:postgresql")
//        testImplementation("org.springframework.boot:spring-boot-starter-test")
//        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        compileOnly("org.projectlombok:lombok:1.18.42")
        annotationProcessor("org.projectlombok:lombok:1.18.42")
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(21)
        options.encoding = "UTF-8"
    }
}
