package fr.sharescrobble.android.auth.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
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

        val button: Button = this.findViewById(R.id.buttonLogin)
        button.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                if (base64Token == null) {
                    AuthService.initLogin()
                    return
                }

            }
        })

        if(base64Token != null) {
            Log.d(Globals.TAG, base64Token.toString())
            AuthService.finishLogin(base64Token.toString())
        }
    }
}