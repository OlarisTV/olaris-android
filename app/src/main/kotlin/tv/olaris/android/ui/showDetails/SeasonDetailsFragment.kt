package tv.olaris.android.ui.showDetails

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import tv.olaris.android.OlarisApplication
import tv.olaris.android.R
import tv.olaris.android.databinding.FragmentSeasonDetailsBinding
import tv.olaris.android.ui.base.BaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_SERVER_ID = "serverId"
private const val ARG_SEASON = "seasonUUID"


class SeasonDetailsFragment() : BaseFragment<FragmentSeasonDetailsBinding>(FragmentSeasonDetailsBinding::inflate) {
    var seasonUUID : String = ""
    var serverId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serverId = it.getInt(ARG_SERVER_ID)
            seasonUUID = it.getString(ARG_SEASON).toString()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val context = this.requireContext()

        lifecycleScope.launch {
            val season =  OlarisApplication.applicationContext().getOrInitRepo(serverId).findSeasonByUUID(seasonUUID)
            if(season != null) {
                binding.recyclerEpisodeList.adapter =
                    EpisodeItemAdapter(context, season, serverId)
                if(resources.getBoolean(R.bool.is_grid)){
                    binding.recyclerEpisodeList.layoutManager = GridLayoutManager(context, resources.getInteger(R.integer.grid_column_count))
                }else{
                    binding.recyclerEpisodeList.layoutManager = LinearLayoutManager(context)
                }

            }
        }
    }
}