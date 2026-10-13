
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

compose.resources {
    packageOfResClass = "apodpet.shared.generated.resources"
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm("desktop")
    
    android {
       namespace = "com.example.apodpet.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

       androidResources {
           enable = true
       }

    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.encryptedprefs.ktx)

        }
        commonMain.dependencies {
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.viewmodel.compose)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.mvi)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.bundles.sharedPref)
            implementation(libs.bundles.coil)
            implementation(libs.bundles.paging)
            implementation(libs.mediaplayer)
            implementation(libs.kotlinx.datetime)
            implementation(project(":core"))
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}