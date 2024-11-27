plugins {
    id("io.element.android-library")
}

android {
    namespace = "io.element.android.features.zeroinvite.api"
}

dependencies {
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.matrix.api)
}
