package tv.olaris.android.ui.showLibrary

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tv.olaris.android.R
import tv.olaris.android.models.Show

class ShowLibraryAdapter(context: Context, private val serverId: Int) :
    ListAdapter<Show, ShowLibraryAdapter.ShowItemHolder>(DiffCallback()) {

    class ShowItemHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val movieCoverArt: ImageView = view.findViewById<ImageView>(R.id.movieCoverArtImage)
        val episodeCount: TextView = view.findViewById<TextView>(R.id.text_episode_count)
        val progressBar: ProgressBar = view.findViewById<ProgressBar>(R.id.progress_bar_cover_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowItemHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.media_list_item_cover_only, parent, false)
        return ShowItemHolder(layout)
    }

    override fun onBindViewHolder(holder: ShowItemHolder, position: Int) {
        val s = getItem(position)

        holder.progressBar.visibility = View.INVISIBLE
        holder.episodeCount.text = s.unwatchedEpisodeCount.toString()

        Glide.with(holder.itemView.context).load(s.fullPosterUrl())
            .placeholder(R.drawable.placeholder_coverart).error(ColorDrawable(Color.RED))
            .into(holder.movieCoverArt);

        holder.movieCoverArt.setOnClickListener {
            val uuid = s.uuid
            val action = ShowLibraryDirections.actionFragmentShowLibraryToFragmentShowDetails(
                uuid = uuid,
                serverId = serverId
            )

            holder.view.findNavController().navigate(action)
        }
    }

}

class DiffCallback : DiffUtil.ItemCallback<Show>() {
    override fun areItemsTheSame(oldItem: Show, newItem: Show): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: Show, newItem: Show): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}