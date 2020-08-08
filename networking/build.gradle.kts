dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.Tool.kotlin}")
    implementation("com.squareup.retrofit2:retrofit:${Versions.App.retrofit}")
    implementation("com.squareup.retrofit2:converter-moshi:${Versions.App.retrofit}")
    implementation("com.squareup.retrofit2:adapter-rxjava2:${Versions.App.retrofit}")
    implementation("com.squareup.moshi:moshi-kotlin:${Versions.App.moshi}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${Versions.App.moshi}")

    testImplementation("junit:junit:${Versions.Test.junit}")
    androidTestImplementation("androidx.test:runner:${Versions.Test.runner}")
}

