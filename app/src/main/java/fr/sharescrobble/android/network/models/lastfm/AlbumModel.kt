package fr.sharescrobble.android.network.models.lastfm

import com.google.gson.annotations.SerializedName

data class AlbumModel(
    val mbid: String,
    @SerializedName("#text")
    val text: String,
)
