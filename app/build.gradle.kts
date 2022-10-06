repositories {
    mavenCentral()
    google()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = Version.Android.compileSdk

    defaultConfig {
        minSdk = Version.Android.minSdk
        targetSdk = Version.Android.targetSdk
        versionCode = Version.Application.code
        versionName = Version.Application.name
        manifestPlaceholders["appName"] = "@string/app_name"
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".$name"
            versionNameSuffix = "-$name"
            isMinifyEnabled = false
            isShrinkResources = false
            manifestPlaceholders["buildType"] = name
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Version.Android.compose
    }
}

androidComponents.onVariants { variant ->
    val output = variant.outputs.single()
    check(output is com.android.build.api.variant.impl.VariantOutputImpl)
    output.outputFileName.set("SberPaySample-${Version.Application.name}-${variant.buildType!!}.apk")
    afterEvaluate {
        tasks.getByName<JavaCompile>("compile${variant.name.capitalize()}JavaWithJavac") {
            targetCompatibility = Version.jvmTarget
        }
        tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compile${variant.name.capitalize()}Kotlin") {
            kotlinOptions.jvmTarget = Version.jvmTarget
        }
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("androidx.compose.foundation:foundation:${Version.Android.compose}")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.github.kepocnhh:KotlinExtension.OkHttp:0.1-SNAPSHOT")
    implementation("com.github.kepocnhh:KotlinExtension.Functional:0.3-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
}
