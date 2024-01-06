package com.example.musicplayerapp.data.database

import androidx.annotation.WorkerThread
import com.example.musicplayerapp.data.database.entities.Song
import kotlinx.coroutines.flow.Flow

class SongRepository(private val songDao: SongDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allSongs: Flow<List<Song>> = songDao.getAllSongs()

    @WorkerThread
    suspend fun insertAll(songs: List<Song>) {
        songDao.insertAll(songs)
    }

    @WorkerThread
    suspend fun update(song: Song) {
        songDao.updateSong(song)
    }

    @WorkerThread
    suspend fun delete(song: Song) {
        songDao.deleteSong(song)
    }

    suspend fun getSongId(title: String, artist: String): Long? {
        return songDao.getSongIdByTitleAndArtist(title, artist)
    }
}