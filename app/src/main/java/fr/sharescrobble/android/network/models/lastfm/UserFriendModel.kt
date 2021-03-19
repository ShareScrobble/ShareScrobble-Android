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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserFriendModel

        if (playlists != other.playlists) return false
        if (playcount != other.playcount) return false
        if (subscriber != other.subscriber) return false
        if (name != other.name) return false
        if (country != other.country) return false
        if (!image.contentEquals(other.image)) return false
        if (registered != other.registered) return false
        if (url != other.url) return false
        if (realname != other.realname) return false
        if (bootstrap != other.bootstrap) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = playlists.hashCode()
        result = 31 * result + playcount.hashCode()
        result = 31 * result + subscriber.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + registered.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + realname.hashCode()
        result = 31 * result + bootstrap.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}
