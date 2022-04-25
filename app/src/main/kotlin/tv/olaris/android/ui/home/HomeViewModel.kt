package tv.olaris.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.olaris.android.models.MediaItem
import tv.olaris.android.repositories.OlarisGraphQLRepository
import tv.olaris.android.repositories.ServersRepository

const val TAG = "home"

class HomeViewModel(
    private val serversRepository: ServersRepository,
    private val graphQLRepository: OlarisGraphQLRepository,
) : ViewModel() {
    private val _upNextItems = MutableLiveData<List<MediaItem>>()
    val upNextItems: LiveData<List<MediaItem>> = _upNextItems

    private val _recentlyAdded = MutableLiveData<List<MediaItem>>()
    val recentlyAddedItems: MutableLiveData<List<MediaItem>> = _recentlyAdded

    fun setCurrentServer(serverId: Int) = viewModelScope.launch {
        serversRepository.setCurrentServer(serversRepository.getServerById(serverId))
    }

    private fun loadData() {
        viewModelScope.launch {
            serversRepository.allServers().collect {

                val recentlyAddedList = mutableListOf<MediaItem>()
                val continueList = mutableListOf<MediaItem>()

                for (s in it) {
                    continueList.addAll(graphQLRepository.findContinueWatchingItems(s))
                    recentlyAddedList.addAll(graphQLRepository.findRecentlyAddedItems(s))
                }

                _upNextItems.value = continueList
                _recentlyAdded.value = recentlyAddedList
            }
        }
    }

    init {
        loadData()
    }
}
