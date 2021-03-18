package fr.sharescrobble.android.network.interfaces

import fr.sharescrobble.android.network.models.users.UserModel
import fr.sharescrobble.android.network.models.users.UserScrobbleModel
import retrofit2.http.GET
import retrofit2.http.Path

interface UsersInterface {
    @GET("/users/{userId}")
    suspend fun getUser(@Path("userId") id: Long): UserModel

    @GET("/users/{userId}/scrobbles")
    suspend fun getUserScrobbles(@Path("userId") id: Long): Array<UserScrobbleModel>
}