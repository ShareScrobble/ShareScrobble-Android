package fr.sharescrobble.android.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import fr.sharescrobble.android.MyApplication
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.core.utils.ErrorUtils
import fr.sharescrobble.android.main.ui.MainActivity
import fr.sharescrobble.android.network.models.auth.AuthUrlModel
import fr.sharescrobble.android.network.models.auth.JwtModel
import fr.sharescrobble.android.network.models.auth.TokensModel
import fr.sharescrobble.android.network.repositories.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        // Restore credentials
        this.restoreCredentials()
        this.decodeJWT()

        Log.d(Constants.TAG, "Loaded AuthService")

        if (currentJwtUser != null) {
            Log.d(Constants.TAG, "Currently logged in " + currentJwtUser.toString())
        }
    }

    /**
     * First part of the OAuth2.0 Login
     */
    fun login() {
        // Query the URL to open
        AuthRepository.apiInterface.getAuthUrl().enqueue(object : Callback<AuthUrlModel> {
            override fun onResponse(call: Call<AuthUrlModel>, response: Response<AuthUrlModel>) {
                val body = response.body() ?: return

                Log.d(Constants.TAG, body.toString())

                // Open Custom Tab
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                customTabsIntent.launchUrl(MyApplication.getCtx(), Uri.parse(body.url))
            }

            override fun onFailure(call: Call<AuthUrlModel>, t: Throwable) {
                // Display an error message
                Log.e(Constants.TAG, t.toString())

                if (t is HttpException) {
                    Toast.makeText(
                        MyApplication.getCtx(),
                        ErrorUtils.parseError(t.response())?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    /**
     * Callback of OAuth2.0 Login in a given [ctx] with an encoded [data]
     */
    fun loginCallback(ctx: Context, data: String) {
        // Get base64 token
        val token = data.replace("sscrobble://auth/", "")

        // Decode it & store it
        Log.d(Constants.TAG, token)
        val json = String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8)
        Log.d(Constants.TAG, json)
        val tokensResponse = Gson().fromJson(json, TokensModel::class.java)
        accessToken = tokensResponse.accessToken
        refreshToken = tokensResponse.refreshToken

        if (accessToken == null || refreshToken == null) {
            throw Error("Failed to decode the JWT")
        }

        // Decode and store data
        this.decodeJWT()
        this.storeCredentials(accessToken, refreshToken)

        Log.d(Constants.TAG, accessToken.toString())
        Log.d(Constants.TAG, currentJwtUser.toString())

        // Redirect to Main
        val intent = Intent(ctx, MainActivity::class.java)
        ctx.startActivity(intent)

        return
    }

    /**
     * Store the tokens ([accessToken] and [refreshToken])
     */
    fun storeCredentials(accessToken: String?, refreshToken: String?) {
        // Persist to memory
        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)
        editor.commit()

        this.accessToken = accessToken ?: ""
        this.refreshToken = refreshToken ?: ""
    }

    /**
     * Restore from persistent memory
     */
    private fun restoreCredentials() {
        this.storeCredentials(
            sharedPreferences.getString("accessToken", null),
            sharedPreferences.getString("refreshToken", null)
        )

        accessToken = sharedPreferences.getString("accessToken", null)
        refreshToken = sharedPreferences.getString("refreshToken", null)
    }

    /**
     * Clear saved credentials (persistent and in memory)
     */
    private fun clearCredentials() {
        this.storeCredentials(null, null)
    }

    /**
     * Decode a JWT to store it into [currentJwtUser]
     */
    private fun decodeJWT() {
        if (accessToken != null) {
            // Parse & store the JWT
            val parsedJWT = JWT(accessToken!!)
            currentJwtUser = JwtModel(
                parsedJWT.getClaim("id").asInt() ?: 0,
                parsedJWT.getClaim("username").asString() ?: "",
                parsedJWT.issuedAt,
                parsedJWT.expiresAt
            )
        }
    }

    /**
     * Logout routine
     */
    fun logout() {
        // Clean-up the refresh token (and ignore API error)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AuthRepository.apiInterface.revokeToken(refreshToken ?: "")
            } catch (e: Throwable) {
            }
        }

        this.currentJwtUser = null

        this.clearCredentials()
    }

    /**
     * Is the User currently authenticated
     */
    fun isAuthenticated(): Boolean {
        return this.currentJwtUser != null
    }
}