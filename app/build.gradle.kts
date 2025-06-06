import com.github.gradle.node.npm.task.NpmTask
import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    alias(libs.plugins.node)
}

dependencies {
    project(":cli")
}

node {
    version.set("24.1.0")
    npmVersion.set("11.4.1")
    download.set(true)
}

// Hide tasks which aren't used (from node gradle plugin)
tasks.configureEach {
    if (name.startsWith("yarn") || name.startsWith("pnpm") || name.startsWith("npm")) {
        group = "hidden"
    }
}

tasks.register<NpmTask>("installDependencies") {
    group = "build"
    description = "Install npm dependencies"
    args.set(listOf("install"))
}

tasks.register<NpmTask>("build") {
    group = "build"
    description = "Run npm build"
    args.set(listOf("run", "build"))

    // Clean dist folder
    doFirst {
        delete(layout.projectDirectory.dir("electron").dir("dist"))
    }

    // Ensure dependencies are installed and we have a packagedChunker
    val jpackage = project(":cli").tasks.named("jpackage");
    dependsOn("installDependencies", jpackage)

    // Copy results
    var buildType = "unknown";
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        buildType = "windows"
    } else if (Os.isFamily(Os.FAMILY_MAC)) {
        buildType = "mac"
    } else if (Os.isFamily(Os.FAMILY_UNIX)) {
        buildType = "linux"
    }
    doLast {
        copy {
            from(layout.projectDirectory.dir("electron").dir("dist"))
            into(rootProject.projectDir.resolve("build").resolve("libs").resolve(buildType))
            include("chunker*", "Chunker*", "*-unpacked/**")
        }
    }
}

tasks.register<NpmTask>("start") {
    group = "build"
    description =
        "Run npm start (Note: Through gradle this will not stop properly, you will need to kill the node process)"
    args.set(listOf("run", "start"))

    // Ensure dependencies are installed and we have a chunker build
    val cliBuild = project(":cli").tasks.named("build");
    dependsOn("installDependencies", cliBuild)
}