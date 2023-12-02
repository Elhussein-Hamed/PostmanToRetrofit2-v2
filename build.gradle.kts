
plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.0"
    id("org.jetbrains.intellij") version "1.12.0"
    id("maven-publish")
}

group = "com.hamed"
version = "2.0.0"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.2")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.robohorse.robopojogenerator:2.4.1", "com.intellij.java"))
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.fifesoft:rsyntaxtextarea:3.3.1")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {

        // Java language level used to compile sources and to generate the files for - Java 11 is required since 2020.3
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    patchPluginXml {
        sinceBuild.set("231.0") // From 2023.1
    }

    listProductsReleases {
        doFirst {
            val text =
                File("${project.buildDir}/tmp/downloadIdeaProductReleasesXml/idea_product_releases.xml").readText()
            val output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n$text"
            File("${project.buildDir}/tmp/downloadIdeaProductReleasesXml/idea_product_releases.xml").writeText(output)
        }
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
