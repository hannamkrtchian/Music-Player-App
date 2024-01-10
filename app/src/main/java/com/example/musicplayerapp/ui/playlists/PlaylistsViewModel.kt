package com.example.musicplayerapp.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.MusicApplication
import com.example.musicplayerapp.data.database.PlaylistRepository
import com.example.musicplayerapp.data.database.PlaylistSongCrossRefRepository
import com.example.musicplayerapp.data.database.SongRepository
import com.example.musicplayerapp.data.database.entities.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistRepository: PlaylistRepository,
                         private val songRepository: SongRepository,
                         private val playlistSongCrossRefRepository: PlaylistSongCrossRefRepository) : ViewModel() {

    val allPlaylists: Flow<List<Playlist>> = playlistRepository.allPlaylists

    fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.insert(playlist)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.update(playlist)
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistRepository.delete(playlist)
        }
    }

    suspend fun getSongId(title: String, artist: String): Long? {
        return songRepository.getSongId(title, artist)
    }

    suspend fun checkPlaylistExists(name: String): Playlist? {
        return playlistRepository.existingPlaylist(name)
    }

    suspend fun checkEntryExists(songId: Long, playlistId: Long): Boolean {
        return playlistSongCrossRefRepository.existingEntry(songId, playlistId).isNotEmpty()
    }

    suspend fun deleteSongsFromPlaylist(playlistId: Long) {
        playlistSongCrossRefRepository.deleteAllSongsFromPlaylist(playlistId)
    }
}

class PlaylistsViewModelFactory(private val playlistRepository: PlaylistRepository,
                                private val songRepository: SongRepository,
                                private val playlistSongCrossRefRepository: PlaylistSongCrossRefRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaylistsViewModel(playlistRepository, songRepository, playlistSongCrossRefRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}