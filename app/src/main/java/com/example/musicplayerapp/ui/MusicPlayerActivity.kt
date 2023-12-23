package com.example.musicplayerapp.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.musicplayerapp.AudioModel
import com.example.musicplayerapp.MyMediaPlayer
import com.example.musicplayerapp.R
import java.io.IOException
import java.io.Serializable
import java.util.concurrent.TimeUnit

class MusicPlayerActivity : AppCompatActivity() {

    // components in activity
    private lateinit var titleTv: TextView
    private lateinit var currentTimeTv: TextView
    private lateinit var totalTimeTv: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var pausePlay: ImageView
    private lateinit var nextBtn: ImageView
    private lateinit var previousBtn: ImageView
    private lateinit var cover: ImageView
    private lateinit var back: ImageView

    private lateinit var currentSong: AudioModel

    private var songsList: ArrayList<AudioModel>? = null
    private var mediaPlayer = MyMediaPlayer.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        // assign components in activity
        titleTv= findViewById(R.id.song_info)
        currentTimeTv= findViewById(R.id.current_time)
        totalTimeTv= findViewById(R.id.total_time)
        seekBar= findViewById(R.id.seek_bar)
        pausePlay= findViewById(R.id.pause_play)
        nextBtn= findViewById(R.id.next)
        previousBtn= findViewById(R.id.previous)
        cover= findViewById(R.id.cover_song)
        back= findViewById(R.id.back_button)

        // Back button logic
        back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        titleTv.isSelected = true

        songsList= intent.getSerializable("LIST")

        setResourcesWithMusic()

        val handler = Handler(Looper.getMainLooper())

        // Update seek bar & change play/pause button
        val updateSeekBarRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    seekBar.progress = mediaPlayer.currentPosition
                    currentTimeTv.text = convertToMMSS(mediaPlayer.currentPosition.toString())
                    pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24)
                } else {
                    pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24)
                }
                handler.postDelayed(this, 100)
            }
        }

        // Start updating the seek bar
        handler.postDelayed(updateSeekBarRunnable, 100)

        // Change in progress of the seek bar
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        }

    private fun setResourcesWithMusic() {
        currentSong = songsList?.get(MyMediaPlayer.currentIndex)!!
        titleTv.text = currentSong.title + " - " + currentSong.artist
        totalTimeTv.text = convertToMMSS(currentSong.duration)

        // click listeners
        pausePlay.setOnClickListener { pausePlay() }
        nextBtn.setOnClickListener { playNextSong() }
        previousBtn.setOnClickListener { playPreviousSong() }

        playMusic()

    }

    private fun playMusic() {
        mediaPlayer.reset()
        try {
            mediaPlayer.setDataSource(currentSong.path)
            mediaPlayer.prepare()
            mediaPlayer.start()

            // seekbar logic
            seekBar.progress = 0
            seekBar.max = mediaPlayer.duration
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun playNextSong() {
        // check if it's the last song (start first song again)
        if(MyMediaPlayer.currentIndex == (songsList?.size?.minus(1))) {
            MyMediaPlayer.currentIndex = 0
            mediaPlayer.reset()
            setResourcesWithMusic()
        } else {
            // go to next song
            MyMediaPlayer.currentIndex += 1
            mediaPlayer.reset()
            setResourcesWithMusic()
        }
    }

    private fun playPreviousSong() {
        // check if it's the first song (go to last song)
        if(MyMediaPlayer.currentIndex == 0) {
            MyMediaPlayer.currentIndex = songsList?.size?.takeIf { it > 0 }?.minus(1) ?: -1
            mediaPlayer.reset()
            setResourcesWithMusic()
        } else {
            // go to previous song
            MyMediaPlayer.currentIndex -= 1
            mediaPlayer.reset()
            setResourcesWithMusic()
        }
    }

    private fun pausePlay() {
        if(mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }

    // static function to convert duration to string
    companion object {
        fun convertToMMSS(duration: String): String {
            val millis = duration.toLongOrNull() ?: 0L
            return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis), // minutes
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60) // seconds
        }
    }
}

// check api to use the right getSerializable function
@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Intent.getSerializable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        this.getSerializableExtra(key, T::class.java)
    else
        this.getSerializableExtra(key) as? T
}