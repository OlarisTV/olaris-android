package tv.olaris.android.models

import fragment.EpisodeBase
import fragment.SeasonBase

fun test(mi: MediaItem): Boolean{
    return true
}
class Episode(name: String,
                   val overview: String,
                   val stillPath: String,
                   val airDate: String,
                   val episodeNumber: Int,
                   uuid: String) : MediaItem(uuid = uuid, name = name, posterUrl = "olaris/m/images/tmdb/w300/${stillPath}") {

    //lateinit var posterUrl: String

    constructor(base: EpisodeBase) : this(base.name, base.overview, base.stillPath, base.airDate, base.episodeNumber, base.uuid) {
        //this.posterUrl = base.stillPath
    }

    constructor(base: EpisodeBase, seasonBase: SeasonBase?) : this(base.name, base.overview, base.stillPath, base.airDate, base.episodeNumber, base.uuid){
        if(seasonBase != null) {
            if (seasonBase.posterPath != null) {
                this.posterUrl = "olaris/m/images/tmdb/w300/${seasonBase.posterPath}"
            }
            this.subTitle = "S${seasonBase.seasonNumber} E${this.episodeNumber}"

            // TODO: At one point we want this smarter
            this.fileUuid = base.files.first()?.fragments?.fileBase?.uuid.toString()
        }
    }

    val files: MutableList<File> = mutableListOf()


    fun stillPathUrl(baseUrl: String) : String{
        return "${baseUrl}/olaris/m/images/tmdb/w300/${stillPath}"
    }
}