package com.example.musicplayerapp.ui.playlists

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.MusicApplication
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.database.PlaylistRepository
import com.example.musicplayerapp.data.database.PlaylistSongCrossRefRepository
import com.example.musicplayerapp.data.database.SongRepository
import com.example.musicplayerapp.data.database.entities.Playlist
import com.example.musicplayerapp.data.database.entities.Song
import com.example.musicplayerapp.databinding.FragmentPlaylistsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlaylistsFragment : Fragment(), CreatePlaylistDialogFragment.CreatePlaylistDialogListener {

    private var _binding: FragmentPlaylistsBinding? = null
    private lateinit var viewModel: ViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Declare variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewNoPlaylists: TextView
    private lateinit var addPlaylistButton: Button
    private lateinit var appContext: Context

    // repositories
    private lateinit var playlistsRepository: PlaylistRepository
    private lateinit var songsRepository: SongRepository
    private lateinit var playlistSongCrossRefRepository: PlaylistSongCrossRefRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context.applicationContext
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // repositories
        val application = requireActivity().application as MusicApplication
        playlistsRepository = application.playlistRepository
        songsRepository = application.songRepository
        playlistSongCrossRefRepository = application.playlistSongCrossRefRepository

        // viewmodel
        val viewModelFactory = PlaylistsViewModelFactory(playlistsRepository, songsRepository, playlistSongCrossRefRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[PlaylistsViewModel::class.java]


        // Get the recyclerview with the playlists, the textview with "no playlists" and the add button
        recyclerView = view.findViewById(R.id.recycler_view_all_playlists)
        textViewNoPlaylists = view.findViewById(R.id.no_playlists)
        addPlaylistButton = view.findViewById(R.id.add_playlist)

        // add playlist button
        addPlaylistButton.setOnClickListener {
            val dialog = CreatePlaylistDialogFragment()

            // Set FragmentResultListener to handle the result after the dialog is dismissed
            setFragmentResultListener("requestKey") { _, result ->
                val playlistName = result.getString("playlistName")
                // Retrieve the list of checked songs from the Bundle
                val checkedSongs: List<Song>? = result.getSerializable("checkedSongs") as? List<Song>

                lifecycleScope.launch {
                    when {
                        playlistName.isNullOrEmpty() -> showErrorDialog(R.string.playlist_name_empty)
                        ((viewModel as PlaylistsViewModel).checkPlaylistExists(playlistName) != null) -> showErrorDialog(R.string.playlist_name_exists)
                        checkedSongs.isNullOrEmpty() -> showErrorDialog(R.string.no_songs_selected)
                        else -> onCreatePlaylist(playlistName, checkedSongs)
                    }
                }
            }

            dialog.show(parentFragmentManager, "CreatePlaylistDialogFragment")
        }

        // show no playlists textview or playlists
        lifecycleScope.launch {
            (viewModel as PlaylistsViewModel).allPlaylists.collect { playlists ->
                if (playlists.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    textViewNoPlaylists.visibility = View.GONE

                    val adapter = PlaylistAdapter(playlists, appContext)

                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(appContext)
                } else {
                    recyclerView.visibility = View.GONE
                    textViewNoPlaylists.visibility = View.VISIBLE
                }
            }
        }
    }

    // This method will be called when the user clicks "Create" in the dialog
    override fun onCreatePlaylist(name: String, songs: List<Song>) {
        if (name.isNotBlank()) {
            val playlist = Playlist(0, name)
            (viewModel as PlaylistsViewModel).insertPlaylist(playlist)

            // Observe the ID of the inserted playlist
            (viewModel as PlaylistsViewModel).allPlaylists.onEach { playlists ->
                val insertedPlaylist = playlists.find { it.name == name } // Retrieve the newly inserted playlist
                insertedPlaylist?.let { newPlaylist ->
                    // Once the new playlist is obtained, insert song IDs associated with this playlist into the cross-reference table
                    val playlistId = newPlaylist.id
                    songs.forEach { song ->
                        // Retrieve song ID from the database and insert it into the cross-reference table
                        val songId = (viewModel as PlaylistsViewModel).getSongId(song.title, song.artist)
                        if (songId != null) {
                            // Check if the entry already exists in PlaylistSongCrossRef
                            val entryExists = (viewModel as PlaylistsViewModel).checkEntryExists(songId, playlistId)
                            if (!entryExists) {
                                // Entry doesn't exist, so add it to the cross-reference table
                                playlistSongCrossRefRepository.addSongToPlaylist(songId, playlistId)
                            }
                        }
                    }
                }
            }.launchIn(lifecycleScope)
        }
    }

    private fun showErrorDialog(messageResId: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.error))
        alertDialogBuilder.setMessage(getString(messageResId))
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}