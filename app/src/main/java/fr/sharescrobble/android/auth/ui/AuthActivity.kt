package fr.sharescrobble.android.auth.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import fr.sharescrobble.android.R
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.core.Constants


class AuthActivity : AppCompatActivity() {
    // UI references
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.hide()

        // Get references & add listeners
        button = this.findViewById(R.id.buttonLogin)
        button.setOnClickListener {
            run {
                AuthService.login()
            }
        }

        // Process intent data if any
        val base64Token = intent.dataString
        if (base64Token != null) {
            Log.d(Constants.TAG, base64Token.toString())
            AuthService.loginCallback(this, base64Token.toString())
        }
    }
}
