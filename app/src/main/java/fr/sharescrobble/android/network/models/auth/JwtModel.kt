package fr.sharescrobble.android.network.models.auth

import java.util.*

data class JwtModel(
    var id: Int,
    var username: String,
    var iat: Date?,
    var exp: Date?,
)