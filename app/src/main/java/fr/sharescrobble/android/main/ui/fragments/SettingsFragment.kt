package fr.sharescrobble.android.main.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import fr.sharescrobble.android.R
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.auth.ui.AuthActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference!!.key) {
            "logout" -> {
                AuthService.logout()
                val intent = Intent(activity, AuthActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear bac stack
                startActivity(intent)
            }

            else -> {
                return super.onPreferenceTreeClick(preference)
            }
        }

        return false
    }
}