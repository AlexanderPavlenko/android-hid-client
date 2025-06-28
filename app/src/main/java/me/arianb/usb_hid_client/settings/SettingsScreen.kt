package me.arianb.usb_hid_client.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import me.arianb.usb_hid_client.R
import me.arianb.usb_hid_client.settings.AppSettings.AppThemePreference
import me.arianb.usb_hid_client.settings.AppSettings.ClearManualInputOnSend
import me.arianb.usb_hid_client.settings.AppSettings.DynamicColors
import me.arianb.usb_hid_client.settings.AppSettings.MediaKeyPassthrough
import me.arianb.usb_hid_client.settings.AppSettings.PreferenceCategory
import me.arianb.usb_hid_client.settings.AppSettings.RemoteHost
import me.arianb.usb_hid_client.settings.AppSettings.RemoteUser
import me.arianb.usb_hid_client.settings.AppSettings.RemotePassword
import me.arianb.usb_hid_client.ui.theme.PaddingNormal
import me.arianb.usb_hid_client.ui.theme.isDynamicColorAvailable
import me.arianb.usb_hid_client.ui.utils.BasicPage
import me.arianb.usb_hid_client.ui.utils.DarkLightModePreviews
import me.arianb.usb_hid_client.ui.utils.SimpleNavTopBar

class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        SettingsPage()
    }
}

@Composable
fun SettingsPage() {
    val padding = PaddingNormal

    BasicPage(
        topBar = { SettingsTopBar() },
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(padding, Alignment.Top),
        scrollable = true
    ) {
        PreferenceCategory(
            title = stringResource(R.string.theme_header),
        ) {
            AppThemePreference()

            if (isDynamicColorAvailable()) {
                DynamicColors()
            }
        }
        PreferenceCategory(
            title = stringResource(R.string.direct_input),
        ) {
            MediaKeyPassthrough()
        }
        PreferenceCategory(
            title = stringResource(R.string.manual_input),
        ) {
            ClearManualInputOnSend()
        }
        PreferenceCategory(
            title = "SSH",
        ) {
            RemoteHost()
            RemoteUser()
            RemotePassword()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar() {
    SimpleNavTopBar(
        title = stringResource(R.string.settings)
    )
}

// Specializations of the generic preference composable "helper" functions
private object AppSettings {
    @Composable
    fun PreferenceCategory(
        title: String,
        showDivider: Boolean = true,
        preferences: @Composable (() -> Unit)
    ) {
        val paddingModifier = Modifier.padding(horizontal = PaddingNormal)

        PreferenceCategory(
            title = title,
            modifier = paddingModifier,
            showDivider = showDivider,
            preferences = preferences,
        )
    }

    @Composable
    fun AppThemePreference(
        enabled: Boolean = true,
        settingsViewModel: SettingsViewModel = viewModel()
    ) {
        val preferencesState by settingsViewModel.userPreferencesFlow.collectAsState()

        val selectedTheme = preferencesState.appTheme
        val options = AppTheme.values
        BasicListPreference(
            title = stringResource(R.string.app_theme_title),
            options = options,
            enabled = enabled,
            selected = selectedTheme,
            onPreferenceClicked = { thisAppTheme ->
                settingsViewModel.setPreference(AppPreference.AppThemeKey, thisAppTheme)
            }
        )
    }

    @Composable
    fun DynamicColors() {
        SwitchPreference(
            title = stringResource(R.string.dynamic_colors_title),
            preference = AppPreference.DynamicColorKey
        )
    }

    @Composable
    fun MediaKeyPassthrough() {
        SwitchPreference(
            title = stringResource(R.string.volume_button_passthrough_title),
            summary = stringResource(R.string.volume_button_passthrough_summary),
            preference = AppPreference.VolumeButtonPassthroughKey
        )
    }

    @Composable
    fun ClearManualInputOnSend() {
        SwitchPreference(
            title = stringResource(R.string.clear_manual_input_title),
            preference = AppPreference.ClearManualInputKey
        )
    }

    @Composable
    fun RemoteHost() {
        TextPreference(
            title = "Host",
            preference = AppPreference.RemoteHostKey,
        )
    }

    @Composable
    fun RemoteUser() {
        TextPreference(
            title = "User",
            preference = AppPreference.RemoteUserKey,
        )
    }

    @Composable
    fun RemotePassword() {
        TextPreference(
            title = "Password",
            preference = AppPreference.RemotePasswordKey,
            masked = true,
        )
    }
}

@DarkLightModePreviews
@Composable
private fun SettingsScreenPreview() {
    Navigator(SettingsScreen())
}
