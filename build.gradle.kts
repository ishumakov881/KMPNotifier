plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinNativeCocoaPods) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kotlinx.binary.validator)
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.google.services) apply false
}

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
    ignoredProjects += "sample"
}

allprojects {
    val isJitPack = System.getenv("JITPACK") == "true"
    group = if (isJitPack) "com.github.ishumakov881.KMPNotifier" else "io.github.mirzemehdi"
    version = if (isJitPack) (System.getenv("JITPACK_VERSION") ?: "1.0-SNAPSHOT") else (project.properties["kmpNotifierVersion"] as String)
}

subprojects {
    val excludedModules = listOf(":sample")
    if (project.path in excludedModules) return@subprojects

    apply(plugin = "org.jetbrains.dokka")
}





