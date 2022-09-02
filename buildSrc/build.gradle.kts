repositories {
    google()
    mavenCentral()
}

plugins {
    id("org.gradle.kotlin.kotlin-dsl") version "2.3.3"
}

dependencies {
    implementation("com.android.tools.build:gradle:7.1.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0") // https://stackoverflow.com/a/68772241/4398606
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.0") // Kotlin runtime JAR files in the classpath should have the same version
}

tasks.getByName<JavaCompile>("compileJava").also {
    it.targetCompatibility = "1.8"
}

tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin").also {
    it.kotlinOptions.jvmTarget = "1.8"
}
