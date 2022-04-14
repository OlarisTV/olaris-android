package tv.olaris.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import tv.olaris.android.OlarisApplication
import tv.olaris.android.models.MediaItem

const val TAG = "home"

class HomeViewModel : ViewModel() {
    private var _upNextItems = MutableLiveData<List<MediaItem>>()
    val upNextItems: LiveData<List<MediaItem>> = _upNextItems

    private var _recentlyAdded = MutableLiveData<List<MediaItem>>()
    val recentlyAddedItems: MutableLiveData<List<MediaItem>> = _recentlyAdded


    fun loadData() {
        viewModelScope.launch {
            OlarisApplication.applicationContext().serversRepository.allServers.collect {

                val recentlyAddedList = mutableListOf<MediaItem>()
                val continueList = mutableListOf<MediaItem>()

                for (s in it) {
                    continueList.addAll(
                        OlarisApplication.applicationContext().getOrInitRepo(s.id)
                            .findContinueWatchingItems()
                    )
                    recentlyAddedList.addAll(
                        OlarisApplication.applicationContext().getOrInitRepo(s.id)
                            .findRecentlyAddedItems()
                    )
                }

                _upNextItems.value = continueList
                _recentlyAdded.value = recentlyAddedList
            }
        }
    }
}