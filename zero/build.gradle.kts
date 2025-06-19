import extension.setupAnvil
import java.util.Properties

val getEnv: (String) -> Properties = { env ->
    val localProperties = Properties()
    try {
        val propertiesFile = rootProject.file("zero/$env.properties")
        propertiesFile.inputStream().use { localProperties.load(it) }
    } catch (ex: Exception) {
        println(ex.toString())
    }
    localProperties
}

plugins {
    id("io.element.android-compose-library")
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "io.element.android.support.zero"

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            val properties = getEnv("development")
            buildConfigField("String", "WALLET_CONNECT_PROJECT_ID", "\"${properties["wallet_connect_project_id"]}\"")
        }
        release {
            val properties = getEnv("production")
            buildConfigField("String", "WALLET_CONNECT_PROJECT_ID", "\"${properties["wallet_connect_project_id"]}\"")
        }
    }
}

setupAnvil()

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.designsystem)
    implementation(libs.dagger)
    implementation(projects.libraries.di)
    implementation(projects.libraries.matrix.api)

    implementation(libs.coil.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    implementation(libs.androidx.datastore.preferences)
    implementation(platform(libs.network.okhttp.bom))
    implementation(libs.network.okhttp)
    implementation(libs.network.okhttp.logging)
    implementation(platform(libs.network.retrofit.bom))
    implementation(libs.network.retrofit)
    implementation(libs.network.retrofit.converter.serialization)
    implementation(libs.serialization.json)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.remote.configs)
}
