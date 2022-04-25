package tv.olaris.android.repositories

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloParseException
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import tv.olaris.android.*
import tv.olaris.android.databases.Server
import tv.olaris.android.models.*
import tv.olaris.android.service.http.OlarisHttpService
import tv.olaris.android.service.http.TokenRefreshAuthenticator

class OlarisGraphQLRepository(
    private val olarisClient: ApolloClient,
    private val okHttpClient: OkHttpClient,
    private val olarisHttpService: OlarisHttpService,
) : KoinComponent {

    // TODO: DRY these two methods up as they are essentially the same thing with a different GrapqhL class. Perhaps use GrapphQL interfaces here? Would need changes on the server side
    suspend fun findRecentlyAddedItems(server: Server): List<MediaItem> {
        val newOlaris = adjustedClient(server)
        val list = mutableListOf<MediaItem>()

        try {
            val res = newOlaris.query(RecentlyAddedQuery()).execute()
            if (res.data != null && !res.data!!.recentlyAdded.isNullOrEmpty()) {
                for (item in res.data!!.recentlyAdded!!) {
                    if (item!!.__typename == "Movie") {
                        val m = item.onMovie!!.movieBase
                        list.add(Movie.createFromGraphQLMovieBase(m, server.id) as MediaItem)
                    } else if (item.__typename == "Episode") {
                        val m = item.onEpisode!!.episodeBase
                        list.add(
                            Episode(
                                m,
                                item.onEpisode.season?.seasonBase,
                                server.id
                            ) as MediaItem
                        )
                    }
                }
            }
        } catch (e: ApolloException) {
            logException("FindRecentlyAddeD", e)
        } catch (e: ApolloParseException) {
            logException("FindRecentlyAddeD parse", e)
        } catch (e: Exception) {
            logException("findRecently ALL", e)
        }
        return list
    }

    suspend fun findContinueWatchingItems(server: Server): List<MediaItem> {
        val newOlaris = adjustedClient(server)
        val list = mutableListOf<MediaItem>()

        try {
            val res = newOlaris.query(ContinueWatchingQuery()).execute()
            if (res.data != null && !res.data!!.upNext.isNullOrEmpty()) {
                for (item in res.data!!.upNext!!) {
                    if (item!!.__typename == "Movie") {
                        val m = item.onMovie!!.movieBase
                        list.add(Movie.createFromGraphQLMovieBase(m, server.id) as MediaItem)
                    } else if (item.__typename == "Episode") {
                        val m = item.onEpisode!!.episodeBase
                        list.add(
                            Episode(
                                m,
                                item.onEpisode.season?.seasonBase,
                                server.id
                            ) as MediaItem
                        )
                    }
                }
            }
        } catch (e: ApolloException) {
            logException("FIndContinueWatch", e)
        } catch (e: ApolloParseException) {
            logException("FIndContinueWatch parse", e)
        } catch (e: Exception) {
            logException("findContinue ALL", e)
        }

        return list
    }

    suspend fun updatePlayState(server: Server, uuid: String, finished: Boolean, playtime: Double) {
        val newOlaris = adjustedClient(server)

        Log.d("playstate", "$playtime.toString(), $uuid, $finished")
        try {
            val m =
                CreatePlayStateMutation(
                    mediaFileUUID = uuid,
                    finished = finished,
                    playtime = playtime
                )
            newOlaris.mutation(m).execute()
        } catch (e: ApolloException) {
            logException("UpdatePlaystate", e)
        }
    }

    suspend fun getStreamingUrl(server: Server, uuid: String): String? {
        val newOlaris = adjustedClient(server)
        val m = CreateStreamingTicketMutation(uuid = uuid)

        try {
            val res = newOlaris.mutation(m).execute()

            if (res.data != null && res.data?.createStreamingTicket != null) {
                return "${server.url}${res.data!!.createStreamingTicket.dashStreamingPath}"
            }

        } catch (e: ApolloException) {
            logException("GetStreamingURL", e)
        }
        return null
    }

    suspend fun findMovieByUUID(server: Server, uuid: String): Movie? {
        val newOlaris = adjustedClient(server)
        var movie: Movie? = null

        try {
            val res = newOlaris.query(FindMovieQuery(uuid = uuid)).execute()
            if (res.data != null && res.data?.movies != null) {
                val m = res.data!!.movies.first()!!
                movie = Movie.createFromGraphQLMovieBase(m.movieBase, server.id)
            }

        } catch (e: ApolloException) {
            logException("FindMovieByUUID", e)
        } catch (e: ApolloParseException) {
            logException("FindMovieByUUID-ParseException", e)

        }

        return movie
    }

    // We expect this can fail but this is being caught in the PagingSource
    suspend fun getMovies(server: Server, limit: Int, offset: Int): List<Movie> {
        val newOlaris = adjustedClient(server)
        val movies: MutableList<Movie> = mutableListOf()

        Log.d("Olaris", "getMovies: Getting movies from GraphQL")
        val res = newOlaris.query(GetMoviesQuery(limit = limit, offset = offset))
            .execute()
        Log.d("Olaris", "getMovies: Done getting movies from GraphQL $res")
        if (res.data != null && res.data?.movies != null) {
            for (movie in res.data!!.movies) {
                val m = movie!!
                movies.add(Movie.createFromGraphQLMovieBase(m.movieBase, server.id))
            }
        }

        return movies.toList()
    }

    suspend fun getAllMovies(server: Server): List<Movie> {
        val newOlaris = adjustedClient(server)
        val movies: MutableList<Movie> = mutableListOf()

        try {
            val res = newOlaris.query(AllMoviesQuery()).execute()

            if (res.data != null && res.data?.movies != null) {
                for (movie in res.data!!.movies) {
                    val m = movie!!
                    movies.add(Movie.createFromGraphQLMovieBase(m.movieBase, server.id))
                }
                return movies.toList()
            }
        } catch (e: ApolloException) {
            logException("getAllMovies", e)
        }

        return movies
    }

    suspend fun findSeasonByUUID(server: Server, uuid: String): Season? {
        val newOlaris = adjustedClient(server)

        try {
            val res = newOlaris.query(FindSeasonQuery(uuid)).execute()
            if (res.data != null) {
                return Show.buildSeason(res.data!!.season.seasonBase, server.id)
            }
        } catch (e: ApolloException) {
            logException("findSeasonByUUID", e)
        }
        return null
    }

    suspend fun getAllShows(server: Server): List<Show> {
        val newOlaris = adjustedClient(server)
        val shows: MutableList<Show> = mutableListOf()

        try {
            val res = newOlaris.query(AllSeriesQuery()).execute()
            Log.d("shows", res.toString())

            if (res.data != null && res.data?.series != null) {
                for (show in res.data!!.series) {
                    val m = show!!
                    Log.d("shows", "Adding show ${m.name}")
                    shows.add(Show.createFromGraphQLSeries(m))
                }
            }
        } catch (e: ApolloException) {
            logException("GetALlShows", e)
        }
        return shows
    }

    suspend fun findShowByUUID(server: Server, uuid: String): Show? {
        val newOlaris = adjustedClient(server)

        try {
            val res = newOlaris.query(FindSeriesQuery(uuid)).execute()
            if (res.data != null && res.data!!.series.isNotEmpty()) {
                return Show.createFromGraphQLSeriesBase(
                    res.data!!.series.first()!!.seriesBase,
                    server.id
                )
            }
        } catch (e: ApolloException) {
            logException("findShowByUUID", e)
        }
        return null
    }

    private fun adjustedClient(server: Server): ApolloClient {
        val newClient = okHttpClient.newBuilder()
            .authenticator(TokenRefreshAuthenticator(olarisHttpService, server))
            .build()
        return olarisClient.newBuilder()
            .okHttpClient(newClient)
            .addHttpHeader("Authorization", "Bearer ${server.currentJWT}")
            .serverUrl("${server.url}/olaris/m/query")
            .build()
    }

    private fun logException(from: String, e: Exception) {
        Log.e("apollo", "Helpie from $from! Error getting data: ${e.localizedMessage}")
        Log.e("apollo", "Cause: ${e.cause}")
        Log.e("apollo", e.toString())
    }
}
