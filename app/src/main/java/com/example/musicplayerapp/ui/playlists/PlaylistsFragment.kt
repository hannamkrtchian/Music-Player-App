package com.example.musicplayerapp.ui.playlists

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.MusicApplication
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.database.PlaylistRepository
import com.example.musicplayerapp.data.database.entities.Playlist
import com.example.musicplayerapp.databinding.FragmentPlaylistsBinding
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
    private lateinit var playlistsRepository: PlaylistRepository

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

        val application = requireActivity().application as MusicApplication
        playlistsRepository = application.repository

        val viewModelFactory = PlaylistsViewModelFactory(playlistsRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[PlaylistsViewModel::class.java]


        // Get the recyclerview with the playlists and the textview with "no playlists"
        recyclerView = view.findViewById(R.id.recycler_view_all_playlists)
        textViewNoPlaylists = view.findViewById(R.id.no_playlists)
        addPlaylistButton = view.findViewById(R.id.add_playlist)

        // add playlist
        addPlaylistButton.setOnClickListener {

            val dialog = CreatePlaylistDialogFragment()

            // Set FragmentResultListener to handle the result after the dialog is dismissed
            setFragmentResultListener("requestKey") { _, result ->
                val playlistName = result.getString("playlistName")
                if (!playlistName.isNullOrEmpty()) {
                    onCreatePlaylist(playlistName)
                }
            }

            dialog.show(parentFragmentManager, "CreatePlaylistDialogFragment")
        }

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
    override fun onCreatePlaylist(name: String) {
        if (name.isNotBlank()) {
            val playlist = Playlist(0, name)
            (viewModel as PlaylistsViewModel).insertPlaylist(playlist)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}