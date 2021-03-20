package fr.sharescrobble.android.network.interfaces

import retrofit2.http.PATCH
import retrofit2.http.Path

interface ScrobbleInterface {
    @PATCH("/scrobble/{username}/subscribe")
    suspend fun subscribe(@Path("username") username: String)

    @PATCH("/scrobble/{username}/unsubscribe")
    suspend fun unsubscribe(@Path("username") username: String)
}