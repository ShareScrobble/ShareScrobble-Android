package fr.sharescrobble.android.network

import android.util.Log
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.auth.data.AuthRepository
import fr.sharescrobble.android.auth.data.RefreshModel
import fr.sharescrobble.android.core.Globals
import kotlinx.coroutines.runBlocking
import okhttp3.*

class MyAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d(Globals.TAG, "Need to refresh")
        Log.d(Globals.TAG, AuthService.refreshToken.toString())

        // Refresh the JWT
        val tokens =
            ApiRepository.apiInterface?.authRefresh(RefreshModel(AuthService.refreshToken ?: ""))
                ?.execute()
                ?.body()
        Log.d(Globals.TAG, tokens.toString())
        if (tokens != null) {
            AuthService.storeCredentials(tokens.accessToken, tokens.refreshToken)
        }

        Log.d(Globals.TAG, "Added a new token " + AuthService.accessToken)

        // Add it to the request
        return response.request().newBuilder()
            .header("Authorization", "Bearer " + AuthService.accessToken)
            .build()
    }
}