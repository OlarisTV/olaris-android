package tv.olaris.android.ui.showDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.olaris.android.models.Season
import tv.olaris.android.repositories.OlarisGraphQLRepository
import tv.olaris.android.repositories.ServersRepository

class SeasonDetailsViewModel(
    private val olarisGraphQLRepository: OlarisGraphQLRepository,
    private val serversRepository: ServersRepository,
) : ViewModel() {

    private val _seasonDetails = MutableLiveData<Season>()
    val seasonDetails: LiveData<Season> = _seasonDetails

    fun getSeasonDetails(uuid: String?) = viewModelScope.launch {
        uuid?.let {
            _seasonDetails.value =
                olarisGraphQLRepository.findSeasonByUUID(serversRepository.getCurrentServer(), uuid)
        }
    }
}
