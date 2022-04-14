package tv.olaris.android.ui.movieLibrary

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tv.olaris.android.R
import tv.olaris.android.databinding.FragmentMovieLibraryBinding
import tv.olaris.android.models.Movie
import tv.olaris.android.ui.base.BaseFragment

private const val ARG_SERVER_ID = "serverId"
const val movieGridSize = 3

class MovieLibrary : BaseFragment<FragmentMovieLibraryBinding>(FragmentMovieLibraryBinding::inflate) {
    private var serverId: Int = 0
    private lateinit var viewModel: MovieLibraryViewModel

    companion object {
        fun newInstance() = MovieLibrary()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serverId = it.getInt(ARG_SERVER_ID)
            Log.d("server_id", serverId.toString())
        }
        viewModel =  MovieLibraryViewModel(serverId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = MovieItemAdapter(requireContext(), serverId)

        binding.movieRecycleview.adapter = adapter.withLoadStateHeaderAndFooter(header = LoadStateAdapter(adapter::retry), footer = LoadStateAdapter(adapter::retry))

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.movieRecycleview.layoutManager =
                GridLayoutManager(
                    context,
                    resources.getInteger(R.integer.landscape_library_column_count)
                )
        } else {
            binding.movieRecycleview.layoutManager =
                GridLayoutManager(context, resources.getInteger(R.integer.library_column_count))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("fragment", "onActivityCreated: launch")


            viewModel.flow.collectLatest { pagingData: PagingData<Movie> ->
                adapter.submitData(pagingData)

            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("fragment", "onActivityCreated: launc2h")

            adapter.loadStateFlow.collect {
                // to determine if we are done with the loading state,
                // you should have already  shown your loading view elsewhere when the entering your fragment
                if (it.prepend is LoadState.NotLoading && it.prepend.endOfPaginationReached) {
                    binding.progressBarMovieLibrary.visibility = View.GONE
                }
            }
        }
    }

}