package fr.sharescrobble.android.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import fr.sharescrobble.android.MyApplication
import fr.sharescrobble.android.auth.data.AuthModel
import fr.sharescrobble.android.auth.data.AuthRepository
import fr.sharescrobble.android.auth.data.TokensModel
import fr.sharescrobble.android.auth.data.UserModel
import fr.sharescrobble.android.core.Globals
import kotlinx.coroutines.coroutineScope
import java.nio.charset.StandardCharsets
import java.util.*

object AuthService {
    private val sharedPreferences =
        MyApplication.getCtx().getSharedPreferences("Auth", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    var currentUser: UserModel? = null
        private set
    var accessToken: String? = null
        private set
    var refreshToken: String? = null
        private set

    private var authRepository = AuthRepository()

    init {
        // Restore creds
        this.restoreCredentials()
        this.decodeJWT()

        Log.d(Globals.TAG, "Loaded AuthService");

        if (currentUser != null) {
            Log.d(Globals.TAG, "Currently logged in " + currentUser.toString())
        }
    }

    fun initLogin() {
        // Query the URL to open
        authRepository.getAuthUrl()
    }

    fun login(body: AuthModel?) {
        if (body == null) return

        Log.d(Globals.TAG, body.toString())

        // Open intent
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(body.url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(MyApplication.getCtx(), intent, null)
    }

    fun finishLogin(toString: String) {
        // Get base64 token
        val token = toString.replace("sscrobble://auth/", "");

        // Decode it & store it
        Log.d(Globals.TAG, token)
        val json = String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8)
        Log.d(Globals.TAG, json);
        val tokensResponse = Gson().fromJson(json, TokensModel::class.java);
        accessToken = tokensResponse.accessToken
        refreshToken = tokensResponse.refreshToken

        if (accessToken == null || refreshToken == null) {
            TODO("Error here")
        }

        this.decodeJWT()
        this.storeCredentials(accessToken, refreshToken)

        Log.d(Globals.TAG, accessToken.toString())
        Log.d(Globals.TAG, currentUser.toString())
    }

    fun storeCredentials(accessToken: String?, refreshToken: String?) {
        // Persist to memory
        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)
        editor.commit()

        this.accessToken = accessToken ?: ""
        this.refreshToken = refreshToken ?: ""
    }

    private fun restoreCredentials() {
        this.storeCredentials(
            sharedPreferences.getString("accessToken", null),
            sharedPreferences.getString("refreshToken", null)
        )

        accessToken = sharedPreferences.getString("accessToken", null)
        refreshToken = sharedPreferences.getString("refreshToken", null)
    }

    private fun clearCredentials() {
        this.storeCredentials(null, null)
    }

    private fun decodeJWT() {
        if (accessToken != null) {
            // Parse & store the JWT
            val parsedJWT = JWT(accessToken!!);
            currentUser = UserModel(
                parsedJWT.getClaim("id").asInt() ?: 0,
                parsedJWT.getClaim("username").asString() ?: "",
                parsedJWT.issuedAt,
                parsedJWT.expiresAt
            );
        }
    }

    fun logout() {
        this.currentUser = null

        this.clearCredentials()
    }

    fun isAuthenticated(): Boolean {
        return this.currentUser != null
    }
}