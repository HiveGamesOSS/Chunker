import org.panteleyev.jpackage.ImageType
import java.io.ByteArrayOutputStream
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.gitVersion)
    alias(libs.plugins.jpackage)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.picocli)
    api(libs.jetbrains.annotations)
    api(libs.fastutil)
    api(libs.caffeine)
    api(libs.guava)
    api(libs.gson)
    api(libs.lz4)
    api(libs.leveldb.api)
    api(libs.leveldb)

    // Unit Testing (JUnit)
    testImplementation(libs.junit)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

group = "com.hivemc.chunker"
version = "1.8.0"
description = "chunker"
base.archivesName = "chunker-cli"
java.sourceCompatibility = JavaVersion.VERSION_17

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])

        pom {
            description.set("Convert Minecraft worlds between Java Edition and Bedrock Edition CLI/API.")
        }
    }

    // Only define the repositories if the url is present
    val deployUrl = System.getenv("MAVEN_DEPLOY_URL")
    if (deployUrl != null) {
        repositories {
            maven {
                url = uri(System.getenv("MAVEN_DEPLOY_URL"))
                credentials {
                    username = System.getenv("MAVEN_DEPLOY_USERNAME")
                    password = System.getenv("MAVEN_DEPLOY_PASSWORD")
                }
            }
        }
    }
}

tasks.clean {
    doFirst {
        // Clear output directory
        delete(layout.buildDirectory.dir("libs"))
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    maxHeapSize = "4096M"
    useJUnitPlatform {
        excludeTags("LongRunning")
    }
}

tasks.register<Test>("longRunningTest") {
    description = "Runs long-running tests, these may take over 30 minutes depending on your machine."
    group = "verification"
    maxHeapSize = "8192M"
    useJUnitPlatform {
        excludeTags()
    }
}

tasks.shadowJar {
    archiveClassifier.set("")

    // Exclude META-INF signatures / module info
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    exclude("META-INF/MANIFEST.MF")
    exclude("META-INF/**/module-info.class")

    doLast {
        copy {
            from(destinationDirectory)
            into(rootProject.projectDir.resolve("build").resolve("libs"))
            include(archiveFileName.get())
        }
    }
}

tasks.jar {
    archiveClassifier.set("unshaded")

    val gitVersion: () -> String = {
        // Get the current branch
        val branch = providers.exec {
            commandLine("git", "branch", "--show-current")
        }.standardOutput.asText.getOrElse("unknown");

        // Get the current commit
        val commit = providers.exec {
            commandLine("git", "describe", "--tags", "--always")
        }.standardOutput.asText.getOrElse("unknown")

        // Replace newlines with dashes and trim it
        branch.plus(commit).trim().replace("\n", "-").replace("\r", "")
    }

    // Set attributes for running the jar
    manifest.attributes["Main-Class"] = "com.hivemc.chunker.cli.CLI"
    manifest.attributes["Chunker-Version"] = version
    manifest.attributes["Implementation-Title"] = "Chunker"
    manifest.attributes["Implementation-Version"] = version
    manifest.attributes["Git-Version"] = gitVersion()

    val timeStamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
    manifest.attributes["Build-Timestamp"] = timeStamp
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:unchecked")
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.jpackage {
    dependsOn("build")

    // Copy files
    doFirst {
        // Clear output directory
        delete(layout.buildDirectory.dir("libs").get().dir("packaged"))

        // Copy inputs
        mkdir("${layout.buildDirectory.get()}/libs/input")
        copy {
            from(layout.buildDirectory.dir("libs"))
            into(layout.buildDirectory.dir("libs/input"))
            include(tasks.shadowJar.get().archiveFileName.get())
        }
    }

    // Settings for jpackage
    type = ImageType.APP_IMAGE
    input = layout.buildDirectory.dir("libs/input").get().toString()
    destination = layout.buildDirectory.dir("libs").get().dir("packaged").toString()
    appVersion = version.toString().replace("-SNAPSHOT", "")

    appName = "chunker-cli"
    vendor = "hivegames.io"

    mainJar = tasks.shadowJar.get().archiveFileName.get()
    mainClass = "com.hivemc.chunker.cli.CLI"

    javaOptions = listOf("-Dfile.encoding=UTF-8")

    windows {
        winConsole = true

        doLast {
            // Clear the old output
            delete(rootProject.projectDir.resolve("build").resolve("libs").resolve("windows"))

            // Copy the new output
            copy {
                from(layout.buildDirectory.dir("libs").get().dir("packaged"))
                into(rootProject.projectDir.resolve("build").resolve("libs").resolve("windows"))
            }
        }
    }

    linux {
        doLast {
            // Clear the old output
            delete(rootProject.projectDir.resolve("build").resolve("libs").resolve("linux"))

            // Copy the new output
            copy {
                from(layout.buildDirectory.dir("libs").get().dir("packaged"))
                into(rootProject.projectDir.resolve("build").resolve("libs").resolve("linux"))
            }
        }
    }

    mac {
        doLast {
            // Clear the old output
            delete(rootProject.projectDir.resolve("build").resolve("libs").resolve("mac"))

            // Copy the new output
            copy {
                from(layout.buildDirectory.dir("libs").get().dir("packaged"))
                into(rootProject.projectDir.resolve("build").resolve("libs").resolve("mac"))
            }
        }
    }
}