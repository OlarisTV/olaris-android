package tv.olaris.android.ui.showDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.olaris.android.models.Show
import tv.olaris.android.repositories.OlarisGraphQLRepository
import tv.olaris.android.repositories.ServersRepository

class ShowDetailsViewModel(
    private val olarisGraphQLRepository: OlarisGraphQLRepository,
    private val serversRepository: ServersRepository,
) : ViewModel() {

    private val _showDetails = MutableLiveData<Show>()
    val showDetails: LiveData<Show> = _showDetails

    fun getShowDetails(uuid: String?) = viewModelScope.launch {
        uuid?.let {
            _showDetails.value =
                olarisGraphQLRepository.findShowByUUID(serversRepository.getCurrentServer(), uuid)
        }
    }
}
