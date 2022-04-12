package tv.olaris.android.ui.movieLibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class MovieLibraryViewModel : ViewModel() {
    // TODO: Can hardcoded 1 as initial value break this? Can we somehow make this a union of null and 1 and only act on an int?
    private val queryFlow = MutableStateFlow(value = 1)

    val flow = queryFlow.flatMapLatest {  serverId ->
        //TODO: This probably can't be cached now as we keep making a new pager, we need some better way of doing this
        Pager(PagingConfig(pageSize = MOVIES_PER_QUERY)) {
            MoviesPagingSource(serverId = serverId)
        }.flow.cachedIn(viewModelScope)
    }

    fun loadData(serverId: Int) = viewModelScope.launch {
        queryFlow.emit(serverId)
    }
}