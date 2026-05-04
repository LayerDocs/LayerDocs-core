plugins {
    kotlin("jvm")
    application
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.apache.pdfbox:pdfbox:3.0.6")
    implementation(project(":layerdocs-core"))
    implementation(project(":layerdocs-html"))
    implementation(project(":layerdocs-plaintext"))
    implementation(project(":layerdocs-server"))
    implementation(project(":layerdocs-interaction"))
    implementation(project(":layerdocs-stdlib"))
    implementation(project(":layerdocs-lsp"))
    implementation(project(":layerdocs-install-layout-navigator"))
    implementation("com.github.ajalt.clikt:clikt:5.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("io.methvin:directory-watcher:0.19.1")
}

application {
    mainClass.set("com.layerdocs.cli.LayerDocsCliKt")
}

// Writes the project version to a file in the resources directory, so it can be accessed at runtime.
val writeVersionFile by tasks.registering {
    val version = project.parent?.version ?: "unknown"
    val versionFile = "version.txt"
    val outputFile = layout.projectDirectory.file("src/main/resources/$versionFile").asFile

    doLast {
        outputFile.writeText(version.toString())
    }
}

tasks.processResources {
    dependsOn(writeVersionFile)
    dependsOn(":assembleDevLib")
}
