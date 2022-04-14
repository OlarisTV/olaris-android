package tv.olaris.android.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import tv.olaris.android.R
import tv.olaris.android.databinding.DashboardFragmentBinding
import tv.olaris.android.ui.dashboard.MediaItemAdapter

class HomeFragment : Fragment() {
    var _binding : DashboardFragmentBinding? = null
    val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DashboardFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spanCount = if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            resources.getInteger(R.integer.landscape_dashboard_column_count)
        }else{
            resources.getInteger(R.integer.dashboard_column_count)
        }

        val continueAdapter = MediaItemAdapter(requireContext())
        val recentlyAddedAdapter = MediaItemAdapter(requireContext())

        binding.recyclerRecentlyAdded.adapter = recentlyAddedAdapter
        binding.recyclerRecentlyAdded.layoutManager = GridLayoutManager(view.context, spanCount)

        binding.recyclerContinueWatching.adapter = continueAdapter
        binding.recyclerContinueWatching.layoutManager = GridLayoutManager(view.context, spanCount)

        viewModel.recentlyAddedItems.observe(viewLifecycleOwner){
            binding.progressRecentlyAdded.visibility = View.INVISIBLE
            recentlyAddedAdapter.submitList(it)
        }

        viewModel.upNextItems.observe(viewLifecycleOwner){
            binding.progressBarUpnext.visibility = View.INVISIBLE
            continueAdapter.submitList(it)
        }

        viewModel.loadData()
    }
}