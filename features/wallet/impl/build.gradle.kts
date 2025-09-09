import extension.setupDependencyInjection

plugins {
    id("io.element.android-compose-library")
    id("kotlin-parcelize")
}

android {
    namespace = "io.element.android.features.wallet.impl"
}

setupDependencyInjection()

dependencies {
    api(projects.features.wallet.api)
    implementation(projects.libraries.core)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.matrix.api)
    implementation(projects.libraries.matrixui)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.uiStrings)
    implementation(projects.features.login.impl)
    implementation(projects.zero)

    implementation(libs.coil.compose)

    implementation(platform(libs.walletconnect.bom))
    implementation(libs.walletconnect.androidcore)
    implementation(libs.walletconnect.appkit)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.accompanist.navigation)

    testImplementation(libs.test.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.molecule.runtime)
    testImplementation(libs.test.truth)
    testImplementation(libs.test.turbine)
    testImplementation(projects.libraries.matrix.test)
    testImplementation(projects.libraries.sessionStorage.test)
    testImplementation(projects.tests.testutils)
}
