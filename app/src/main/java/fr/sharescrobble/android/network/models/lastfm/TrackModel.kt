package fr.sharescrobble.android.network.models.lastfm

data class TrackModel(
    val url: String,
    val date: DateModel,
    val mbid: String,
    val name: String,
    val album: AlbumModel,
    val image: Array<ImageModel>,
    val loved: String,
    val artist: ArtistModel,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackModel

        if (url != other.url) return false
        if (date != other.date) return false
        if (mbid != other.mbid) return false
        if (name != other.name) return false
        if (album != other.album) return false
        if (!image.contentEquals(other.image)) return false
        if (loved != other.loved) return false
        if (artist != other.artist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + mbid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + loved.hashCode()
        result = 31 * result + artist.hashCode()
        return result
    }
}
