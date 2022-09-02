object Version {
    const val jvmTarget = "1.8"
    const val kotlin = "1.6.21"

    object Android {
        const val toolsBuildGradle = "7.1.1"
        const val compileSdk = 31
        const val minSdk = 23
        const val targetSdk = compileSdk
        const val compose = "1.1.0"
    }

    object Application {
        const val code = 1
        const val name = "0.0.$code"
    }
}
