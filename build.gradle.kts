plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.0"
    id("org.jetbrains.intellij") version "1.9.0"
}

group = "com.hamed"
version = "1.5.3-beta"

repositories {
    mavenCentral()
    maven { url "https://jitpack.io" }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.robohorse.robopojogenerator:2.3.8"))
}

dependencies {

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.fifesoft:rsyntaxtextarea:3.3.0")
    implementation("com.github.Elhussein-Hamed:PostmanToRetrofit2-v2:1.5.2")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {

        // Java language level used to compile sources and to generate the files for - Java 11 is required since 2020.3
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    patchPluginXml {
        sinceBuild.set("203.0")
        untilBuild.set("223.*")
    }

    signPlugin {
        if (System.getenv("CERTIFICATE_CHAIN") != null && System.getenv("PRIVATE_KEY") != null) {
            certificateChain.set(File(System.getenv("CERTIFICATE_CHAIN")).readText(Charsets.UTF_8))
            privateKey.set(File(System.getenv("PRIVATE_KEY")).readText(Charsets.UTF_8))
            password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
        }
        else
            assert(false)
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    setupDependencies {
        doLast {
            // Fixes IDEA-298989.
            fileTree("$buildDir/instrumented/instrumentCode") { include("**/**/*Dialog.class") }.files.forEach { delete(it) }
        }
    }
}
