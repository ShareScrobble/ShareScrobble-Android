package fr.sharescrobble.android.network.interfaces

import fr.sharescrobble.android.network.models.auth.AuthUrlModel
import fr.sharescrobble.android.network.models.auth.RefreshTokenModel
import fr.sharescrobble.android.network.models.auth.TokensModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthInterface {
    @GET("auth/lastfm/android")
    fun getAuthUrl(): Call<AuthUrlModel>

    @POST("/auth/refresh")
    fun authRefresh(@Body refreshTokenModel: RefreshTokenModel): Call<TokensModel>

    @POST("/auth/revoke/{refreshToken}")
    suspend fun revokeToken(@Path("refreshToken") refreshToken: String)
}