package com.example.musicplayerapp

import android.app.Application
import com.example.musicplayerapp.data.database.PlaylistDatabase
import com.example.musicplayerapp.data.database.PlaylistRepository
import com.example.musicplayerapp.data.database.PlaylistSongCrossRefRepository
import com.example.musicplayerapp.data.database.SongRepository

class MusicApplication : Application() {

    lateinit var playlistDatabase: PlaylistDatabase
        private set

    lateinit var playlistRepository: PlaylistRepository
        private set

    lateinit var songRepository: SongRepository
        private set

    lateinit var playlistSongCrossRefRepository: PlaylistSongCrossRefRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Obtain an instance of the database using your custom getDatabase function
        playlistDatabase = PlaylistDatabase.getDatabase(this)

        // Create repositories using the database instance
        playlistRepository = PlaylistRepository(playlistDatabase.playlistDao())
        songRepository = SongRepository(playlistDatabase.songDao())
        playlistSongCrossRefRepository = PlaylistSongCrossRefRepository(playlistDatabase.playlistSongCrossRefDao())

    }
}

