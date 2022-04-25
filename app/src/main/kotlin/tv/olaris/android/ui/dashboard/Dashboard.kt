package tv.olaris.android.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.olaris.android.databinding.DashboardFragmentBinding
import tv.olaris.android.ui.base.BaseFragment

private const val ARG_SERVER_ID = "serverId"

class Dashboard : BaseFragment<DashboardFragmentBinding>(DashboardFragmentBinding::inflate) {
    private val viewModel: DashboardViewModel by viewModel()
    private var serverId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serverId = it.getInt(ARG_SERVER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val spanCount =
//            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                resources.getInteger(R.integer.landscape_dashboard_column_count)
//            } else {
//                resources.getInteger(R.integer.dashboard_column_count)
//            }

        val continueAdapter = MediaItemAdapter { serverId ->
            viewModel.setCurrentServer(serverId)
        }

        binding.recyclerContinueWatching.adapter = continueAdapter
        binding.recyclerContinueWatching.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)


        val recentlyAddedAdapter = MediaItemAdapter { serverId ->
            viewModel.setCurrentServer(serverId)
        }

        binding.recyclerRecentlyAdded.adapter = recentlyAddedAdapter
        binding.recyclerRecentlyAdded.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)


        viewModel.continueWatchingItems.observe(viewLifecycleOwner) {
            binding.progressBarUpnext.visibility = View.INVISIBLE
            continueAdapter.submitList(it)
        }

        viewModel.recentlyAddedItems.observe(viewLifecycleOwner) {
            binding.progressRecentlyAdded.visibility = View.INVISIBLE
            recentlyAddedAdapter.submitList(it)
        }

        viewModel.loadData(serverId)
    }
}
