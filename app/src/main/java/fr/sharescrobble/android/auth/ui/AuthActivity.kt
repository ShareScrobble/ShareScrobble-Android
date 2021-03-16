package fr.sharescrobble.android.auth.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fr.sharescrobble.android.R
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.core.Globals

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val base64Token = intent.dataString

        Log.d(Globals.TAG, "Auth")
        Log.d(Globals.TAG, base64Token.toString());

        if (base64Token == null) {
            AuthService.initLogin()
            return
        }

        Log.d(Globals.TAG, base64Token.toString())
        AuthService.finishLogin(base64Token.toString())
    }
}