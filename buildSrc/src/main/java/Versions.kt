object Versions {
    val App = App()
    val Test = Test()
    val Tool = Tool()
    val Debug = Debug()
    val AndroidConfig = AndroidConfig()
}

class App {
    /*  Architecture */
    val androidX = "1.1.0-rc01"
    val rxAndroid = "2.1.1"
    val rxJava = "2.1.13"
    val rxBinding = "3.0.0"
    val viewModel = "2.0.0"
    val workManager = "2.2.0"
    val threeTenABP = "1.2.1"

    /* UI */
    val material = "1.1.0-alpha09"
    val constraintLayout = "2.0.0-beta2"

    /* Network & Data Layer */
    val moshi = "1.8.0"
    val retrofit = "2.6.1"
    val room = "2.1.0-alpha04"

    /* DI */
    val dagger = "2.21"
}

class Debug {
    val stetho = "1.5.1"
}

class Test {
    val junit = "4.12"
    val runner = "1.2.0"
    val core = "1.2.0"
    val junitExt = "1.1.0"
    val espresso = "3.2.0"
}

class Tool {
    val kotlin = "1.3.50"
    val buildGradle = "3.4.2"
}

class AndroidConfig {
    val sdkVersion = 28 // target & compile sdk
    val minSdkVersion = 21
    val versionCode = 1
    val versionName = "0.1.0"
}
