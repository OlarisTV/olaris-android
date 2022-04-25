package tv.olaris.android.ui.movieLibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import tv.olaris.android.repositories.OlarisGraphQLRepository
import tv.olaris.android.repositories.ServersRepository

class MovieLibraryViewModel(
    private val serversRepository: ServersRepository,
    private val graphQLRepository: OlarisGraphQLRepository,
) : ViewModel() {

    val flow = Pager(
        PagingConfig(
            pageSize = MOVIES_PER_QUERY,
            enablePlaceholders = false,
            prefetchDistance = MOVIES_PER_QUERY * 3
        )
    ) {
        return@Pager MoviesPagingSource { limit, offset ->
            graphQLRepository.getMovies(serversRepository.getCurrentServer(), limit, offset)
        }
    }.flow.cachedIn(viewModelScope)
}
