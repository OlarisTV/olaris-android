package tv.olaris.android.ui.showLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.olaris.android.OlarisApplication
import tv.olaris.android.models.Show

class ShowLibraryViewModel: ViewModel() {

    private var _shows  = MutableLiveData<List<Show>>()
    val shows : LiveData<List<Show>> = _shows

    private var _dataLoaded = MutableLiveData<Boolean>()
    val dataLoaded: LiveData<Boolean> = _dataLoaded

    fun loadData(serverId: Int) = viewModelScope.launch {
        _shows.value = OlarisApplication.applicationContext().getOrInitRepo(serverId).getAllShows()
        _dataLoaded.value = true
    }
}