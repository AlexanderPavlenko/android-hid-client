package me.arianb.usb_hid_client

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import me.arianb.usb_hid_client.input_views.DirectInput
import me.arianb.usb_hid_client.input_views.DirectInputIconButton
import me.arianb.usb_hid_client.input_views.ManualInput
import me.arianb.usb_hid_client.settings.SettingsScreen
import me.arianb.usb_hid_client.settings.SettingsViewModel
import me.arianb.usb_hid_client.ui.standalone_screens.HelpScreen
import me.arianb.usb_hid_client.ui.standalone_screens.InfoScreen
import me.arianb.usb_hid_client.ui.theme.PaddingNormal
import me.arianb.usb_hid_client.ui.utils.BasicPage
import me.arianb.usb_hid_client.ui.utils.BasicTopBar
import me.arianb.usb_hid_client.ui.utils.DarkLightModePreviews
import timber.log.Timber

class MainScreen : Screen {
    @Composable
    override fun Content() {
        MainPage()
    }
}

@Composable
fun MainPage(
    mainViewModel: MainViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val uiState by mainViewModel.uiState.collectAsState()
    Timber.d("in MainScreen, uiState is: %s", uiState.toString())

    val snackbarHostState = remember { SnackbarHostState() }

    val preferences by settingsViewModel.userPreferencesFlow.collectAsState()

    val padding = PaddingNormal
    BasicPage(
        snackbarHostState = snackbarHostState,
        topBar = { MainTopBar() },

        // The padding below the top app bar is pretty big, so omit top padding
        padding = PaddingValues(start = padding, end = padding, bottom = padding),

        horizontalAlignment = Alignment.CenterHorizontally,

        // I have to manually manage the spacing of elements here, because of the special case of having an invisible
        // View (Direct Input). Otherwise, there's gonna be an awkward spacing created by the invisible View.
        verticalArrangement = Arrangement.Top
    ) {
        ManualInput()
        Spacer(Modifier.height(PaddingNormal))

        // This has to be here, if I move it below Touchpad(), it never gets focused. I think it's because it ends up
        // out of the user's view, so Android just doesn't allow it to gain focus.
        DirectInput()

        LaunchedEffect(uiState) {
            Timber.d("LAUNCHED EFFECT RUNNING WITH UI STATE = %s", uiState.toString())
        }
    }
}

private typealias MenuItem = Pair<Screen, String>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar() {
    val navigator = LocalNavigator.currentOrThrow
    var showDropdownMenu by remember { mutableStateOf(false) }

    BasicTopBar(
        title = stringResource(R.string.app_name),
        actions = {
            DirectInputIconButton()
            IconButton(onClick = { showDropdownMenu = true }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Overflow Menu",
                )
                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false }
                ) {
                    val menuItems = arrayOf(
                        MenuItem(SettingsScreen(), stringResource(R.string.settings)),
                        MenuItem(HelpScreen(), stringResource(R.string.help)),
                        MenuItem(InfoScreen(), stringResource(R.string.info))
                    )
                    for (item in menuItems) {
                        DropdownMenuItem(
                            text = { Text(item.second) },
                            onClick = {
                                // Navigate to screen (safely)
                                //
                                // NOTE:
                                //  Extra code here is necessary because the user can spam click the DropdownMenuItem
                                //  before the navigation has completed. This would lead to it trying to navigate to the
                                //  same screen twice. As of right now, Voyager will crash if this happens without you
                                //  setting unique keys in every Screen. However, even after fixing that, being able
                                //  to navigate to the same screen multiple times is undesirable. For this reason, I have
                                //  added extra code that makes sure the given subclass of Screen isn't already present
                                //  in the navigation stack before we navigate.

                                val thisScreen = item.first

                                // Ensure that the Screen we're about to push isn't already in the navigation stack.
                                // Iterates in reverse because it's more likely for the duplicate item to be at the end.
                                for (screen in navigator.items.reversed()) {
                                    if (screen::class == thisScreen::class) {
                                        return@DropdownMenuItem
                                    }
                                }

                                // Navigate to screen
                                navigator.push(thisScreen)
                                showDropdownMenu = false
                            }
                        )
                    }
                }
            }
        }
    )
}

@DarkLightModePreviews
@Composable
private fun MainScreenPreview() {
    Navigator(MainScreen())
}
