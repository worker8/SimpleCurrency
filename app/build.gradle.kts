android {
    defaultConfig {
        applicationId = "com.worker8.simplecurrency"
    }
    dependencies {
        implementation(project(":currencylayer"))

        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.Tool.kotlin}")

        /*  Architecture */
        implementation("androidx.appcompat:appcompat:${Versions.App.androidX}")
        implementation("androidx.lifecycle:lifecycle-extensions:${Versions.App.viewModel}")
        implementation("androidx.lifecycle:lifecycle-viewmodel:${Versions.App.viewModel}")
        implementation("androidx.work:work-runtime:${Versions.App.workManager}")
        implementation("io.reactivex.rxjava2:rxjava:${Versions.App.rxJava}")
        implementation("io.reactivex.rxjava2:rxandroid:${Versions.App.rxAndroid}")
        implementation("com.jakewharton.rxbinding3:rxbinding:${Versions.App.rxBinding}")
        implementation("com.jakewharton.threetenabp:threetenabp:${Versions.App.threeTenABP}")

        /* UI */
        implementation("androidx.constraintlayout:constraintlayout:${Versions.App.constraintLayout}")
        implementation("com.google.android.material:material:${Versions.App.material}")

        /* Network & Data Layer */
        implementation("com.squareup.retrofit2:retrofit:${Versions.App.retrofit}")
        implementation("com.squareup.retrofit2:converter-moshi:${Versions.App.retrofit}")
        implementation("androidx.room:room-runtime:${Versions.App.room}")
        implementation("androidx.room:room-rxjava2:${Versions.App.room}")
        annotationProcessor("androidx.room:room-compiler:${Versions.App.room}")
        kapt("androidx.room:room-compiler:${Versions.App.room}")

        /* DI */
        implementation("com.google.dagger:dagger:${Versions.App.dagger}")
        kapt("com.google.dagger:dagger-compiler:${Versions.App.dagger}")
        implementation("com.google.dagger:dagger-android:${Versions.App.dagger}")
        implementation("com.google.dagger:dagger-android-support:${Versions.App.dagger}")
        kapt("com.google.dagger:dagger-android-processor:${Versions.App.dagger}")

        /* Debug */
        debugImplementation("com.facebook.stetho:stetho:${Versions.Debug.stetho}")
        debugImplementation("com.facebook.stetho:stetho-okhttp3:${Versions.Debug.stetho}")

        /* Test */
        testImplementation("junit:junit:${Versions.Test.junit}")
        testImplementation("io.mockk:mockk:${Versions.Test.mockk}")
        androidTestImplementation("androidx.test.ext:junit:${Versions.Test.junitExt}")
        androidTestImplementation("androidx.test:core:${Versions.Test.core}")
        androidTestImplementation("androidx.test:runner:${Versions.Test.runner}")
        androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.Test.espresso}")
    }
}
