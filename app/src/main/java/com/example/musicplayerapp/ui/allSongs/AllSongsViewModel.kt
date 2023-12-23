package com.example.musicplayerapp.ui.allSongs

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayerapp.AudioModel
import java.io.File

class AllSongsViewModel : ViewModel() {

    private val _songsList = MutableLiveData<ArrayList<AudioModel>>()
    val songsList: LiveData<ArrayList<AudioModel>> = _songsList

    fun fetchSongs(cursor: Cursor?, context: Context) {
        val songs = mutableListOf<AudioModel>()


        cursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val path = cursor.getString(1)
                    val file = File(path)

                    val songData = AudioModel(
                        cursor.getString(1),
                        cursor.getString(0),
                        cursor.getString(2),
                        cursor.getString(3),
                        getArtUriFromMusicFile(context, file)
                    )
                    if (File(songData.path).exists()) {
                        songs.add(songData)
                    }
                } while (cursor.moveToNext())
            }
        }

        _songsList.value = ArrayList(songs)
        cursor?.close()
    }

    fun getArtUriFromMusicFile(context: Context, file: File): String {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursorCol = arrayOf(MediaStore.Audio.Media.ALBUM_ID)
        val selection = (
                "${MediaStore.Audio.Media.IS_MUSIC}=1 AND ${MediaStore.Audio.Media.DATA}=?"
                )
        val selectionArgs = arrayOf(file.absolutePath)
        val cursor =
            context.applicationContext.contentResolver.query(uri, cursorCol, selection, selectionArgs, null)

        // If the cusor count is greater than 0 then parse the data and get the art id.
        if (cursor != null && cursor.moveToFirst()) {
            val albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
            val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
            val albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId)
            cursor.close()
            return albumArtUri.toString()
        }

        cursor?.close()
        return Uri.EMPTY.toString()
    }
}