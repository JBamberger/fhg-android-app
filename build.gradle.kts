// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlin_version by extra("1.5.10")
    val hilt_version by extra("2.37")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
        classpath("app.cash.licensee:licensee-gradle-plugin:1.1.0")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
