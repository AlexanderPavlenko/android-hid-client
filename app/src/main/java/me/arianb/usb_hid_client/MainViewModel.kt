package me.arianb.usb_hid_client

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.arianb.usb_hid_client.report_senders.KeySender
import me.arianb.usb_hid_client.settings.AppPreference
import me.arianb.usb_hid_client.settings.UserPreferencesRepository
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Data class that represents the UI state
 */
data class MyUiState(
    // Character Device Stuff
    val missingCharacterDevice: Boolean = false,
    val isCharacterDevicePermissionsBroken: String? = null,

    // Other Stuff
    val isDeviceUnplugged: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState

    private val userPreferencesStateFlow =
        UserPreferencesRepository.getInstance(application).userPreferencesFlow

    val keySender: StateFlow<KeySender> = userPreferencesStateFlow
        .mapState {
            KeySender()
        }

    private val senderFlowList = listOf(keySender)

    init {
        senderFlowList.forEach { senderFlow ->
            viewModelScope.launch {
                senderFlow.collectLatest { sender ->
                    val prefs = UserPreferencesRepository.getInstance(application)
                    try {
                        sender.start(
                            host = prefs.getPreference(AppPreference.RemoteHostKey),
                            user = prefs.getPreference(AppPreference.RemoteUserKey),
                            password = prefs.getPreference(AppPreference.RemotePasswordKey),
                            onSuccess = {
                                // This is called when no exception was thrown, meaning everything is good :)
                                // so let's set the UI state back to default (no errors)
                                _uiState.update { MyUiState() }
                            },
                            onException = { e ->
                                if (e is FileNotFoundException) {
                                    Timber.i("Character device doesn't exist. The user probably skipped the character device creation prompt.")
                                } else {
                                    handleException(e)
                                }
                            }
                        )
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }
    }

    private fun handleException(e: IOException) {
        val exceptionString = e.message ?: Log.getStackTraceString(e)
        val lowercaseExceptionString = exceptionString.lowercase()

        if (lowercaseExceptionString.contains("errno 108")) {
            Timber.i("device might be unplugged")
            _uiState.update { it.copy(isDeviceUnplugged = true) }
        } else if (lowercaseExceptionString.contains("permission denied")) {
            Timber.i("char dev perms are wrong")
            _uiState.update { it.copy(isCharacterDevicePermissionsBroken = "") }
        } else if (lowercaseExceptionString.contains("enxio")) {
            Timber.i("somehow the HID gadget is disabled but the character devices are still present")
        } else {
            Timber.e(e)
            Timber.e("unknown error has occurred while trying to write to character device")
//            showSnackbar("ERROR: Failed to send mouse report.", Snackbar.LENGTH_SHORT)
        }

        Timber.d("in MainViewModel, new state is: %s", uiState.value.toString())
    }

    // Keyboard
    fun addStandardKey(modifier: Byte, key: Byte) =
        keySender.value.addStandardKey(modifier, key)

    fun addMediaKey(key: Byte) =
        keySender.value.addMediaKey(key)

    private inline fun <T, R> StateFlow<T>.mapState(
        crossinline transform: (value: T) -> R
    ) = mapState(viewModelScope, transform)
}
