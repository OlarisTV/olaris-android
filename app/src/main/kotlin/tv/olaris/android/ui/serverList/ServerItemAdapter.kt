package tv.olaris.android.ui.serverList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tv.olaris.android.R
import tv.olaris.android.databases.Server

class ServerItemAdapter(
    private val itemClickListener: (server: Server) -> Unit,
) : ListAdapter<Server, ServerItemAdapter.ServerItemHolder>(DiffCallback()) {

    class ServerItemHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val serverUrl: TextView = view.findViewById<TextView>(R.id.text_server_label)
        val deleteServerIcon: ImageView = view.findViewById<ImageView>(R.id.icon_delete_server)
        var serverVersion: TextView = view.findViewById<TextView>(R.id.text_server_version)
        val progressStatusOffline: ProgressBar =
            view.findViewById(R.id.progress_server_status_offline)
        val progressStatusOnline: ProgressBar =
            view.findViewById(R.id.progress_server_status_online)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerItemHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.server_card, parent, false)
        return ServerItemHolder(layout)
    }

    override fun onBindViewHolder(holder: ServerItemHolder, position: Int) {
        val s = getItem(position)

        holder.serverUrl.text = s.name
        holder.serverVersion.text = s.version

        if (s.isOnline) {
            holder.progressStatusOffline.visibility = View.INVISIBLE
            holder.progressStatusOnline.visibility = View.VISIBLE
        } else {
            holder.progressStatusOffline.visibility = View.VISIBLE
            holder.progressStatusOnline.visibility = View.INVISIBLE
            holder.serverVersion.text = holder.view.context.getString(R.string.server_offline)
        }

        holder.deleteServerIcon.setOnClickListener {

            MaterialAlertDialogBuilder(holder.view.context)
                .setTitle(holder.view.context.getString(R.string.remove_server_title))
                .setMessage(
                    holder.view.context.getString(
                        R.string.remove_server_confirmation,
                        s.name
                    )
                )
                .setNeutralButton(holder.view.context.getString(R.string.cancel_button)) { _, _ ->
                }

                .setPositiveButton(holder.view.context.getString(R.string.confirm_button)) { _, _ ->
                    itemClickListener(getItem(position))
                }
                .show()
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Server>() {
    override fun areItemsTheSame(oldItem: Server, newItem: Server): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Server, newItem: Server): Boolean {
        return oldItem == newItem
    }
}
