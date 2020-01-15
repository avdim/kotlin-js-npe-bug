rootProject.name = "js-npe-bug"

pluginManagement {
  plugins {
    kotlin("multiplatform") version "1.3.70-eap-42"
  }
  repositories {
    gradlePluginPortal()
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
  }
}

