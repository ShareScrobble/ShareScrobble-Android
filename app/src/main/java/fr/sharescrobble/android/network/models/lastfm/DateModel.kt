package fr.sharescrobble.android.network.models.lastfm

import com.google.gson.annotations.SerializedName

data class DateModel(
    val uts: String,
    @SerializedName("#text")
    val text : String,
)
