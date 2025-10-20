package io.element.android.features.login.impl.screens.extendedOnboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.login.impl.screens.extendedOnboarding.views.ForgotPasswordView
import io.element.android.features.login.impl.screens.extendedOnboarding.views.VerifyOtpView
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.inputs

@ContributesNode(AppScope::class)
@AssistedInject
class ExtendedOnboardingNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: ExtendedOnboardingPresenter.Factory
) : Node(buildContext, plugins = plugins) {

    enum class ExtendedOnboardingFlow {
        VERIFY_OTP,
        FORGOT_PASSWORD
    }

    data class Inputs(
        val flow: ExtendedOnboardingFlow,
        val userEmail: String = ""
    ) : NodeInputs

    private val inputs: Inputs = inputs()

    private val presenter = presenterFactory.create(
        params = inputs,
    )

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        return when (inputs.flow) {
            ExtendedOnboardingFlow.VERIFY_OTP ->
                VerifyOtpView(
                    state = state,
                    onBackClick = ::navigateUp
                )
            ExtendedOnboardingFlow.FORGOT_PASSWORD ->
                ForgotPasswordView(
                    state = state,
                    onBackClick = ::navigateUp
                )
        }
    }
}
