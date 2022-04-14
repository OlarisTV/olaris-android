package tv.olaris.android.ui.movieLibrary

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.bumptech.glide.load.HttpException
import tv.olaris.android.OlarisApplication
import tv.olaris.android.models.Movie
import java.io.IOException

const val MOVIES_PER_QUERY = 15

class MoviesPagingSource(val serverId: Int) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            val a=  anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            Log.d("refreshKey", "getRefreshKey: Going back to $a")
            return a
        }

    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        var nextPageNumber = params.key ?: 0

        Log.d("PagingSource", "load: key: ${params.key} / nextPageNumber: $nextPageNumber")
        return try {
            val movies = OlarisApplication.applicationContext().getOrInitRepo(serverId)
                .getMovies(limit = MOVIES_PER_QUERY, offset = nextPageNumber * MOVIES_PER_QUERY)

            // TODO: There should be a way to clean this up!
            if(movies.size >= MOVIES_PER_QUERY){
                nextPageNumber += 1
                LoadResult.Page(data = movies, prevKey = null, nextKey = nextPageNumber)
            }else {
                LoadResult.Page(data = movies, prevKey = null, nextKey = null)
            }


        } catch(exception: IOException){
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch(exception: java.net.SocketTimeoutException){
            Log.d("PagingSource", exception.toString())
            LoadResult.Error(exception)
        } catch(exception: ApolloNetworkException){
            Log.d("PagingSource", exception.toString())
            LoadResult.Error(exception)
        }
    }
}