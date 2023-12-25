package com.example.musicplayerapp.ui.allSongs

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.MusicListAdapter
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.FragmentAllSongsBinding
import java.io.File


class AllSongsFragment : Fragment() {

    private var _binding: FragmentAllSongsBinding? = null
    private lateinit var viewModel: AllSongsViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Declare variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewNoSongs: TextView
    private lateinit var appContext: Context
    private lateinit var contentResolver: ContentResolver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllSongsBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AllSongsViewModel::class.java]

        // Get the recyclerview with the songs and the textview with "no songs"
        recyclerView = view.findViewById(R.id.recycler_view_all_songs)
        textViewNoSongs = view.findViewById(R.id.no_songs)

        // projection for cursor
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST
        )

        // selection for cursor
        val selection: String = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        // assign contentResolver and appContext
        contentResolver = requireActivity().contentResolver
        appContext = requireContext().applicationContext

        // cursor
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, selection, null,
            MediaStore.Audio.Media.TITLE + " ASC")

        viewModel.fetchSongs(cursor, appContext)

        // Show "error" text view if there are no songs, otherwise list with songs
        viewModel.songsList.observe(viewLifecycleOwner) { songsList ->
            if (songsList.isEmpty()) {
                textViewNoSongs.visibility = View.VISIBLE
            } else {
                textViewNoSongs.visibility = View.GONE
                recyclerView.layoutManager = LinearLayoutManager(appContext)

                // album art
                songsList.forEach { song ->
                    val file = File(song.path)
                    val albumArtUri = viewModel.getArtUriFromMusicFile(appContext, file)
                    song.albumArtUri = albumArtUri
                }

            recyclerView.adapter = MusicListAdapter(songsList, appContext)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}