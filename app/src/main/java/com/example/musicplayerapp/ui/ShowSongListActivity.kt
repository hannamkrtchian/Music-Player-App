package com.example.musicplayerapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R

class ShowSongListActivity : AppCompatActivity() {
    var currentIndex: Int = -1

    private lateinit var back: ImageView
    private lateinit var titleTv: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewNoSongs: TextView
    private lateinit var edit: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_song_list)

        back= findViewById(R.id.back_button)
        titleTv= findViewById(R.id.playlist_name)
        recyclerView= findViewById(R.id.recycler_view_songs_playlist)
        textViewNoSongs = findViewById(R.id.no_songs)
        edit = findViewById(R.id.edit_button)

        // Back button logic
        back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}