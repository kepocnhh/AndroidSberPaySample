object Version {
    const val jvmTarget = "1.8"
    const val kotlin = "1.6.10"
    // const val kotlin = "1.6.21" // compose 1.1.1 require kotlin 1.6.10

    object Android {
        const val toolsBuildGradle = "7.1.3"
        const val compileSdk = 31
        const val minSdk = 23
        const val targetSdk = compileSdk
        const val compose = "1.1.1"
        // const val compose = "1.2.0" // require kotlin 1.7.0
        // const val compose = "1.2.1" // 2022/09/02 compiler 1.2.0
    }

    object Application {
        const val code = 1
        const val name = "0.0.$code"
    }
}
