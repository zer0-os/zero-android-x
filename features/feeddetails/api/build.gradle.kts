plugins {
    id("io.element.android-library")
}

android {
    namespace = "io.element.android.features.feeddetails.api"
}

dependencies {
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.matrix.api)
}
