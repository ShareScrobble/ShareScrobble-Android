package fr.sharescrobble.android.network.models.users

import java.util.*

data class UserModel(
    val id: Int,
    val username: String,
    val sourceScrobble: String?,
    val startedShared: Date?,
    val createdAt: Date,
    val updatedAt: Date,
)
