package tv.olaris.android.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Season(
    val name: String,
    val overview: String,
    val seasonNumber: Int,
    val airDate: String,
    val posterPath: String,
    val uuid: String,
    val unwatchedEpisodesCount: Int,
) {
    val episodes: MutableList<@Contextual Episode> = mutableListOf()
}

