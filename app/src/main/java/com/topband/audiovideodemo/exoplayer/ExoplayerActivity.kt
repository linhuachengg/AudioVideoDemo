package com.topband.audiovideodemo.exoplayer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_exoplayer.*
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource.HttpDataSourceException
import com.topband.audiovideodemo.R


class ExoplayerActivity : AppCompatActivity() {
    private lateinit var  player : SimpleExoPlayer
    private  var uri = Uri.parse("http://video.topband-cloud.com/ce665250c7d741bb9651cb6c3b4629ce/e6749ce61de54989b9aa923c8b8e9c66-eb7d285de0108ca8b459ae377806d76b-sd.mp4")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exoplayer)
         createPlayer()
    }

     private fun createPlayer(){
         player = SimpleExoPlayer.Builder(this).build()
         video_view.player = player
         var dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "AudioVideoDemo"))
         var videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
         player.prepare(videoSource)
         player.playWhenReady = true
         player.repeatMode = Player.REPEAT_MODE_ONE
         player.addListener(object:Player.EventListener{
             override fun onPlayerError(error: ExoPlaybackException) {
                 L("error")
                 if (error.type === ExoPlaybackException.TYPE_SOURCE) {
                     val cause = error.sourceException
                     if (cause is HttpDataSourceException) {
                         // An HTTP error occurred.
                         // This is the request for which the error occurred.
                         val requestDataSpec = cause.dataSpec
                         // It's possible to find out more about the error both by casting and by
                         // querying the cause.
                         if (cause is HttpDataSource.InvalidResponseCodeException) {
                             // Cast to InvalidResponseCodeException and retrieve the response code,
                             // message and headers.
                         } else {
                             // Try calling httpError.getCause() to retrieve the underlying cause,
                             // although note that it may be null.
                         }
                     }
                 }
             }

             override fun onIsPlayingChanged(isPlaying: Boolean) {
               L("ispalying:$isPlaying")
             }

             override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                 when(playbackState){
                     Player.STATE_BUFFERING -> {
                         L("state:$playbackState-->buffer")
                     }
                     Player.STATE_READY -> {
                         L("state:$playbackState-->READY")
                     }
                     Player.STATE_ENDED -> {
                         L("state:$playbackState-->ENDED")
                     }
                     Player.STATE_IDLE -> {
                         L("state:$playbackState-->IDLE")
                     }
                 }
             }

             override fun onSeekProcessed() {
                 L("onSeekProcessed")
             }
         })
     }

    private fun release(){
        player.release()
    }

    private fun L(string: String){
        Log.d("lhc->",string)
    }

}