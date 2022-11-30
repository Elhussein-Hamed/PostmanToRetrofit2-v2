plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.9.0"
}

group = "com.hamed"
version = "1.4.0"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.robohorse.robopojogenerator:2.3.8"))
}

dependencies {

    implementation("org.jsonschema2pojo:jsonschema2pojo:1.1.2")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
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
