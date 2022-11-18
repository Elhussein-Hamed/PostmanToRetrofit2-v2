plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.9.0"
}

group = "com.hamed"
version = "1.3.0"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2021.3.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

dependencies {

    implementation("org.jsonschema2pojo:jsonschema2pojo:1.1.2")
    implementation("com.formdev:flatlaf:2.5")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("192.0")
        untilBuild.set("223.*")
    }

    signPlugin {
        if (System.getenv("CERTIFICATE_CHAIN") != null && System.getenv("PRIVATE_KEY") != null) {
            certificateChain.set(File(System.getenv("CERTIFICATE_CHAIN")).readText(Charsets.UTF_8))
            privateKey.set(File(System.getenv("PRIVATE_KEY")).readText(Charsets.UTF_8))
            password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
        }
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
