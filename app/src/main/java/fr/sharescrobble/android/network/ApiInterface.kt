package fr.sharescrobble.android.network

import fr.sharescrobble.android.auth.data.AuthModel
import fr.sharescrobble.android.auth.data.RefreshModel
import fr.sharescrobble.android.auth.data.TokensModel
import fr.sharescrobble.android.auth.data.UserModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {
    @GET("auth/lastfm/android")
    fun getAuthUrl(): Call<AuthModel>

    @POST("/auth/refresh")
    fun authRefresh(@Body refreshModel: RefreshModel): Call<TokensModel>

    @GET("/users/{userId}")
    suspend fun getUser(@Path("userId") id: Long): UserModel
}