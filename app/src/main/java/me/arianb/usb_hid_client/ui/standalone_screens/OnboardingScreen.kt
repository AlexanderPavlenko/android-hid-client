package top.flvr.ssh_hid_client.ui.standalone_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import top.flvr.ssh_hid_client.MainScreen
import top.flvr.ssh_hid_client.R
import top.flvr.ssh_hid_client.settings.AppPreference
import top.flvr.ssh_hid_client.settings.SettingsViewModel
import top.flvr.ssh_hid_client.ui.theme.PaddingExtraExtraLarge
import top.flvr.ssh_hid_client.ui.utils.BasicPage
import top.flvr.ssh_hid_client.ui.utils.BasicTopBar
import top.flvr.ssh_hid_client.ui.utils.DarkLightModePreviews

class OnboardingScreen : Screen {
    @Composable
    override fun Content() {
        OnboardingPage()
    }
}

@Composable
fun OnboardingPage() {
    BasicPage(
        topBar = { OnboardingTopBar() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingExtraExtraLarge, Alignment.Top),
    ) {
        OnboardingTitle()
        InfoText()
        ContinueButton()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OnboardingTopBar() {
    BasicTopBar(title = stringResource(R.string.onboarding_title))
}

@Composable
fun OnboardingTitle() {
    Text(
        text = stringResource(R.string.onboarding_header),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun InfoText() {
    Text(
        text = stringResource(R.string.onboarding_notice),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun ContinueButton(settingsViewModel: SettingsViewModel = viewModel()) {
    val navigator = LocalNavigator.currentOrThrow

    Button(
        onClick = {
            settingsViewModel.setPreference(AppPreference.OnboardingDoneKey, true)
            navigator.replace(MainScreen())
        }
    ) {
        Text(stringResource(R.string.onboarding_continue_btn))
    }
}

@DarkLightModePreviews
@Composable
private fun OnboardingScreenPreview() {
    Navigator(OnboardingScreen())
}
