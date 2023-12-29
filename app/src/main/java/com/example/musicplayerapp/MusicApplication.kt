package com.example.musicplayerapp

import android.app.Application
import com.example.musicplayerapp.data.database.PlaylistDatabase
import com.example.musicplayerapp.data.database.PlaylistRepository

class MusicApplication : Application() {

    lateinit var database: PlaylistDatabase
        private set

    lateinit var repository: PlaylistRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Obtain an instance of the database using your custom getDatabase function
        database = PlaylistDatabase.getDatabase(this)

        // Create a repository using the database instance
        repository = PlaylistRepository(database.playlistDao())

        // Other initialization code...
    }
}

