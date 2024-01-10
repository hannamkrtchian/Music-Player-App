package com.example.musicplayerapp.ui

import android.app.AlertDialog
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
import com.example.musicplayerapp.data.database.entities.Song
import com.example.musicplayerapp.ui.allSongs.AllSongsViewModel
import com.example.musicplayerapp.ui.playlists.CreatePlaylistDialogFragment
import com.example.musicplayerapp.ui.playlists.PlaylistsViewModel
import com.example.musicplayerapp.ui.playlists.PlaylistsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ShowSongListActivity : AppCompatActivity() {

    // variables
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

    // viewmodels
    private lateinit var allSongsViewModel: AllSongsViewModel
    private lateinit var playlistsViewModel: PlaylistsViewModel

    // context
    private lateinit var context: Context

    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_song_list)

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

        // viewmodels
        allSongsViewModel = ViewModelProvider(this)[AllSongsViewModel::class.java]
        playlistsViewModel = ViewModelProvider(this, PlaylistsViewModelFactory(
            playlistRepository,
            songRepository,
            playlistSongCrossRefRepository))[PlaylistsViewModel::class.java]

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

            delete.setOnClickListener { deletePlaylist(playlistId) }
            edit.setOnClickListener { openDialog(playlistId, playlistName) }
        }

        loadSongsForPlaylist(playlistId)

    }

    private fun loadSongsForPlaylist(playlistId: Long) {
        mainScope.launch {
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
                val arrayListSongs = ArrayList(songsList)

                recyclerView.adapter = MusicListAdapter(arrayListSongs, context)
            }
        }
    }

    private suspend fun fetchSongsForPlaylist(playlistId: Long): List<AudioModel> = suspendCoroutine { continuation ->
        val allSongs: MutableList<AudioModel> = mutableListOf()

        lifecycleScope.launch(Dispatchers.IO) {
            // Get song IDs associated with the playlist from the cross-reference table
            val songIds = playlistSongCrossRefRepository.getSongsForPlaylist(playlistId)

            // Retrieve song titles and artists based on the song IDs
            val songsInfo = songIds.map { songId ->
                val song = songRepository.getSongById(songId)
                song?.let { Pair(it.title, it.artist) }
            }

            val cursorList = mutableListOf<Cursor>()

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
                    MediaStore.Audio.Media.TITLE + " ASC"
                )

                cursor?.let { cursorList.add(it) }
            }

            // Process the retrieved song data after all cursors are available
            cursorList.forEach { cursor ->
                cursor.use { cursor ->
                    while (cursor.moveToNext()) {
                        // Process song data and add it to allSongs list
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

            continuation.resume(allSongs.toList())
        }
    }

    private fun deletePlaylist(playlistId: Long) {
        mainScope.launch {
            val playlist = playlistRepository.getPlaylist(playlistId)
            playlistsViewModel.deletePlaylist(playlist)
        }

        onBackPressedDispatcher.onBackPressed()
    }

    private fun openDialog(playlistId: Long, playlistName: String) {
        val dialog = CreatePlaylistDialogFragment()

        dialog.setPlaylistName(playlistName)

        // Set FragmentResultListener to handle the result after the dialog is dismissed
        supportFragmentManager.setFragmentResultListener("requestKey", this) { _, result ->
            val newPlaylistName = result.getString("playlistName")
            // Retrieve the list of checked songs from the Bundle
            val checkedSongs: List<Song>? = result.getSerializable("checkedSongs") as? List<Song>

            lifecycleScope.launch {
                when {
                    newPlaylistName.isNullOrEmpty() -> showErrorDialog(R.string.playlist_name_empty)
                    (playlistsViewModel.checkPlaylistExists(newPlaylistName) != null
                            && playlistsViewModel.checkPlaylistExists(newPlaylistName)?.name != newPlaylistName) -> showErrorDialog(R.string.playlist_name_exists)
                    checkedSongs.isNullOrEmpty() -> showErrorDialog(R.string.no_songs_selected)
                    else -> updatePlaylist(playlistId, newPlaylistName, checkedSongs)
                }
            }
        }

        dialog.show(supportFragmentManager, "CreatePlaylistDialogFragment")
    }

    private fun updatePlaylist(id: Long, name: String, songs: List<Song>) {
        mainScope.launch(Dispatchers.IO) {
            val playlistToUpdate = playlistRepository.getPlaylist(id)

            // Update playlist properties
            playlistToUpdate.name = name
            playlistsViewModel.updatePlaylist(playlistToUpdate)

            // Delete all songs in a playlist
            playlistsViewModel.deleteSongsFromPlaylist(id)

            // Update songs in playlist
            songs.forEach { song ->
                // Retrieve song ID from the database and insert it into the cross-reference table
                val songId = playlistsViewModel.getSongId(song.title, song.artist)
                if (songId != null) {
                    // Check if the entry already exists in PlaylistSongCrossRef
                    val entryExists = playlistsViewModel.checkEntryExists(songId, id)
                    if (!entryExists) {
                        // Entry doesn't exist, so add it to the cross-reference table
                        playlistSongCrossRefRepository.addSongToPlaylist(songId, id)
                    }
                }
            }

            // Reload the playlist title and songs
            mainScope.launch(Dispatchers.Main) {
                // Reload the playlist title
                titleTv.text = name

                // Reload songs for the updated playlist
                loadSongsForPlaylist(id)
            }
        }
    }


    private fun showErrorDialog(messageResId: Int) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.error))
        alertDialogBuilder.setMessage(getString(messageResId))
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel() // Cancel the CoroutineScope to avoid leaks
    }
}