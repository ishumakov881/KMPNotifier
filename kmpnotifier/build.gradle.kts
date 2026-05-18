import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinNativeCocoaPods)
    alias(libs.plugins.mavenPublish)
    signing
}

kotlin {
    explicitApi()
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    js(IR) {
        nodejs()
        browser()
        binaries.library()
    }

    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()


    cocoapods {
        ios.deploymentTarget = "15.4"
        framework {
            baseName = "KMPNotifier"
            isStatic = true
        }
        noPodspec()
        pod("FirebaseMessaging")
    }



    sourceSets {

        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.ktx)
            implementation(libs.firebase.messaging)

        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutine)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

    }
}

android {
    namespace = "com.mmk.kmpnotifier"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

mavenPublishing {
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true
        )
    )
    coordinates(
        project.group.toString(),
        "kmpnotifier",
        project.version.toString()
    )
    pom {
        name = "KMPNotifier"
        description = "Kotlin Multiplatform Push Notification Library targeting ios and android"
        url = "https://github.com/ishumakov881/KMPNotifier/"
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        developers {
            developer {
                id.set("ishumakov881")
                name.set("ishumakov881")
                url.set("https://github.com/ishumakov881")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/ishumakov881/KMPNotifier.git")
            developerConnection.set("scm:git:ssh://git@github.com/ishumakov881/KMPNotifier.git")
            url.set("https://github.com/ishumakov881/KMPNotifier")
        }
        issueManagement {
            system.set("Github")
            url.set("https://github.com/ishumakov881/KMPNotifier/issues")
        }
    }


    publishToMavenCentral(automaticRelease = true)
    val isSigningRequired = project.findProperty("signing.required")?.toString()?.toBoolean() ?: true
    val hasSigningKeys =
        project.hasProperty("signingInMemoryKey") ||
            System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey") != null ||
            project.hasProperty("signing.keyId") ||
            System.getenv("SIGNING_KEY_ID") != null
    
    if (isSigningRequired && hasSigningKeys) {
        signAllPublications()
    }
}

signing {
    val isSigningRequired = project.findProperty("signing.required")?.toString()?.toBoolean() ?: true
    val hasSigningKeys =
        project.hasProperty("signingInMemoryKey") ||
            System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey") != null ||
            project.hasProperty("signing.keyId") ||
            System.getenv("SIGNING_KEY_ID") != null
    isRequired = isSigningRequired && hasSigningKeys
}

