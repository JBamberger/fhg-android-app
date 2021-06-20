plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

val kotlin_version: String by rootProject.extra
val hilt_version: String by rootProject.extra

android {
    compileSdkVersion(30)

    defaultConfig {
        // legacy name. Replace with de.jbamberger.fhgapp to match package
        applicationId = "xyz.jbapps.vplan"
        minSdkVersion(16)
        targetSdkVersion(30)
        versionCode = 31
        versionName = "3.2.0"
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        testOptions {
            unitTests {
                isIncludeAndroidResources = true
                all {
                    it.jvmArgs("-noverify")
                }
            }
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/NOTICE")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/README.HTML")
        exclude("readme.html")
        exclude("license.html")
        exclude("META-INF/eclipse.inf")
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("androidx.annotation:annotation:1.2.0")
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.fragment:fragment-ktx:1.3.5")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.3.1")
    implementation("androidx.paging:paging-runtime-ktx:3.0.0")
    implementation("com.jakewharton.timber:timber:4.7.1")


    val okhttp_version = "4.9.1"
    val glide_version = "4.12.0"
    val moshi_version = "1.12.0"
    val retrofit_version = "2.9.0"

    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp_version")

    implementation("com.squareup.moshi:moshi:$moshi_version")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshi_version")

    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit_version")

    api("com.github.bumptech.glide:glide:$glide_version")
    kapt("com.github.bumptech.glide:compiler:$glide_version")

    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-compiler:$hilt_version")

    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test:runner:1.3.0")
}

kapt {
    correctErrorTypes = true
}