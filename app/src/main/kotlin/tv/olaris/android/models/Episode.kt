package tv.olaris.android.models

import tv.olaris.android.fragment.EpisodeBase
import tv.olaris.android.fragment.SeasonBase

class Episode(
    name: String,
    val overview: String,
    private val stillPath: String,
    val airDate: String,
    val episodeNumber: Int,
    uuid: String,
    serverId: Int? = null,
) : MediaItem(
    uuid = uuid,
    name = name,
    posterPath = stillPath,
    posterUrl = "olaris/m/images/tmdb/w300/${stillPath}",
    serverId = serverId
) {

    //lateinit var posterUrl: String

    constructor(base: EpisodeBase, serverId: Int) :
            this(base.name, base.overview, base.stillPath, base.airDate, base.episodeNumber, base.uuid, serverId)

    constructor(base: EpisodeBase, seasonBase: SeasonBase?, serverId: Int) :
            this(base.name, base.overview, base.stillPath, base.airDate, base.episodeNumber, base.uuid, serverId) {
        if (seasonBase != null) {
            this.posterUrl = "olaris/m/images/tmdb/w300/${seasonBase.posterPath}"
            this.posterPath = seasonBase.posterPath
            this.subTitle = "S${seasonBase.seasonNumber} E${this.episodeNumber}"

            // TODO: At one point we want this smarter
            this.fileUuid = base.files.first()?.fileBase?.uuid.toString()
            this.playtime = base.playState?.playstateBase?.playtime!!
            this.finished = base.playState.playstateBase.finished == true

            if(base.files.isNotEmpty()){
                this.runtime = base.files.first()!!.fileBase.totalDuration!!
            }
        }
    }

    val files: MutableList<File> = mutableListOf()


    fun stillPathUrl() : String {
       // return "${baseUrl}/olaris/m/images/tmdb/w300/${stillPath}"
        return "${baseImageUrl}/w300/${stillPath}"
    }
}