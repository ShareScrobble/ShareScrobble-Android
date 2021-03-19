package fr.sharescrobble.android.auth.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import fr.sharescrobble.android.R
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.core.Constants


class AuthActivity : AppCompatActivity() {
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        button = this.findViewById(R.id.buttonLogin)
        button.setOnClickListener {
            run {
                AuthService.login()
            }
        }

        val base64Token = intent.dataString
        if (base64Token != null) {
            Log.d(Constants.TAG, base64Token.toString())
            AuthService.loginCallback(this, base64Token.toString())
        }
    }
}
