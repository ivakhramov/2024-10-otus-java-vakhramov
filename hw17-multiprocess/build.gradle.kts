import com.google.protobuf.gradle.id

plugins {
    id("java")
    id("idea")
    id("com.google.protobuf")
    id("com.diffplug.spotless")
}

group = "ru.otus"
version = "unspecified"

repositories {
    mavenCentral()
}

spotless {
    ratchetFrom("origin/main")
    java {
        target("src/**/*.java")
        targetExclude("build/generated/**/*.java")
        googleJavaFormat()
    }
}

dependencies {
    implementation("io.grpc:grpc-netty-shaded:1.64.0")
    implementation("io.grpc:grpc-protobuf:1.64.0")
    implementation("io.grpc:grpc-stub:1.64.0")

    implementation("org.apache.tomcat:annotations-api:6.0.53")

    implementation("ch.qos.logback:logback-classic")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

val protoSrcDir = "$projectDir/build/generated"

idea {
    module {
        sourceDirs = sourceDirs.plus(file(protoSrcDir))
    }
}

sourceSets {
    main {
        proto {
            srcDir(protoSrcDir)
        }
    }
}

protobuf {
    generatedFilesBaseDir = protoSrcDir
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.64.0"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

afterEvaluate {
    tasks {
        getByName("generateProto").dependsOn(processResources)
    }
}

tasks.test {
    useJUnitPlatform()
}
