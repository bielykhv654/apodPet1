import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.buildkonfig)
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
            baseName = "Core"
            isStatic = true
        }
    }

    jvm("desktop")

    android {
        namespace = "com.example.apodpet.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources {
            enable = true
        }

    }

    sourceSets {
        val desktopMain = getByName("desktopMain")
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(libs.bundles.koin)
            implementation(libs.bundles.room)
            implementation(libs.bundles.ktor)
            implementation(libs.kotlinx.datetime)
        }
        desktopMain.dependencies {
            implementation(libs.ktor.client.logging.jvm)
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
        }
    }
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}
fun getSecret(key: String, defaultValue: String = ""): String {
    return localProperties.getProperty(key)
        ?: providers.gradleProperty(key).orNull
        ?: System.getenv(key)
        ?: defaultValue
}

buildkonfig {
    packageName = "com.example.apodpet.core"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", "api.nasa.gov")
        buildConfigField(FieldSpec.Type.STRING, "NASA_API_KEY", getSecret("NASA_API_KEY"))
    }
    defaultConfigs("dev") {
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", "api.nasa.gov")
        buildConfigField(FieldSpec.Type.STRING, "NASA_API_KEY", getSecret("NASA_API_KEY"))
    }

    defaultConfigs("stage") {
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", "api.nasa.gov")
        buildConfigField(FieldSpec.Type.STRING, "NASA_API_KEY", getSecret("NASA_API_KEY"))
    }
}
dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspDesktop", libs.room.compiler)
}
room {
    schemaDirectory("$projectDir/schemas")
}


