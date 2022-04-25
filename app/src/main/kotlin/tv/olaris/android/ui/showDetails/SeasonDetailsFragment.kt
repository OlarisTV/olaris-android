package tv.olaris.android.ui.showDetails

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.ext.android.inject
import tv.olaris.android.R
import tv.olaris.android.databinding.FragmentSeasonDetailsBinding
import tv.olaris.android.ui.base.BaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_SEASON = "seasonUUID"

class SeasonDetailsFragment :
    BaseFragment<FragmentSeasonDetailsBinding>(FragmentSeasonDetailsBinding::inflate) {
    private val seasonDetailsViewModel: SeasonDetailsViewModel by inject()

    var seasonUUID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            seasonUUID = it.getString(ARG_SEASON).toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = this.requireContext()

        seasonDetailsViewModel.seasonDetails.observe(viewLifecycleOwner) { it ->
            it?.let { season ->
                binding.recyclerEpisodeList.adapter =
                    EpisodeItemAdapter(season)
                if (resources.getBoolean(R.bool.is_grid)) {
                    binding.recyclerEpisodeList.layoutManager = GridLayoutManager(
                        context,
                        resources.getInteger(R.integer.grid_column_count)
                    )
                } else {
                    binding.recyclerEpisodeList.layoutManager = LinearLayoutManager(context)
                }
            }
        }
        seasonDetailsViewModel.getSeasonDetails(seasonUUID)
    }
}
