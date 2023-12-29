package com.example.musicplayerapp.data.database

import com.example.musicplayerapp.data.database.entities.PlaylistSongCrossRef

class PlaylistSongCrossRefRepository(private val playlistSongCrossRefDao: PlaylistSongCrossRefDao) {
    suspend fun addSongToPlaylist(songId: Long, playlistId: Long) {
        val crossRef = PlaylistSongCrossRef(playlistId, songId)
        playlistSongCrossRefDao.insert(crossRef)
    }

    suspend fun removeSongFromPlaylist(songId: Long, playlistId: Long) {
        val crossRef = PlaylistSongCrossRef(playlistId, songId)
        playlistSongCrossRefDao.delete(crossRef)
    }

    suspend fun getSongsForPlaylist(playlistId: Long): List<Long> {
        return playlistSongCrossRefDao.getSongsForPlaylist(playlistId)
    }
}
