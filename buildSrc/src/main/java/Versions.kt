object Versions {
    val App = App()
    val Test = Test()
    val Tool = Tool()
    val AndroidConfig = AndroidConfig()
}

class App {
    val androidX = "1.1.0-rc01"
    val rxAndroid = "2.1.1"
    val rxJava = "2.1.13"
    val rxBinding = "3.0.0"
    val material = "1.1.0-alpha09"
    val constraintLayout = "2.0.0-beta2"
    val recyclerView = "1.1.0-beta01"
    val viewModel = "2.0.0"
}

class Test {
    val junit = "4.12"
    val runner = "1.0.2"
    val espresso = "3.0.2"
}

class Tool {
    val kotlin = "1.3.50"
    val buildGradle = "3.4.2"
}

class AndroidConfig {
    val sdkVersion = 28 // target & compile sdk
    val minSdkVersion = 21
    val targetSdkVersion = 28
    val versionCode = 1
    val versionName = "0.1.0"
}
