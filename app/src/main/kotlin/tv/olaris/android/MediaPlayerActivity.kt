package tv.olaris.android

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.navArgs
import tv.olaris.android.ui.mediaPlayer.MediaPlayerFragment

class MediaPlayerActivity : FragmentActivity() {
    private lateinit var fullscreenContent: FragmentContainerView
    private val args: MediaPlayerActivityArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //enableFullscreen()

        setContentView(R.layout.activity_media_player)
        fullscreenContent = findViewById(R.id.fcv_fullscreen)

        val tx = supportFragmentManager.beginTransaction()
        val fragment = MediaPlayerFragment()

        fragment.arguments = args.toBundle()
        tx.replace(R.id.fcv_fullscreen, fragment)
        tx.commit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }
}