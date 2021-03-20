package fr.sharescrobble.android.network.models.lastfm

import com.google.gson.annotations.SerializedName

data class RegisteredModel(
    val unixtimeval: String,
    @SerializedName("#text")
    val text: String,
)
