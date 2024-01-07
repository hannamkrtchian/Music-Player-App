package com.example.musicplayerapp.ui

import android.content.Context
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.AudioModel
import com.example.musicplayerapp.MusicApplication
import com.example.musicplayerapp.MusicListAdapter
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.database.PlaylistRepository
import com.example.musicplayerapp.data.database.PlaylistSongCrossRefRepository
import com.example.musicplayerapp.data.database.SongRepository
import com.example.musicplayerapp.ui.allSongs.AllSongsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ShowSongListActivity : AppCompatActivity() {
    var currentIndex: Int = -1

    private lateinit var back: ImageView
    private lateinit var titleTv: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewNoSongs: TextView
    private lateinit var edit: ImageView
    private lateinit var delete: ImageView

    // repositories
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var songRepository: SongRepository
    private lateinit var playlistSongCrossRefRepository: PlaylistSongCrossRefRepository

    // viewmodel
    private lateinit var allSongsViewModel: AllSongsViewModel

    // context
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_song_list)

        // viewmodel
        allSongsViewModel = ViewModelProvider(this)[AllSongsViewModel::class.java]

        back = findViewById(R.id.back_button)
        titleTv = findViewById(R.id.playlist_name)
        recyclerView = findViewById(R.id.recycler_view_songs_playlist)
        textViewNoSongs = findViewById(R.id.no_songs)
        edit = findViewById(R.id.edit_button)
        delete = findViewById(R.id.delete_button)

        // repositories
        val application = application as MusicApplication
        playlistRepository = application.playlistRepository
        songRepository = application.songRepository
        playlistSongCrossRefRepository = application.playlistSongCrossRefRepository

        // context
        context = this

        // Back button logic
        back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        titleTv.isSelected = true

        val playlistId = intent.getLongExtra("PLAYLIST_ID", -1)

        // playlist name
        lifecycleScope.launch {
            val playlistName: String = playlistRepository.getPlaylistNameById(playlistId)
            titleTv.text = playlistName
        }


        val songsList = fetchSongsForPlaylist(playlistId)

        if (songsList.isEmpty()) {
            textViewNoSongs.visibility = View.VISIBLE
        } else {
            textViewNoSongs.visibility = View.GONE
            recyclerView.layoutManager = LinearLayoutManager(context)

            // album art
            songsList.forEach { song ->
                val file = File(song.path)
                val albumArtUri = allSongsViewModel.getArtUriFromMusicFile(context, file)
                song.albumArtUri = albumArtUri
            }

            // convert to arraylist
            // Assuming songsList is a List<AudioModel>
            val arrayListSongs = ArrayList(songsList)

            recyclerView.adapter = MusicListAdapter(arrayListSongs, context)
        }
    }

    private fun fetchSongsForPlaylist(playlistId: Long): List<AudioModel> {

        val allSongs: MutableList<AudioModel> = mutableListOf()

        lifecycleScope.launch(Dispatchers.IO) {
            // Get song IDs associated with the playlist from the cross-reference table
            val songIds = playlistSongCrossRefRepository.getSongsForPlaylist(playlistId)

            // Retrieve song titles and artists based on the song IDs
            val songsInfo = songIds.map { songId ->
                val song = songRepository.getSongById(songId)
                song.let {
                    it?.let { it1 -> Pair(it1.title, it.artist) }
                }
            }

            songsInfo.forEach { pair ->
                val title = pair?.first
                val artist = pair?.second

                // projection for cursor
                val projection = arrayOf(
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ARTIST
                )

                // selection for cursor
                val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 " +
                        "AND ${MediaStore.Audio.Media.TITLE} == ?" +
                        "AND ${MediaStore.Audio.Media.ARTIST} == ?"

                val selectionArgs = arrayOf(title, artist)

                // assign contentResolver and appContext
                val contentResolver = contentResolver

                // cursor
                val cursor: Cursor? = contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, selectionArgs,
                    MediaStore.Audio.Media.TITLE + " ASC")

                cursor?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val path = cursor.getString(1)
                        val file = File(path)

                        val songData = AudioModel(
                            cursor.getString(1),
                            cursor.getString(0),
                            cursor.getString(2),
                            cursor.getString(3),
                            allSongsViewModel.getArtUriFromMusicFile(context, file)
                        )
                        allSongs.add(songData)
                    }
                }
            }
        }

        return allSongs.toList()
    }
}