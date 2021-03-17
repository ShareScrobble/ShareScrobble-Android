package fr.sharescrobble.android.network.models.lastfm

data class UserModel(
    val playlists: String,
    val playcount: String,
    val gender: String,
    val name: String,
    val subscriber: String,
    val url: String,
    val country: String,
    val image: Array<ImageModel>,
    val registered: RegisteredModel,
    val type: String,
    val age: String,
    val bootstrap: String,
    val realname: String,
)
