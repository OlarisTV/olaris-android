package tv.olaris.android.ui.movieLibrary

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.bumptech.glide.load.HttpException
import org.koin.core.component.KoinComponent
import tv.olaris.android.models.Movie
import java.io.IOException

const val MOVIES_PER_QUERY = 60

class MoviesPagingSource(
    private val getMovies: suspend (limit: Int, offset: Int) -> List<Movie>
) : PagingSource<Int, Movie>(), KoinComponent {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let {
            Log.d("refreshKey", "getRefreshKey: Going back to")
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        var nextPageNumber = params.key ?: 0

        Log.d("PagingSource", "load: key: ${params.key} / nextPageNumber: $nextPageNumber")
        return try {

            val movies = getMovies(MOVIES_PER_QUERY, nextPageNumber * MOVIES_PER_QUERY)

            // TODO: There should be a way to clean this up!
            var nextKey: Int? = null

            if (movies.size >= MOVIES_PER_QUERY) {
                nextPageNumber += 1
                nextKey = nextPageNumber
            }

            LoadResult.Page(data = movies, prevKey = null, nextKey = nextKey)

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: java.net.SocketTimeoutException) {
            Log.d("PagingSource", exception.toString())
            LoadResult.Error(exception)
        } catch (exception: ApolloNetworkException) {
            Log.d("PagingSource", exception.toString())
            LoadResult.Error(exception)
        }
    }
}
