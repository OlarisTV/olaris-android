package tv.olaris.android.ui.showLibrary

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import tv.olaris.android.R
import tv.olaris.android.databinding.FragmentShowLibraryBinding

private const val ARG_SERVER_ID = "serverId"


class ShowLibrary : Fragment() {
    private var _binding: FragmentShowLibraryBinding? = null
    private val binding get() = _binding!!
    private var serverId: Int = 0
    private val viewModel: ShowLibraryViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serverId = it.getInt(ARG_SERVER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentShowLibraryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ShowLibraryAdapter(requireContext(), serverId)

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

        viewModel.loadData(serverId)
    }
}