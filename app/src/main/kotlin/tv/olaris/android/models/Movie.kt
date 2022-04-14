package tv.olaris.android.models

import tv.olaris.android.fragment.MovieBase
import kotlin.math.roundToInt

class Movie(
    uuid: String,
    posterUrl: String,
    posterPath: String,
    serverId: Int? = null,
    val backdropPath: String,
    val title: String,
    val year: Int,
    val overview: String,
    val movieBase: MovieBase,
) : MediaItem(
    uuid = uuid,
    posterUrl = posterUrl,
    name = title,
    posterPath = posterPath,
    serverId = serverId,
) {
    var fileUUIDs: MutableList<String> = mutableListOf()

    // TODO: This was before I learned about multiple constructors; refactor
    companion object {
        fun createFromGraphQLMovieBase(m: MovieBase, serverId: Int): Movie {
            val movie = Movie(
                title = m.title,
                year = m.year.toInt(),
                overview = m.overview,
                movieBase = m,
                uuid = m.uuid,
                backdropPath = m.backdropPath,
                posterUrl = m.posterURL,
                posterPath = m.posterPath,
                serverId = serverId,
            )
            if (m.files.isNotEmpty()) {
                for (file in m.files) {
                    if (file != null) {
                        movie.fileUUIDs.add(file.uuid)
                    }
                }
            }
            movie.fileUuid = movie.fileUUIDs.first()

            if (m.files.isNotEmpty()) {
                movie.runtime = m.files.first()!!.totalDuration!!
            }
            movie.playtime = m.playState?.playstateBase?.playtime!!
            movie.subTitle = movie.year.toString()
            return movie
        }
    }

    fun getFileName(): String {
        val file = movieBase.files.first()
        if (file != null) {
            return file.fileName
        }
        return "Unknown"
    }

    fun getRuntime(): Int {
        val file = movieBase.files.first()
        if (file != null) {
            val minutes = (file.totalDuration!! / 60).roundToInt()
            return minutes
        }

        return 0
    }

    fun fullCoverArtUrl(): String {
        //"${baseImageUrl}/olaris/m/images/tmdb/w1280/${this.backdropPath}"
        return "${baseImageUrl}/w1280/${this.backdropPath}"
    }

    fun getResolution(): String {
        val file = this.movieBase.files.first()
        if (file != null && file.streams.isNotEmpty()) {
            val videoStreams = file.streams.filter {
                if (it != null) {
                    it.streamBase.streamType == "video"
                } else {
                    false
                }
            }

            if (videoStreams.isNotEmpty()) {
                return videoStreams.first()?.streamBase?.resolution.toString()
            }
        }
        return "unknown"
    }
}

