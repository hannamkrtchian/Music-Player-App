package com.example.musicplayerapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import okhttp3.*
import com.bumptech.glide.Glide
import com.example.musicplayerapp.AudioModel
import com.example.musicplayerapp.MyMediaPlayer
import com.example.musicplayerapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private lateinit var getLyrics: Button

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
        getLyrics= findViewById(R.id.get_lyrics)

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

                if (currentTimeTv.text == totalTimeTv.text) {
                    playNextSong()
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

        getLyrics.setOnClickListener {
            fetchLyrics()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setResourcesWithMusic() {
        currentSong = songsList?.get(MyMediaPlayer.currentIndex)!!

        // Title
        if (currentSong.artist.contains("unknown")) {
            titleTv.text = currentSong.title
        } else {
            titleTv.text = currentSong.title + " - " + currentSong.artist
        }

        totalTimeTv.text = convertToMMSS(currentSong.duration)

        // album art
        val albumArtUri = Uri.parse(currentSong.albumArtUri)
        if (!isDestroyed && !isFinishing) {
            Glide.with(this)
                .load(albumArtUri)
                .placeholder(R.drawable.baseline_music_note_24) // Placeholder image
                .error(R.drawable.baseline_music_note_24) // Error image if loading fails
                .into(cover)
        }

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

    private fun showLyricsDialog(lyrics: String) {
        val dialogFragment = LyricsDialogFragment()
        val bundle = Bundle().apply {
            putString("lyrics", lyrics)
        }
        dialogFragment.arguments = bundle

        dialogFragment.show(supportFragmentManager, "LyricsDialogFragment")
    }

    private fun fetchLyrics() {
        // Call the suspend function within a coroutine scope
        lifecycleScope.launch(Dispatchers.Main) {
            val lyrics = fetchLyricsFromInternet(currentSong.title, currentSong.artist)
            // Show the dialog with the lyrics
            showLyricsDialog(lyrics)
        }
    }

    private suspend fun fetchLyricsFromInternet(title: String, artist: String): String {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()

            val url = "https://api.lyrics.ovh/v1/$artist/$title"

            val request = Request.Builder()
                .url(url)
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                responseBody ?: ""
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }
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