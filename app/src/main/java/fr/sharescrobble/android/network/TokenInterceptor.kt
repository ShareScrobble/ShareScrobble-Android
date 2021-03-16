package fr.sharescrobble.android.network

import android.util.Log
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.core.Globals
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
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

        return chain.proceed(request)
    }
}