buildscript {
    repositories {
        google()  // Ensure this line is included
        mavenCentral()  // Ensure this line is included
    }
    dependencies {
        classpath (libs.google.services)// Add this line
    }
} // Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.chaquo.python") version "16.0.0" apply false
}