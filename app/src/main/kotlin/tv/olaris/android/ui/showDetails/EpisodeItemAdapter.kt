package tv.olaris.android.ui.showDetails

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tv.olaris.android.R
import tv.olaris.android.models.Season

class EpisodeItemAdapter(context: Context, val seasonBase: Season, val serverId: Int) :
    RecyclerView.Adapter<EpisodeItemAdapter.EpisodeItemHolder>() {

    class EpisodeItemHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val episodeTitle: TextView = view.findViewById(R.id.text_episode_title)
        val episodeDescription: TextView = view.findViewById(R.id.text_episode_description)
        val episodeDetails: TextView = view.findViewById(R.id.text_episode_number)
        val episodeStillImage: ImageView = view.findViewById(R.id.image_view_episode_item_cover)
        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar_episode_item)
        val playButton: Button = view.findViewById(R.id.btn_play_episode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeItemHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.season_list_item, parent, false)
        return EpisodeItemHolder(layout)
    }


    override fun getItemCount(): Int {
        return seasonBase.episodes.size
    }

    override fun onBindViewHolder(holder: EpisodeItemHolder, position: Int) {
        val episode = seasonBase.episodes[position]

        holder.episodeTitle.text = episode.name
        holder.episodeDescription.text = episode.overview
        holder.progressBar.progress = episode.playProgress().toInt()
        holder.episodeDetails.text =
            "Episode ${episode.episodeNumber.toString()} - ${episode.airDate}"

        Glide.with(holder.itemView.context).load(episode.stillPathUrl())
            .placeholder(R.drawable.placeholder_coverart).error(ColorDrawable(Color.RED))
            .into(holder.episodeStillImage);

        holder.playButton.setOnClickListener {
            val nav = holder.view.findNavController()

            val action =
                ShowDetailsFragmentDirections.actionFragmentShowDetailsToFragmentFullScreenMediaPlayer(
                    uuid = episode.files.first().uuid,
                    serverId = serverId,
                    episode.playtime.toInt(),
                    episode.uuid
                )
            nav.navigate(action)
        }
    }
}