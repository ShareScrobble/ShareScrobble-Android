package fr.sharescrobble.android.network.models

data class ErrorModel(
    val statusCode: Int,
    val message: String,
    val error: String
)
