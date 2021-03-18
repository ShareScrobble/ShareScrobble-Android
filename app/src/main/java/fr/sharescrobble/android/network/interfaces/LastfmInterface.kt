package fr.sharescrobble.android.network.interfaces

import fr.sharescrobble.android.network.models.lastfm.TrackModel
import fr.sharescrobble.android.network.models.lastfm.UserFriendModel
import fr.sharescrobble.android.network.models.lastfm.UserModel
import retrofit2.http.GET
import retrofit2.http.Path

interface LastfmInterface {
    @GET("/lastfm/{username}")
    suspend fun getUser(@Path("username") username: String): UserModel

    @GET("/lastfm/{username}/friends")
    suspend fun getFriends(@Path("username") username: String): Array<UserFriendModel>

    @GET("/lastfm/{username}/tracks")
    suspend fun getRecentTracks(@Path("username") username: String): Array<TrackModel>
}