import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.Logging
import org.testcontainers.containers.PostgreSQLContainer

val group: String by project
val version: String by project

plugins {
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.0.12.RELEASE"
    id("nu.studer.jooq") version "6.0.1"
    id("org.liquibase.gradle") version "2.1.1"

    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    jacoco
}

java.sourceCompatibility = JavaVersion.VERSION_17
if (!JavaVersion.current().isCompatibleWith(java.sourceCompatibility)) {
    throw GradleException("Java ${java.sourceCompatibility} or higher is required, actual version is ${JavaVersion.current()}.")
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-gcp-dependencies:1.2.8.RELEASE")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.liquibase:liquibase-core:4.15.0")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("io.mockk:mockk:1.12.5")
    testImplementation("org.testcontainers:testcontainers:1.17.3")
    testImplementation("org.testcontainers:postgresql:1.17.3")
    testImplementation("org.testcontainers:junit-jupiter:1.17.3")
    testImplementation("io.projectreactor:reactor-core:3.4.22")
    testImplementation("org.junit.platform:junit-platform-suite-engine:1.9.0")

    testImplementation("io.kotest:kotest-runner-junit5:5.5.1")
    testImplementation("io.kotest:kotest-assertions-core:5.5.1")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")

    jooqGenerator("org.postgresql:postgresql")

    liquibaseRuntime("org.liquibase:liquibase-core:4.15.0")
    liquibaseRuntime("info.picocli:picocli:4.6.3")
    liquibaseRuntime("org.liquibase:liquibase-gradle-plugin:2.1.1")
    liquibaseRuntime("org.postgresql:postgresql")
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.testcontainers:postgresql:1.17.3")
    }
}

val doNotStartPostgres: String? by project

val postgresContainer: PostgreSQLContainer<Nothing>? =
    if (doNotStartPostgres == "true") {
        null
    } else {
        println("Starting postgresql container")

        PostgreSQLContainer<Nothing>("postgres:13.8")
            .apply {
                withReuse(true)
                start()
            }
    }


jooq {
    version.set("3.15.5")

    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    url = postgresContainer?.jdbcUrl
                    user = postgresContainer?.username
                    password = postgresContainer?.username
                }

                logging = Logging.WARN
                generator.apply {
                    name = "org.jooq.codegen.JavaGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"

                        inputSchema = "public"
                        isOutputSchemaToDefault = true
                        excludes = "databasechangelog,databasechangeloglock"
                    }
                    generate.apply {
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isPojosEqualsAndHashCode = true

                        isNullableAnnotation = true
                        nullableAnnotationType = "org.jetbrains.annotations.Nullable"
                        isNonnullAnnotation = true
                        nonnullAnnotationType = "org.jetbrains.annotations.NotNull"
                    }
                    target.apply {
                        packageName = "cz.vitaplsek.e2e.jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

liquibase {
    activities {
        register("main") {

            arguments = mapOf(
                "changeLogFile" to "src/main/resources/db/db.changelog-master.xml",
                "url" to postgresContainer?.jdbcUrl,
                "username" to postgresContainer?.username,
                "password" to postgresContainer?.password,
                "driver" to "org.postgresql.Driver",
                "contexts" to "ddl"
            )
        }
    }
    runList = "main"
}

tasks.getByName<nu.studer.gradle.jooq.JooqGenerate>("generateJooq") {
    dependsOn("update")
    doLast {
        println("Stopping postgresql container")
        postgresContainer?.close()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test>() {
    useJUnitPlatform()
}
