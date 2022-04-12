package tv.olaris.android.ui.movieLibrary

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

// Adapter that displays a loading spinner when
// state is LoadState.Loading, and an error message and retry
// button when state is LoadState.Error.
class LoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ) = LoadStateViewHolder(parent, retry)

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        loadState: LoadState
    ) = holder.bind(loadState)
}
