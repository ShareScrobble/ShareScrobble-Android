package fr.sharescrobble.android.auth.data

data class AuthModel(
    var status: String,
    var message: String? = "",
    var url: String? = ""
)
