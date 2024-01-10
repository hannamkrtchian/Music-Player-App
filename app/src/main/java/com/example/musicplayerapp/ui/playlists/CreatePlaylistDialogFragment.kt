package com.example.musicplayerapp.ui.playlists

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.MusicApplication
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.database.entities.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CreatePlaylistDialogFragment : DialogFragment() {

    interface CreatePlaylistDialogListener {
        fun onCreatePlaylist(name: String, songs: List<Song>)
    }

    // Only when editing a playlist
    private var playlistName: String? = null

    fun setPlaylistName(name: String) {
        playlistName = name
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.playlist_dialog, null)

        // declare repository for songs
        val application = requireActivity().application as MusicApplication
        val songsRepository = application.songRepository

        // elements of dialog
        val editTextPlaylistName = dialogView.findViewById<EditText>(R.id.editTextPlaylistName)
        val selectSongsRecyclerView = dialogView.findViewById<RecyclerView>(R.id.recycler_view_all_songs)

        // appcontext
        val appContext = requireContext().applicationContext

        // Inside your function or coroutine
        val songsFlow: Flow<List<Song>> = songsRepository.allSongs

        // Collect the data from the Flow to get the List<Song>
        // This can be done within a coroutine scope
        lifecycleScope.launch {
            var songsList: List<Song>
            songsFlow.collect { songs ->
                songsList = songs // Collecting the list from the Flow
                selectSongsRecyclerView.layoutManager = LinearLayoutManager(appContext)
                selectSongsRecyclerView.adapter = PlaylistMusicListAdapter(songsList, appContext)
            }
        }

        // set title according to situation
        val title: String = if (playlistName.isNullOrEmpty()) {
            getString(R.string.title_of_the_playlist)
        } else {
            playlistName as String
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(dialogView)
                .setTitle(getString(R.string.title_of_the_playlist))
                .setPositiveButton(R.string.create) { _, _ ->
                    val playlistName = editTextPlaylistName.text.toString()
                    val checkedSongs = (selectSongsRecyclerView.adapter as PlaylistMusicListAdapter).getCheckedSongs()

                    val result = Bundle().apply {
                        // playlist name
                        putString("playlistName", playlistName)
                        // playlist songs
                        putSerializable("checkedSongs", ArrayList(checkedSongs))
                    }
                    parentFragmentManager.setFragmentResult("requestKey", result)
                    dismiss()
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    dismiss()
                }
            // Set default value for editTextPlaylistName
            editTextPlaylistName.setText(title)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}