import com.worker8.gradle.lintModel.Issue
import com.worker8.gradle.lintModel.Issues
import com.worker8.gradle.lintModel.Location
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

android {
    defaultConfig {
        applicationId = "com.worker8.simplecurrency"
    }
    dependencies {
        implementation(project(":fixerio"))

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

open class GithubTask : DefaultTask() {
    @TaskAction
    fun hey() {

    }
}

open class DebugTask : DefaultTask() {
    val message = project.objects.property<String>()
    @TaskAction
    fun greet() {
        val xmlFile = File("lint-results.xml")
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(xmlFile)
        document.getDocumentElement().normalize()

        val issuesNodeList = document.getElementsByTagName("issues")
        if (issuesNodeList != null && issuesNodeList.length > 0) {
            val issuesElement = issuesNodeList.item(0) as Element
            val issues = Issues()
            for (i in 0 until issuesElement.childNodes.length) {
                val child = issuesElement.childNodes.item(i)
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    val element = child as Element
                    val locationElement =
                        element.getElementsByTagName("location").item(0) as Element
                    val issue = Issue(
                        id = element.getAttribute("id"),
                        severity = element.getAttribute("severity"),
                        message = element.getAttribute("message"),
                        category = element.getAttribute("category"),
                        priority = element.getAttribute("priority"),
                        summary = element.getAttribute("summary"),
                        explanation = element.getAttribute("explanation"),
                        errorLine1 = element.getAttribute("errorLine1"),
                        errorLine2 = element.getAttribute("errorLine2"),
                        location = Location(
                            file = locationElement.getAttribute("file"),
                            line = locationElement.getAttribute("line"),
                            column = locationElement.getAttribute("column")
                        )
                    )
                    if (issue.category == "Warning") {
                        issues.warningList.add(issue)
                    } else if (issue.category == "Error") {
                        issues.errorList.add(issue)
                    }
                }
            }
//            val github = com.jcabi.github.RtGithub(".. your OAuth token ..")
//            val repo = github.repos().get(
//                Coordinates.Simple("octocat/Hello-World")
//            )
//            val issue = repo.issues().create("How are you?", "Please tell me...")
//            issue.comments().post("My first comment!")
        } else {
            System.out.println("There is no lint warnings or errors! :)")
        }
    }
}

tasks {

    // `hello` is a `TaskProvider<GreetingTask>`
    val hello by registering(DebugTask::class) {

    }
    val github by registering(GithubTask::class)
}
