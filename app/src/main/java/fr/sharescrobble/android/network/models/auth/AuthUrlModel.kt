package fr.sharescrobble.android.network.models.auth

data class AuthUrlModel(
    var status: String,
    var message: String? = "",
    var url: String? = ""
)
