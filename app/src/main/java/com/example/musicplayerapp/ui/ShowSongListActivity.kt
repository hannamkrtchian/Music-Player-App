package com.example.musicplayerapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicplayerapp.R

class ShowSongListActivity : AppCompatActivity() {
    var currentIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_song_list)
    }
}