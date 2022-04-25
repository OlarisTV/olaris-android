package tv.olaris.android.ui.movieLibrary

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.olaris.android.R
import tv.olaris.android.databinding.FragmentMovieLibraryBinding
import tv.olaris.android.models.Movie
import tv.olaris.android.ui.base.BaseFragment

private const val ARG_SERVER_ID = "serverId"
const val movieGridSize = 3

class MovieLibrary :
    BaseFragment<FragmentMovieLibraryBinding>(FragmentMovieLibraryBinding::inflate) {
    private val viewModel: MovieLibraryViewModel by viewModel()

    companion object {
        fun newInstance() = MovieLibrary()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MovieItemAdapter()

        binding.movieRecycleview.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(adapter::retry),
            footer = LoadStateAdapter(adapter::retry)
        )

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
