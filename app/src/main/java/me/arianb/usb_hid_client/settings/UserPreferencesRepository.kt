package me.arianb.usb_hid_client.settings

import android.app.Application
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.arianb.usb_hid_client.R

sealed class AppPreference(val preference: PreferenceKey<*>) {
    data object OnboardingDoneKey : BooleanPreferenceKey("onboarding_done", false)
    data object ClearManualInputKey : BooleanPreferenceKey("clear_manual_input", false)
    data object VolumeButtonPassthroughKey : BooleanPreferenceKey("volume_button_passthrough", false)
    data object AppThemeKey : ObjectPreferenceKey<AppTheme>(
        "app_theme", AppTheme.System,
        fromStringPreference = {
            val defaultValue = AppTheme.System

            when (it) {
                AppTheme.System.key -> AppTheme.System
                AppTheme.DarkMode.key -> AppTheme.DarkMode
                AppTheme.LightMode.key -> AppTheme.LightMode
                else -> defaultValue
            }
        },
        toStringPreference = { it.key }
    )

    data object DynamicColorKey : BooleanPreferenceKey("dynamic_color", false)
    data object RemoteHostKey : StringPreferenceKey("remote_host", "")
    data object RemoteUserKey : StringPreferenceKey("remote_user", "")
    data object RemotePasswordKey : StringPreferenceKey("remote_password", "")
}

sealed class SealedString(val key: String, @StringRes val id: Int)

sealed class AppTheme(key: String, @StringRes id: Int) : SealedString(key, id) {
    data object System : AppTheme("system", R.string.app_theme_system)
    data object LightMode : AppTheme("light", R.string.app_theme_light_mode)
    data object DarkMode : AppTheme("dark", R.string.app_theme_dark_mode)

    companion object {
        val values: List<AppTheme>
            get() = listOf(
                System,
                LightMode,
                DarkMode
            )
    }
}

data class UserPreferences(
    val isOnboardingDone: Boolean,
    val clearManualInput: Boolean,
    val isVolumeButtonPassthroughEnabled: Boolean,
    val appTheme: AppTheme,
    val isDynamicColorEnabled: Boolean,
    val remoteHost: String,
    val remoteUser: String,
    val remotePassword: String
)

class UserPreferencesRepository private constructor(application: Application) {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    private val _userPreferencesFlow = MutableStateFlow(userPreferences)
    val userPreferencesFlow: StateFlow<UserPreferences> = _userPreferencesFlow

    private fun <T> PreferenceKey<T>.getValue() = this.getValue(sharedPreferences)
    private fun <T> PreferenceKey<T>.setValue(value: T) = this.setValue(sharedPreferences, value)
    private fun <T> PreferenceKey<T>.resetToDefault() = this.resetToDefault(sharedPreferences)

    private val userPreferences: UserPreferences
        get() {
            return UserPreferences(
                isOnboardingDone = AppPreference.OnboardingDoneKey.getValue(),
                clearManualInput = AppPreference.ClearManualInputKey.getValue(),
                isVolumeButtonPassthroughEnabled = AppPreference.VolumeButtonPassthroughKey.getValue(),
                appTheme = AppPreference.AppThemeKey.getValue(),
                isDynamicColorEnabled = AppPreference.DynamicColorKey.getValue(),
                remoteHost = AppPreference.RemoteHostKey.getValue(),
                remoteUser = AppPreference.RemoteUserKey.getValue(),
                remotePassword = AppPreference.RemotePasswordKey.getValue()
            )
        }

    fun <T> getPreference(key: PreferenceKey<T>): T =
        key.getValue()

    fun <T> setPreference(key: PreferenceKey<T>, value: T) {
        key.setValue(value)
        _userPreferencesFlow.update { userPreferences }
    }

    fun <T> resetPreferenceToDefault(key: PreferenceKey<T>) {
        key.resetToDefault()
        _userPreferencesFlow.update { userPreferences }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null

        fun getInstance(application: Application): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }

                val instance = UserPreferencesRepository(application)
                INSTANCE = instance
                instance
            }
        }
    }
}
