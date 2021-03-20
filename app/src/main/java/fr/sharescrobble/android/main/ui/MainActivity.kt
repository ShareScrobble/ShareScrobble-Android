package fr.sharescrobble.android.main.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.sharescrobble.android.R
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.auth.ui.AuthActivity
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.main.ui.fragments.FriendsFragment
import fr.sharescrobble.android.main.ui.fragments.HistoryFragment
import fr.sharescrobble.android.main.ui.fragments.HomeFragment
import fr.sharescrobble.android.main.ui.fragments.SettingsFragment

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private val fm: FragmentManager = supportFragmentManager

    // UI References
    private lateinit var mainFrame: FrameLayout
    private lateinit var bottomNavigationView: BottomNavigationView

    private val homeFragment: HomeFragment = HomeFragment()
    private val friendsFragment: FriendsFragment = FriendsFragment()
    private val historyFragment: HistoryFragment = HistoryFragment()
    private val settingsFragment: SettingsFragment = SettingsFragment()
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Security check
        if (!AuthService.isAuthenticated()) {
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

            return
        }

        // UI references queries
        mainFrame = findViewById(R.id.main_frame)
        bottomNavigationView = findViewById(R.id.main_bottom_navigation)

        val intent = intent
        when (intent.action) {
            "UNSUBSCRIBE" -> {
                homeFragment.toRemoveNotification = true
            }
        }

        // Setup bottom navigation
        fm.beginTransaction().add(R.id.main_frame, settingsFragment).hide(settingsFragment).commit()
        fm.beginTransaction().add(R.id.main_frame, historyFragment).hide(historyFragment).commit()
        fm.beginTransaction().add(R.id.main_frame, friendsFragment).hide(friendsFragment).commit()
        fm.beginTransaction().add(R.id.main_frame, homeFragment).commit()

        this.configureBottomMenu()
    }

    /**
     * Configure the [BottomNavigationView]
     */
    private fun configureBottomMenu() {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            updateMainFragment(item.itemId)
        }
    }

    /**
     * Navigation done programmatically using [id]
     */
    fun navigateTo(id: Int) {
        bottomNavigationView.selectedItemId = id
    }

    /**
     * Update the displayed Fragment using its [id]
     */
    private fun updateMainFragment(id: Int): Boolean {

        when (id) {
            R.id.action_home -> {
                Log.d(Constants.TAG, "Clicked home")
                fm.beginTransaction().hide(activeFragment).show(homeFragment).commit()
                activeFragment = homeFragment
            }
            R.id.action_friends -> {
                Log.d(Constants.TAG, "Clicked friends")
                fm.beginTransaction().hide(activeFragment).show(friendsFragment).commit()
                activeFragment = friendsFragment
            }
            R.id.action_history -> {
                Log.d(Constants.TAG, "Clicked history")
                fm.beginTransaction().hide(activeFragment).show(historyFragment).commit()
                activeFragment = historyFragment
            }
            R.id.action_settings -> {
                Log.d(Constants.TAG, "Clicked settings")
                fm.beginTransaction().hide(activeFragment).show(settingsFragment).commit()
                activeFragment = settingsFragment
            }
            else -> {
                return false
            }
        }

        return true
    }

    /**
     * Forward the onRequestPermissionsResult to the activity
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}