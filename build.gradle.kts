import com.android.build.gradle.BaseExtension
import com.worker8.gradle.Secrets

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.Tool.buildGradle}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Tool.kotlin}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

subprojects {
    if (name == "app") {
        apply {
            plugin("com.android.application")
        }
    } else {
        apply {
            plugin("com.android.library")
        }
    }

    apply {
        plugin("org.jetbrains.kotlin.android")
        plugin("org.jetbrains.kotlin.kapt")
        plugin("kotlin-android-extensions")
    }

    configure<BaseExtension> {
        compileSdkVersion(Versions.AndroidConfig.sdkVersion)
        defaultConfig {
            minSdkVersion(Versions.AndroidConfig.minSdkVersion)
            targetSdkVersion(Versions.AndroidConfig.sdkVersion)
            versionCode = Versions.AndroidConfig.versionCode
            versionName = Versions.AndroidConfig.versionName
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes {
            all {
                buildConfigField(
                    "String",
                    "CURRENCY_LAYER_ACCCES_KEY",
                    "\"${Secrets.currencyLayerAccessToken}\""
                )
            }
            release {
                isMinifyEnabled = false
                proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
                proguardFile(file("proguard-rules.pro"))
            }
        }
    }
}

fun <T> NamedDomainObjectContainer<T>.release(configure: T.() -> Unit) = getByName("release", configure)
fun <T> NamedDomainObjectContainer<T>.debug(configure: T.() -> Unit) = getByName("debug", configure)
fun <T> NamedDomainObjectContainer<T>.all(configure: T.() -> Unit) = getByName("all", configure)
