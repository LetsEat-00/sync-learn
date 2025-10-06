dependencies {
    implementation(project(":modules:domain"))
    implementation(project(":modules:adapter-persistence"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}