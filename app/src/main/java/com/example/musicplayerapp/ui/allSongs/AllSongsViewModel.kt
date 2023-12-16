package com.example.musicplayerapp.ui.allSongs

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayerapp.ui.AudioModel
import java.io.File

class AllSongsViewModel : ViewModel() {

    private val _songsList = MutableLiveData<List<AudioModel>>()
    val songsList: LiveData<List<AudioModel>> = _songsList

    fun fetchSongs(cursor: Cursor?) {
        val songs = mutableListOf<AudioModel>()
        cursor.use { cursor ->
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val songData = AudioModel(
                        cursor.getString(1),
                        cursor.getString(0),
                        cursor.getString(2)
                    )
                    if (File(songData.path).exists()) {
                        songs.add(songData)
                    }
                }
            }
        }

        _songsList.value = songs
    }
}