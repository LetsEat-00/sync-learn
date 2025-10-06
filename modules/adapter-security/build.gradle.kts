dependencies {
    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    // OAuth2 Client (GitHub 로그인에 필요)
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    // JSON 파싱용 (CustomOAuth2UserService에서 ObjectMapper 사용)
    implementation("com.fasterxml.jackson.core:jackson-databind")
    // RestTemplate 사용 시 필요 (대부분 WebStarter에 포함되어 있지만 security 모듈에 따로 추가)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // 모듈
    implementation(project(":modules:domain"))
    implementation(project(":modules:adapter-persistence"))
    implementation(project(":modules:application"))
}