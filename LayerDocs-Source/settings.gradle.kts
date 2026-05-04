pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "layerdocs"

include("layerdocs-core")
include("layerdocs-html")
include("layerdocs-plaintext")
include("layerdocs-cli")
include("layerdocs-stdlib")
include("layerdocs-test")
include("layerdocs-libs")
include("layerdocs-server")
include("layerdocs-interaction")
include("layerdocs-layerdoc")
include("layerdocs-layerdoc-reader")
include("layerdocs-install-layout-navigator")
include("layerdocs-lsp")
