package live.bestream.spiritplayer

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()

        playerView = PlayerView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.BLACK)
            useController = true
            setShowSubtitleButton(true)
            setShowNextButton(false)
            setShowPreviousButton(false)
            controllerShowTimeoutMs = 4000
            requestFocus()
        }
        setContentView(playerView)

        val streamUrl = intent.getStringExtra("STREAM_URL") ?: ""
        if (streamUrl.isNotEmpty()) {
            initializePlayer(streamUrl)
        } else {
            finish()
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer(url: String) {
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(
                buildUponParameters()
                    .setPreferredAudioLanguage("fr")
                    .setPreferredTextLanguage("fr")
            )
        }

        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build().also { exo ->
                playerView.player = exo
                val mediaItem = MediaItem.fromUri(url)
                exo.setMediaItem(mediaItem)
                exo.prepare()
                exo.playWhenReady = true
            }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        playerView.showController()
        return super.onKeyDown(keyCode, event)
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        )
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
