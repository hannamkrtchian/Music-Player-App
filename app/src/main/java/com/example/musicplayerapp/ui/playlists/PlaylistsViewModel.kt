package com.example.musicplayerapp.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.database.PlaylistDao
import com.example.musicplayerapp.data.database.PlaylistDatabase
import com.example.musicplayerapp.data.database.PlaylistRepository
import com.example.musicplayerapp.data.database.entities.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistRepository: PlaylistRepository) : ViewModel() {

    private val allPlaylists: Flow<List<Playlist>> = playlistRepository.allPlaylists

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