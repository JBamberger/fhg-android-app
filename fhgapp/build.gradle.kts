import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("app.cash.licensee")
    id("com.google.devtools.ksp")
}

licensee {
    allow("Apache-2.0")
//    allow("MIT")
//    allow("BSD-2-Clause")

    allowDependency("com.github.bumptech.glide", "glide", "4.15.1") {
        because("BSD-2-Clause license but the URL is not recognized.")
    }
    allowDependency("com.github.bumptech.glide", "annotations", "4.15.1") {
        because("BSD-2-Clause license but the URL is not recognized.")
    }
    allowDependency("com.github.bumptech.glide", "disklrucache", "4.15.1") {
        because("Apache-2.0 declared with wrong URL.")
    }
    allowDependency("com.github.bumptech.glide", "gifdecoder", "4.15.1") {
        because("MIT declared with wrong URL.")
    }
}

android {
    namespace = "de.jbamberger.fhgapp"
    compileSdk = 33

    defaultConfig {
        // Legacy package name. Should probably be replaced with a domain I own. Cannot be changed
        // without losing user count, ratings and comments due to Google Play Store policies.
        applicationId = "xyz.jbapps.vplan"
        minSdk = 16
        targetSdk = 33
        versionCode = 34
        versionName = "3.2.3"

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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    val jv = JavaVersion.VERSION_17
    compileOptions {
        sourceCompatibility = jv
        targetCompatibility = jv
    }
    kotlinOptions {
        jvmTarget = jv.toString()
    }

    lint {
        lintConfig = file("$rootDir/lint.xml")
    }

    // The output generated by the Licensee plugin is included in the assets of the application.
    // To achieve this a new asset directory is created and registered.
    applicationVariants.configureEach {
        val variant = this
        val capVariantName = variant.name.replaceFirstChar { c -> c.uppercaseChar() }
        val outDir = "generated/dependencyAssets/${variant.name}/"

        android.sourceSets[variant.name].assets.srcDir(layout.buildDirectory.dir(outDir))

        // Task that copies the dependency list to the asset directory.
        val copyArtifactsTask =
            tasks.register("copy${capVariantName}ArtifactList", Copy::class) {
                from(
                    project.extensions.getByType(ReportingExtension::class.java)
                        .file("licensee/${variant.name}/artifacts.json")
                )
                into(layout.buildDirectory.dir(outDir))
            }
        // Ensure that the licensee plugin runs before the dependency list is copied.
        copyArtifactsTask.dependsOn("licensee$capVariantName")
        // Ensure that the dependency list is copied before the assets are merged.
        tasks["merge${capVariantName}Assets"].dependsOn(copyArtifactsTask)
        tasks.findByName("lintAnalyze${capVariantName}")?.dependsOn(copyArtifactsTask)
        tasks.findByName("lintVitalAnalyze${capVariantName}")?.dependsOn(copyArtifactsTask)
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
    implementation("org.jetbrains:annotations:22.0.0")

    implementation("androidx.annotation:annotation:1.6.0")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.1")
    implementation("androidx.paging:paging-runtime-ktx:3.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    implementation("com.jakewharton.timber:timber:5.0.1")

    val okhttpVersion = "4.11.0"
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    val moshiVersion = "1.14.0"
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

    val glideVersion = "4.15.1"
    api("com.github.bumptech.glide:glide:$glideVersion")
    ksp("com.github.bumptech.glide:ksp:$glideVersion")

    val hiltVersion: String by rootProject.extra
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
}

kapt {
    correctErrorTypes = true
}