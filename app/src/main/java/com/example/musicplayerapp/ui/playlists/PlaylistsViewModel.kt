package com.example.musicplayerapp.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.database.PlaylistRepository
import com.example.musicplayerapp.data.database.entities.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistRepository: PlaylistRepository) : ViewModel() {

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
}

class PlaylistsViewModelFactory(private val playlistRepository: PlaylistRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaylistsViewModel(playlistRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}