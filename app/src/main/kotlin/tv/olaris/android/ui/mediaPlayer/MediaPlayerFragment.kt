package tv.olaris.android.ui.mediaPlayer

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import tv.olaris.android.databinding.FragmentFullScreenMediaPlayerBinding
import tv.olaris.android.ui.base.BaseFragment
import tv.olaris.android.util.disableFullscreen
import tv.olaris.android.util.enableFullscreen

private const val ARG_UUID = "uuid"
private const val ARG_SERVERID = "serverId"
private const val ARG_PLAYTIME = "playtime"
private const val ARG_MEDIA_UUID = "mediaUuid"
private const val TAG = "mediaplayer"

class MediaPlayerFragment : BaseFragment<FragmentFullScreenMediaPlayerBinding>(FragmentFullScreenMediaPlayerBinding::inflate) {
    private var currentWindow = 0
    private var serverId: Int = 0
    private var uuid: String = ""
    private var mediaUuid: String = ""
    private var playbackPosition: Int = 0
    private val viewModel: MediaPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serverId = it.getInt(ARG_SERVERID)
            uuid = it.getString(ARG_UUID).toString()
            mediaUuid = it.getString(ARG_MEDIA_UUID).toString()
            playbackPosition = it.getInt(ARG_PLAYTIME)
        }

        Log.d(TAG, "Creating view, arguments Server: $serverId")

        //This can force landscape
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        viewModel.player.observe(this) { player ->
            Log.d(TAG, "Got a player!")
            if (player == null) parentFragmentManager.popBackStack()

            binding.exoPlayerFullScreen.player = player
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.player.observe(viewLifecycleOwner) {
            Log.d(TAG, "Got a player part two")
            binding.exoPlayerFullScreen.player = it
            binding.exoPlayerFullScreen.player!!.addListener(PlayerListener(this.requireActivity().window))
        }

        viewModel.getStreamingUrl(serverId, uuid).observe(viewLifecycleOwner) { streamingUrl ->
            Log.d(TAG, streamingUrl)
            viewModel.play(streamingUrl, mediaUuid, playbackPosition.toLong())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.exoPlayerFullScreen.player = null
    }

    override fun onDestroy() {
        super.onDestroy()
        with(requireActivity()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            disableFullscreen(true)
        }
    }

    override fun onPause() {
        viewModel.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().enableFullscreen()
    }

}