package tv.olaris.android.ui.mediaPlayer

import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.google.android.exoplayer2.Player


class PlayerListener(private val window: Window) : Player.Listener {

    override fun onIsPlayingChanged(isPlaying: Boolean) {

        Log.d("player", "Event changed, isPlaying: $isPlaying")
        super.onIsPlayingChanged(isPlaying)

        if (isPlaying) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
