package fr.sharescrobble.android.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import fr.sharescrobble.android.MyApplication
import fr.sharescrobble.android.auth.ui.AuthActivity
import fr.sharescrobble.android.network.models.auth.AuthUrlModel
import fr.sharescrobble.android.network.models.auth.TokensModel
import fr.sharescrobble.android.network.models.auth.JwtModel
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.core.utils.ErrorUtils
import fr.sharescrobble.android.main.ui.MainActivity
import fr.sharescrobble.android.network.repositories.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.nio.charset.StandardCharsets
import java.util.*

object AuthService {
    private val sharedPreferences =
        MyApplication.getCtx().getSharedPreferences("Auth", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    var currentJwtUser: JwtModel? = null
        private set
    var accessToken: String? = null
        private set
    var refreshToken: String? = null
        private set

    init {
        // Restore creds
        this.restoreCredentials()
        this.decodeJWT()

        Log.d(Constants.TAG, "Loaded AuthService");

        if (currentJwtUser != null) {
            Log.d(Constants.TAG, "Currently logged in " + currentJwtUser.toString())
        }
    }

    fun login() {
        // Query the URL to open
        AuthRepository.apiInterface.getAuthUrl().enqueue(object : Callback<AuthUrlModel> {
            override fun onResponse(call: Call<AuthUrlModel>, response: Response<AuthUrlModel>) {
                val body = response.body() ?: return;

                Log.d(Constants.TAG, body.toString())

                // Open intent
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(body.url))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(MyApplication.getCtx(), intent, null)
            }

            override fun onFailure(call: Call<AuthUrlModel>, t: Throwable) {
                // Display an error message
                Log.e(Constants.TAG, t.toString())

                if (t is HttpException) {
                    Toast.makeText(MyApplication.getCtx(), ErrorUtils.parseError(t.response())?.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun loginCallback(ctx: Context, toString: String) {
        // Get base64 token
        val token = toString.replace("sscrobble://auth/", "");

        // Decode it & store it
        Log.d(Constants.TAG, token)
        val json = String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8)
        Log.d(Constants.TAG, json);
        val tokensResponse = Gson().fromJson(json, TokensModel::class.java);
        accessToken = tokensResponse.accessToken
        refreshToken = tokensResponse.refreshToken

        if (accessToken == null || refreshToken == null) {
            throw Error("Failed to decode the JWT")
        }

        this.decodeJWT()
        this.storeCredentials(accessToken, refreshToken)

        Log.d(Constants.TAG, accessToken.toString())
        Log.d(Constants.TAG, currentJwtUser.toString())

        val intent = Intent(ctx, MainActivity::class.java)
        ctx.startActivity(intent)

        return
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
            currentJwtUser = JwtModel(
                parsedJWT.getClaim("id").asInt() ?: 0,
                parsedJWT.getClaim("username").asString() ?: "",
                parsedJWT.issuedAt,
                parsedJWT.expiresAt
            );
        }
    }

    fun logout() {
        // Clean-up the refresh token (and ignore API error)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AuthRepository.apiInterface.revokeToken(refreshToken ?: "")
            } catch (e: Throwable) {}
        }

        this.currentJwtUser = null

        this.clearCredentials()
    }

    fun isAuthenticated(): Boolean {
        return this.currentJwtUser != null
    }
}