extra["noRuntime"] = true

plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation(project(":layerdocs-core"))
    implementation(project(":layerdocs-html"))
    implementation(project(":layerdocs-plaintext"))
    implementation(project(":layerdocs-stdlib"))
}

tasks.test {
    useJUnitPlatform()
    // Resolve the synthetic `lib/` directory the same way the CLI does at runtime.
    dependsOn(":assembleDevLib")
}
