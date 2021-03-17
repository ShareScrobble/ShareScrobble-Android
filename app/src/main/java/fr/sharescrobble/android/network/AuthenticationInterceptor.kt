package fr.sharescrobble.android.network

import android.util.Log
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.core.Globals
import fr.sharescrobble.android.network.models.auth.RefreshTokenModel
import fr.sharescrobble.android.network.repositories.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this) {
            val original = chain.request()

            Log.d(Globals.TAG, original.url().encodedPath())

            // Whitelist some endpoints here
            if (original.url().encodedPath().contains("/auth/refresh")
                || original.url().encodedPath().contains("/auth/logout")
                || original.url().encodedPath().contains("/auth/lastfm")
            ) {
                return chain.proceed(original)
            }

            // No token = we skip the authorization
            if (AuthService.accessToken == null) {
                return chain.proceed(original)
            }

            Log.d(Globals.TAG, "Added a token " + AuthService.accessToken)

            val originalHttpUrl = original.url()
            val requestBuilder = original.newBuilder()
                .addHeader("Authorization", "Bearer " + AuthService.accessToken)
                .url(originalHttpUrl)

            val request = requestBuilder.build()

            val initialResponse = chain.proceed(request)

            when {
                /*initialResponse.code() == 403 || */initialResponse.code() == 401 -> {
                Log.d(Globals.TAG, "Need to refresh")
                Log.d(Globals.TAG, AuthService.refreshToken.toString())

                initialResponse.close()

                // Refresh the JWT
                val tokens = runBlocking {
                    AuthRepository.apiInterface.authRefresh(
                        RefreshTokenModel(
                            AuthService.refreshToken ?: ""
                        )
                    )
                        .execute().body()
                }

                Log.d(Globals.TAG, tokens.toString())
                if (tokens != null) {
                    AuthService.storeCredentials(tokens.accessToken, tokens.refreshToken)
                }

                Log.d(Globals.TAG, "Added a new token " + AuthService.accessToken)

                // Add it to the request
                return chain.proceed(
                    original.newBuilder()
                        .header("Authorization", "Bearer " + AuthService.accessToken)
                        .build()
                )
            }
                else -> return initialResponse
            }
        }
    }
}