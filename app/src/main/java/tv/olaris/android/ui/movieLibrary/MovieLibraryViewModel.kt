package tv.olaris.android.ui.movieLibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class MovieLibraryViewModel(private val serverId: Int) : ViewModel() {
    val flow = Pager(PagingConfig(pageSize = MOVIES_PER_QUERY)) {
            MoviesPagingSource(serverId = serverId)
        }.flow.cachedIn(viewModelScope)
}
class MovieLibraryViewModelFactory(private val serverId: Int): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MovieLibraryViewModel(serverId) as T
}
