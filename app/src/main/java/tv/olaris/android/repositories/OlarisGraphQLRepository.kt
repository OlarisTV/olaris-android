package tv.olaris.android.repositories

import AllMoviesQuery
import AllSeriesQuery
import ContinueWatchingQuery
import CreatePlayStateMutation
import CreateStreamingTicketMutation
import FindMovieQuery
import FindSeasonQuery
import FindSeriesQuery
import GetMoviesQuery
import RecentlyAddedQuery
import android.util.Log
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.olaris.android.OlarisApplication
import tv.olaris.android.databases.Server
import tv.olaris.android.models.*
import tv.olaris.android.service.graphql.GraphqlClient

class OlarisGraphQLRepository(private var server: Server) {
    private val olarisClient: GraphqlClient = OlarisApplication.applicationContext().getClient(server)

    // TODO: DRY these two methods up as they are essentially the same thing with a different GrapqhL class. Perhaps use GrapphQL interfaces here? Would need changes on the server side
    suspend fun findRecentlyAddedItems(): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        try {
            val res = olarisClient.get().query(RecentlyAddedQuery()).await()
            if (res.data != null && !res.data!!.recentlyAdded.isNullOrEmpty()) {
                for (item in res.data!!.recentlyAdded!!) {
                    if (item!!.__typename == "Movie") {
                        val m = item.asMovie!!.fragments.movieBase
                        list.add(Movie.createFromGraphQLMovieBase(m, server.id) as MediaItem)
                    } else if (item.__typename == "Episode") {
                        val m = item.asEpisode!!.fragments.episodeBase
                        list.add(
                            Episode(
                                m,
                                item.asEpisode.season?.fragments?.seasonBase,
                                server.id
                            ) as MediaItem
                        )
                    }
                }
            }
        } catch (e: ApolloException) {
            logException("FindRecentlyAddeD", e)
        }catch(e: ApolloParseException){
            logException("FindRecentlyAddeD parse", e)
        }
        return list
    }

    suspend fun findContinueWatchingItems(): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        try {
            val res = olarisClient.get().query(ContinueWatchingQuery()).await()
            if (res.data != null && !res.data!!.upNext.isNullOrEmpty()) {
                for (item in res.data!!.upNext!!) {
                    if (item!!.__typename == "Movie") {
                        val m = item.asMovie!!.fragments.movieBase
                        list.add(Movie.createFromGraphQLMovieBase(m, server.id) as MediaItem)
                    } else if (item.__typename == "Episode") {
                        val m = item.asEpisode!!.fragments.episodeBase
                        list.add(
                            Episode(
                                m,
                                item.asEpisode.season?.fragments?.seasonBase,
                                server.id
                            ) as MediaItem
                        )
                    }
                }
            }
        } catch (e: ApolloException) {
            logException("FIndContinueWatch", e)
        } catch(e: ApolloParseException){
            logException("FIndContinueWatch parse", e)
        }

        return list
    }

    suspend fun updatePlayState(uuid: String, finished: Boolean, playtime: Double) {
        Log.d("playstate", "$playtime.toString(), $uuid, $finished")
        try {
            val m =
                CreatePlayStateMutation(
                    mediaFileUUID = uuid,
                    finished = finished,
                    playtime = playtime
                )
            olarisClient.get().mutate(m).await()
        } catch (e: ApolloException) {
            logException("UpdatePlaystate", e)
        }
    }

    suspend fun getStreamingUrl(uuid: String): String? {
        val m = CreateStreamingTicketMutation(uuid = uuid)
        try {
            val res = olarisClient.get().mutate(m).await()

            if (res.data != null && res.data?.createStreamingTicket != null) {
                return "${server.url}${res.data!!.createStreamingTicket.dashStreamingPath}"
            }

        } catch (e: ApolloException) {
            logException("GetStreamingURL",e)
        }
        return null
    }

    suspend fun findMovieByUUID(uuid: String): Movie? = withContext(Dispatchers.IO) {
        var movie: Movie? = null
        try {
            val res = olarisClient.get().query(FindMovieQuery(uuid = uuid)).await()
            if (res.data != null && res.data?.movies != null) {
                var m = res.data!!.movies.first()!!
                movie = Movie.createFromGraphQLMovieBase(m.fragments.movieBase, server.id)
            }

        } catch (e: ApolloException) {
            logException("FindMovieByUUID", e)
        }catch (e: ApolloParseException){
            logException("FindMovieByUUID-ParseException", e)

        }

        return@withContext movie
    }

    // We expect this can fail but this is being caught in the PagingSource
    suspend fun getMovies(limit: Int, offset: Int): List<Movie> = withContext(Dispatchers.IO){
        var movies: MutableList<Movie> = mutableListOf()

        Log.d("Olaris", "getMovies: Getting movies from GraphQL")
        val res = olarisClient.get().query(GetMoviesQuery(limit = limit,offset = offset)).await()
        Log.d("Olaris", "getMovies: Done getting movies from GraphQL $res")
        if (res.data != null && res.data?.movies != null) {
            for (movie in res.data!!.movies) {
                val m = movie!!
                movies.add(Movie.createFromGraphQLMovieBase(m.fragments.movieBase, server.id))
            }
        }

        return@withContext movies.toList()
    }

    suspend fun getAllMovies(): List<Movie> = withContext(Dispatchers.IO) {
        var movies: MutableList<Movie> = mutableListOf()

        try {
            val res = olarisClient.get().query(AllMoviesQuery()).await()

            if (res.data != null && res.data?.movies != null) {
                for (movie in res.data!!.movies) {
                    val m = movie!!
                    movies.add(Movie.createFromGraphQLMovieBase(m.fragments.movieBase, server.id))
                }
                return@withContext movies.toList()
            }
        } catch (e: ApolloException) {
            logException("getAllMovies", e)
        }

        return@withContext movies
    }

    suspend fun findSeasonByUUID(uuid: String): Season? {
        try {
            val res = olarisClient.get().query(FindSeasonQuery(uuid)).await()
            if (res.data != null) {
                return Show.buildSeason(res.data!!.season.fragments.seasonBase, server.id)
            }
        } catch (e: ApolloException) {
            logException("findSeasonByUUID", e)
        }
        return null
    }

    suspend fun getAllShows(): List<Show> {
        val shows: MutableList<Show> = mutableListOf()
        try {
            val res = olarisClient.get().query(AllSeriesQuery()).await()
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

    suspend fun findShowByUUID(uuid: String): Show? {
        try {
            val res = olarisClient.get().query(FindSeriesQuery(uuid)).await()
            if (res.data != null && res.data!!.series.isNotEmpty()) {
                return Show.createFromGraphQLSeriesBase(res.data!!.series.first()!!.fragments.seriesBase, server.id)
            }
        } catch (e: ApolloException) {
            logException("findShowByUUID", e)
        }
        return null
    }

    private fun logException(from: String, e: ApolloException) {
        Log.e("apollo", "Helpie from $from! Error getting data: ${e.localizedMessage}")
        Log.e("apollo", "Cause: ${e.cause}")
        Log.e("apollo", e.toString())
    }

}