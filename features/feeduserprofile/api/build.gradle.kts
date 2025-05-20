plugins {
    id("io.element.android-library")
}

android {
    namespace = "io.element.android.features.feeduserprofile.api"
}

dependencies {
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.matrix.api)
}
