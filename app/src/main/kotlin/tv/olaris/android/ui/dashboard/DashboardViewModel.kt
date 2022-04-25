package tv.olaris.android.ui.dashboard

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.olaris.android.models.MediaItem
import tv.olaris.android.repositories.OlarisGraphQLRepository
import tv.olaris.android.repositories.ServersRepository

class DashboardViewModel(
    private val graphQLRepository: OlarisGraphQLRepository,
    private val serversRepository: ServersRepository,
) : ViewModel() {
    private var _continueWatchingItems = MutableLiveData<List<MediaItem>?>()
    val continueWatchingItems: MutableLiveData<List<MediaItem>?> =
        _continueWatchingItems

    private var _recentlyAddedItems = MutableLiveData<List<MediaItem>?>()
    val recentlyAddedItems: MutableLiveData<List<MediaItem>?> =
        _recentlyAddedItems


    fun loadData(serverId: Int) = viewModelScope.launch {
        Log.d("dashboard", "Server id $serverId")
        // I'm not 100% sure we need this if null check in a viewmodel as it is suppose to live through device reorientations and such
        if (_recentlyAddedItems.value.isNullOrEmpty() || _continueWatchingItems.value.isNullOrEmpty()) {

            with(graphQLRepository) {
                _continueWatchingItems.value =
                    findContinueWatchingItems(serversRepository.getCurrentServer())
                Log.d("dashboard", _continueWatchingItems.value?.size.toString())

                _recentlyAddedItems.value =
                    findRecentlyAddedItems(serversRepository.getCurrentServer())
                Log.d("dashboard", _recentlyAddedItems.value?.size.toString())
            }
        }
    }

    fun setCurrentServer(serverId: Int) = viewModelScope.launch {
        serversRepository.setCurrentServer(serversRepository.getServerById(serverId))
    }
}
