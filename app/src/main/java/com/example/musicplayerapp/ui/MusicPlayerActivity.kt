package com.example.musicplayerapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.example.musicplayerapp.AudioModel
import com.example.musicplayerapp.R

class MusicPlayerActivity : AppCompatActivity() {

    // all components in activity
    val titleTv: TextView = findViewById(R.id.song_title)
    var currentTimeTv: TextView = findViewById(R.id.current_time)
    val totalTimeTv: TextView = findViewById(R.id.total_time)
    val seekBar: SeekBar = findViewById(R.id.seek_bar)
    val pausePlay: ImageView = findViewById(R.id.pause_play)
    val nextBtn: ImageView = findViewById(R.id.next)
    val previousBtn: ImageView = findViewById(R.id.previous)
    val cover: ImageView = findViewById(R.id.cover_song)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)
    }
}