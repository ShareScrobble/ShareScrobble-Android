package fr.sharescrobble.android.network.models.lastfm

import com.google.gson.annotations.SerializedName

data class ImageModel(
    val size: String,
    @SerializedName("#text")
    val text: String,
)
