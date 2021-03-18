package fr.sharescrobble.android.network.models.lastfm

data class UserFriendModel(
    val playlists: String,
    val playcount: String,
    val subscriber: String,
    val name: String,
    val country: String,
    val image: Array<ImageModel>,
    val registered: RegisteredModel,
    val url: String,
    val realname: String,
    val bootstrap: String,
    val type: String,
)
