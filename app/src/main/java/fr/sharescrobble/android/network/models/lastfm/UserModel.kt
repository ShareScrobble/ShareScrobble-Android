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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserModel

        if (playlists != other.playlists) return false
        if (playcount != other.playcount) return false
        if (gender != other.gender) return false
        if (name != other.name) return false
        if (subscriber != other.subscriber) return false
        if (url != other.url) return false
        if (country != other.country) return false
        if (!image.contentEquals(other.image)) return false
        if (registered != other.registered) return false
        if (type != other.type) return false
        if (age != other.age) return false
        if (bootstrap != other.bootstrap) return false
        if (realname != other.realname) return false

        return true
    }

    override fun hashCode(): Int {
        var result = playlists.hashCode()
        result = 31 * result + playcount.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + subscriber.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + registered.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + age.hashCode()
        result = 31 * result + bootstrap.hashCode()
        result = 31 * result + realname.hashCode()
        return result
    }
}
