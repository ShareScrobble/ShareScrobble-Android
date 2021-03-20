package fr.sharescrobble.android.main.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import fr.sharescrobble.android.R
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.auth.ui.AuthActivity

class SettingsFragment : PreferenceFragmentCompat(),
    ActivityCompat.OnRequestPermissionsResultCallback {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference!!.key) {
            "logout" -> {
                // Logout from the API and the App
                AuthService.logout()
                val intent = Intent(activity, AuthActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear bac stack
                startActivity(intent)
            }

            "gpsTimeout" -> {
                // Only ask when you are checking it (not when unchecking it it's stupid)
                if (findPreference<CheckBoxPreference>(preference.key)!!.isChecked) {
                    // Check we have the permissions or disable
                    // ACCESS_BACKGROUND_LOCATION is only available on Android API 29 or higher
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) !=
                            PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) !=
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            if (shouldShowRequestPermissionRationale(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            ) {
                                requestPermissions(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                    ), 1
                                )
                            } else {
                                requestPermissions(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                    ), 1
                                )
                            }
                        }
                    } else {
                        if (ContextCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) !=
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            if (shouldShowRequestPermissionRationale(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            ) {
                                requestPermissions(
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                                )
                            } else {
                                requestPermissions(
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                                )
                            }
                        }
                    }
                }
            }

            else -> {
                // Do nothing outside of base implementation
                return super.onPreferenceTreeClick(preference)
            }
        }

        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    if (permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION) && permissions.contains(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) && grantResults.any { i -> i != 0 }
                    ) {
                        // User denied
                        Toast.makeText(
                            activity,
                            "You need to allow Location permission & background access to enable this feature",
                            Toast.LENGTH_LONG
                        ).show()

                        // Toggle off
                        findPreference<CheckBoxPreference>("gpsTimeout")?.isChecked = false
                    }
                } else {
                    if (permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults.any { i -> i != 0 }) {
                        // User denied
                        Toast.makeText(
                            activity,
                            "You need to allow Location permission & background access to enable this feature",
                            Toast.LENGTH_LONG
                        ).show()

                        // Toggle off
                        findPreference<CheckBoxPreference>("gpsTimeout")?.isChecked = false
                    }
                }

            }
        }
    }
}