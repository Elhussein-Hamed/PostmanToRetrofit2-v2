plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("org.jetbrains.intellij.platform") version "2.0.1"
    id("maven-publish")
}

group = "com.hamed"
version = "2.3.0"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fifesoft:rsyntaxtextarea:3.5.1")
    implementation(kotlin("script-runtime"))
    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        // Target IDE Platform
        intellijIdeaCommunity("2024.2")

        bundledPlugin("com.intellij.java")
        plugin("com.robohorse.robopojogenerator:2.6.1")

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }

    tasks {
        setupDependencies {
            doLast {
                // Fixes IDEA-298989.
                fileTree("$buildDir/instrumented/instrumentCode") { include("**/**/*Dialog.class") }.files.forEach { delete(it) }
            }
        }
    }
}

intellijPlatform {
    pluginVerification {
        ides {
            recommended()
        }
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {

        // Java language level used to compile sources and to generate the files for - Java 11 is required since 2020.3
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("231.0") // From 2023.1
    }

    signPlugin {
        if (System.getenv("CERTIFICATE_CHAIN") != null && System.getenv("PRIVATE_KEY") != null) {
            certificateChainFile.set(file(System.getenv("CERTIFICATE_CHAIN")))
            privateKeyFile.set(file(System.getenv("PRIVATE_KEY")))
            password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
        }
        else
            assert(false)
    }

    verifyPluginSignature {
        if (System.getenv("CERTIFICATE_CHAIN") != null) {
            certificateChainFile.set(file(System.getenv("CERTIFICATE_CHAIN")))
        }
        else
            assert(false)
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
