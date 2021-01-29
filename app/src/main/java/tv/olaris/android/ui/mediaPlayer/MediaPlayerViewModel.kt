package tv.olaris.android.ui.mediaPlayer

import androidx.lifecycle.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.MimeTypes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tv.olaris.android.OlarisApplication

class MediaPlayerLifecycleObserver(private val viewModel: MediaPlayerViewModel) :
    LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.d("mediaplayer", "INIT PLAYER")
        viewModel.initPlayer()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        viewModel.pause()
    }
}

class MediaPlayerViewModel() : ViewModel(), Player.EventListener {
    var serverId: Int = 0
    var uuid: String = ""
    var mediaUUID = ""
    var currentPos: Long = 0
    val playerOrNull: ExoPlayer? get() = _player.value

    val player: LiveData<ExoPlayer?> get() = _player
    private val _player = MutableLiveData<ExoPlayer?>()


    private val lifecycleObserver = MediaPlayerLifecycleObserver(this)

    private val streamingUrl: LiveData<String> = liveData {
        var streamingURL = OlarisApplication.applicationContext().getOrInitRepo(serverId).getStreamingUrl(uuid)
        if (streamingURL != null) {
            //TODO: Fix this hardcoded codec crap.
            streamingURL += "?playableCodecs=avc1.640029&playableCodecs=avc1.64001f&playableCodecs=avc1.64001e&playableCodecs=avc1.640020&playableCodecs=avc1.640028&playableCodecs=avc1.640029&playableCodecs=avc1.64001f&playableCodecs=mp4a.40.2"
            emit(streamingURL)
        }
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }

    // TODO: Fix these arguments here.
    fun getStreamingUrl(s: Int, u: String): LiveData<String> {
        serverId = s
        uuid = u

        return streamingUrl
    }

    private suspend fun reportPlaybackState() {
        val player = playerOrNull ?: return
        currentPos = player.currentPosition

        OlarisApplication.applicationContext().getOrInitRepo(serverId).updatePlayState(mediaUUID, false, (currentPos / 1000).toDouble())

        Log.d("mediaplayer", currentPos.toString())
    }

    private fun setupTimeUpdates() {
        viewModelScope.launch {
            while (true) {
                reportPlaybackState()
                delay(1000)
            }
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        val player = playerOrNull ?: return
        when (playbackState) {
            Player.STATE_READY -> {
                setupTimeUpdates()
            }
        }
    }

    fun play(mediaUrl: String, uuid: String, startPos: Long = 0) {
        mediaUUID = uuid
        currentPos = startPos * 1000
        val player = playerOrNull ?: return
        val mi = MediaItem.Builder()
            .setUri(mediaUrl)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()
        player.setMediaItem(mi)
        player.setSeekParameters(SeekParameters.NEXT_SYNC)
        player.prepare()
        if (startPos > 0) player.seekTo(currentPos)
        player.playWhenReady = true
    }

    fun pause() {
        playerOrNull?.playWhenReady = false
    }


    fun initPlayer() {
        _player.value = SimpleExoPlayer.Builder(OlarisApplication.applicationContext())
            .setTrackSelector(DefaultTrackSelector(OlarisApplication.applicationContext())).build()
            .apply {
                videoScalingMode = Renderer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                prepare()
            }.apply {
            addListener(this@MediaPlayerViewModel)
        }
    }

    private fun destroyPlayer() {
        playerOrNull?.release()
        _player.value = null
    }

    override fun onCleared() {
        Log.d("playerViewModel", "onCleared()")
        ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
        destroyPlayer()
    }
}