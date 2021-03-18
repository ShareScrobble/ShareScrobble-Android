package fr.sharescrobble.android.main.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import fr.sharescrobble.android.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}