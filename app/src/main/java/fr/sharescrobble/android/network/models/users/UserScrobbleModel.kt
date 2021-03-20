package fr.sharescrobble.android.network.models.users

import fr.sharescrobble.android.network.models.lastfm.TrackModel
import java.util.*

data class UserScrobbleModel(
    val id: Int,
    val lastFmData: TrackModel,
    val user: UserModel,
    val createdAt: Date,
    val updatedAt: Date,
)
