package tv.olaris.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.olaris.android.databinding.DashboardFragmentBinding
import tv.olaris.android.ui.base.BaseFragment
import tv.olaris.android.ui.dashboard.MediaItemAdapter

class HomeFragment : BaseFragment<DashboardFragmentBinding>(DashboardFragmentBinding::inflate) {
    private val viewModel: HomeViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val continueAdapter = MediaItemAdapter { serverId ->
            viewModel.setCurrentServer(serverId)
        }
        val recentlyAddedAdapter = MediaItemAdapter { serverId ->
            viewModel.setCurrentServer(serverId)
        }

        binding.recyclerRecentlyAdded.adapter = recentlyAddedAdapter
        binding.recyclerRecentlyAdded.layoutManager =
            GridLayoutManager(view.context, 2, GridLayoutManager.HORIZONTAL, false)

        binding.recyclerContinueWatching.adapter = continueAdapter
        binding.recyclerContinueWatching.layoutManager =
            GridLayoutManager(view.context, 2, GridLayoutManager.HORIZONTAL, false)

        viewModel.recentlyAddedItems.observe(viewLifecycleOwner) {
            binding.progressRecentlyAdded.visibility = View.INVISIBLE
            recentlyAddedAdapter.submitList(it)
        }

        viewModel.upNextItems.observe(viewLifecycleOwner) {
            binding.progressBarUpnext.visibility = View.INVISIBLE
            continueAdapter.submitList(it)
        }
    }
}
