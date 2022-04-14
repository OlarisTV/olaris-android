package tv.olaris.android.ui.dashboard

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.olaris.android.OlarisApplication
import tv.olaris.android.models.MediaItem

class DashboardViewModel : ViewModel() {
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
            with(OlarisApplication.applicationContext().getOrInitRepo(serverId)) {
                _continueWatchingItems.value = findContinueWatchingItems()
                Log.d("dashboard", _continueWatchingItems.value?.size.toString())

                _recentlyAddedItems.value = findRecentlyAddedItems()
                Log.d("dashboard", _recentlyAddedItems.value?.size.toString())
            }
        }
    }
}