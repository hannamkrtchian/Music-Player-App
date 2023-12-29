package com.example.musicplayerapp.data.database

import androidx.annotation.WorkerThread
import com.example.musicplayerapp.data.database.entities.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val playlistDao: PlaylistDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allPlaylists: Flow<List<Playlist>> = playlistDao.getAllPlaylists()

    @WorkerThread
    suspend fun insert(playlist: Playlist) {
        playlistDao.insertPlaylist(playlist)
    }

    @WorkerThread
    suspend fun update(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist)
    }

    @WorkerThread
    suspend fun delete(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist)
    }
}