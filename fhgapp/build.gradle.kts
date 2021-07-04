import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("app.cash.licensee")
}

licensee {
    allow("Apache-2.0")
//    allow("MIT")
//    allow("BSD-2-Clause")

    allowDependency("com.github.bumptech.glide", "glide", "4.12.0") {
        because("BSD-2-Clause license but the URL is not recognized.")
    }
    allowDependency("com.github.bumptech.glide", "annotations", "4.12.0") {
        because("BSD-2-Clause license but the URL is not recognized.")
    }
    allowDependency("com.github.bumptech.glide", "disklrucache", "4.12.0") {
        because("Apache-2.0 declared with wrong URL.")
    }
    allowDependency("com.github.bumptech.glide", "gifdecoder", "4.12.0") {
        because("MIT declared with wrong URL.")
    }
}

android {
    compileSdkVersion(30)

    defaultConfig {
        // Legacy package name. Should probably be replaced with a domain I own. Cannot be changed
        // without losing user count, ratings and comments due to Google Play Store policies.
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

    // The output generated by the Licensee plugin is included in the assets of the application.
    // To achieve this a new asset directory is created and registered.
    sourceSets["main"].assets.srcDir(layout.buildDirectory.dir("generated/dependencyAssets/"))
    applicationVariants.configureEach {
        val variant = this

        // Task that copies the dependency list to the asset directory.
        val copyArtifactsTask =
            tasks.register<Copy>("copy${variant.name.capitalize()}ArtifactList") {
                from(
                    project.extensions.getByType(ReportingExtension::class.java)
                        .file("licensee/${variant.name}/artifacts.json")
                )
                into(layout.buildDirectory.dir("generated/dependencyAssets/"))
            }
        // Ensure that the licensee plugin runs before the dependency list is copied.
        copyArtifactsTask.dependsOn("licensee${variant.name.capitalize()}")
        // Ensure that the dependency list is copied before the assets are merged.
        tasks["merge${variant.name.capitalize()}Assets"].dependsOn(copyArtifactsTask)
    }
}


dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    val kotlinVersion: String by rootProject.extra
    // The kotlin-stdlib-jdk8 extension library is specified to enforce a specific version
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    // Manually overwriting older annotations library versions because the older versions declare
    // their license with the wrong url.
    implementation("org.jetbrains:annotations:21.0.1")

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

    val okhttpVersion = "4.9.1"
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    val moshiVersion = "1.12.0"
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

    val glideVersion = "4.12.0"
    api("com.github.bumptech.glide:glide:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")

    val hiltVersion: String by rootProject.extra
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test:runner:1.4.0")
}

kapt {
    correctErrorTypes = true
}