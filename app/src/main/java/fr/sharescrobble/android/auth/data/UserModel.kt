package fr.sharescrobble.android.auth.data

import java.util.*

data class UserModel(
    var id: Int,
    var username: String,
    var iat: Date?,
    var exp: Date?,
)