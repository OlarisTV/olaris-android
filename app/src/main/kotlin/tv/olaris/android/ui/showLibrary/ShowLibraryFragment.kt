package tv.olaris.android.ui.showLibrary

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.olaris.android.R
import tv.olaris.android.databinding.FragmentShowLibraryBinding
import tv.olaris.android.ui.base.BaseFragment

class ShowLibrary : BaseFragment<FragmentShowLibraryBinding>(FragmentShowLibraryBinding::inflate) {
    private val viewModel: ShowLibraryViewModel by viewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ShowLibraryAdapter()

        val spanCount = resources.getInteger(R.integer.library_column_count)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            resources.getInteger(R.integer.landscape_library_column_count)
        }

        binding.recyclerShowLibrary.layoutManager = GridLayoutManager(context, spanCount)
        binding.recyclerShowLibrary.adapter = adapter

        viewModel.dataLoaded.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBarShowLibrary.visibility = View.INVISIBLE
            } else {
                binding.progressBarShowLibrary.visibility = View.VISIBLE
            }
        }

        viewModel.shows.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}
