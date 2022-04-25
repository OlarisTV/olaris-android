package tv.olaris.android.ui.showLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.olaris.android.models.Show
import tv.olaris.android.repositories.OlarisGraphQLRepository
import tv.olaris.android.repositories.ServersRepository

class ShowLibraryViewModel(
    private val graphQLRepository: OlarisGraphQLRepository,
    private val serversRepository: ServersRepository,
) : ViewModel() {

    private val _shows = MutableLiveData<List<Show>>()
    val shows: LiveData<List<Show>> = _shows

    private val _dataLoaded = MutableLiveData<Boolean>()
    val dataLoaded: LiveData<Boolean> = _dataLoaded

    private fun loadData() = viewModelScope.launch {
        _shows.value = graphQLRepository.getAllShows(serversRepository.getCurrentServer())
        _dataLoaded.value = true
    }

    init {
        loadData()
    }
}
