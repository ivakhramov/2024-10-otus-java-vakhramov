plugins {
    id("java")
}

group = "ru.otus"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("ch.qos.logback:logback-classic")

    implementation("org.hibernate.orm:hibernate-core")
    implementation("org.postgresql:postgresql")

    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    testImplementation("org.assertj:assertj-core")

    testImplementation("com.h2database:h2")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

tasks.test {
    useJUnitPlatform()
}