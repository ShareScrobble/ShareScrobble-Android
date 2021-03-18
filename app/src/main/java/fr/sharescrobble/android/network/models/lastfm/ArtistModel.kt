package fr.sharescrobble.android.network.models.lastfm

data class ArtistModel(
    val url: String,
    val mbid: String,
    val name: String,
    val image: Array<ImageModel>,
    val streamable: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArtistModel

        if (url != other.url) return false
        if (mbid != other.mbid) return false
        if (name != other.name) return false
        if (!image.contentEquals(other.image)) return false
        if (streamable != other.streamable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + mbid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + streamable.hashCode()
        return result
    }
}
