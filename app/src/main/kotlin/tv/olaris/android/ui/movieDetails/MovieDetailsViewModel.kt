package tv.olaris.android.ui.movieDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tv.olaris.android.models.Movie
import tv.olaris.android.repositories.OlarisGraphQLRepository
import tv.olaris.android.repositories.ServersRepository

class MovieDetailsViewModel(
    private val graphQLRepository: OlarisGraphQLRepository,
    private val serversRepository: ServersRepository,
) : ViewModel() {

    private var _movie = MutableLiveData<Movie?>()
    var movie: MutableLiveData<Movie?> = _movie

    private val _posterUrl = MutableLiveData<String>()
    val posterUrl: LiveData<String> = _posterUrl

    private val _coverArtUrl = MutableLiveData<String>()
    val coverArtUrl: LiveData<String> = _coverArtUrl

    fun getMovie(uuid: String) {
        if (_movie.value == null) {
            viewModelScope.launch {
                _movie.value =
                    graphQLRepository.findMovieByUUID(serversRepository.getCurrentServer(), uuid)
                _posterUrl.value = _movie.value?.fullPosterUrl()
                _coverArtUrl.value = _movie.value?.fullCoverArtUrl()
            }
        }
    }
}
