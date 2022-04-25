package tv.olaris.android.models

@kotlinx.serialization.Serializable
open class MediaItem(
    val uuid: String,
    var posterUrl: String,
    var posterPath: String,
    val name: String,
    val serverId: Int?,
    var runtime: Double = 0.0,
    var subTitle: String = "",
    var fileUuid: String = "",
    var playtime: Double = 0.0,
    var finished: Boolean = false,
    val baseImageUrl: String = "https://image.tmdb.org/t/p/",
) {

    fun hasPosterPath(): Boolean {
        return this.posterPath != ""
    }

    fun playProgress(): Double {
        return ((100 * playtime) / runtime)
    }

    fun hasStarted(): Boolean {
        if (playtime != 0.0) {
            return true
        }

        return false
    }

    fun fullPosterUrl(): String {
        return "${baseImageUrl}/w500/${this.posterPath}"
    }
}
