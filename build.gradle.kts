// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion by extra("1.8.20")
    val hiltVersion by extra("2.44")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath("app.cash.licensee:licensee-gradle-plugin:1.1.0")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
