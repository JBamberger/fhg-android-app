// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion by extra("1.8.21")
    val hiltVersion by extra("2.44")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath("app.cash.licensee:licensee-gradle-plugin:1.1.0")
    }
}

plugins {
    id("com.google.devtools.ksp") version "1.8.21-1.0.11" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}
