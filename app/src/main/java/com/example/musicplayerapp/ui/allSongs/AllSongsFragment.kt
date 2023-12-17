package com.example.musicplayerapp.ui.allSongs

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
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.FragmentAllSongsBinding
import com.example.musicplayerapp.ui.AudioModel
import com.example.musicplayerapp.ui.MusicListAdapter

class AllSongsFragment : Fragment() {

    private var _binding: FragmentAllSongsBinding? = null
    private lateinit var viewModel: AllSongsViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val allSongsViewModel =
            ViewModelProvider(this)[AllSongsViewModel::class.java]

        _binding = FragmentAllSongsBinding.inflate(inflater, container, false)


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AllSongsViewModel::class.java]

        // Get the recyclerview with the songs and the textview with "no songs"
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_all_songs)
        val textViewNoSongs = view.findViewById<TextView>(R.id.no_songs)

        // projection for cursor
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )

        // selection for cursor
        val selection: String = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        // declare contentResolver and appContext
        val contentResolver = requireActivity().contentResolver
        val appContext = requireContext().applicationContext


        val songs = mutableListOf<AudioModel>()

        // cursor
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, selection, null, null)

        viewModel.fetchSongs(cursor)

        // Show "error" text view if there are no songs, otherwise list with songs
        viewModel.songsList.observe(viewLifecycleOwner) { songsList ->
            if (songsList.isEmpty()) {
                textViewNoSongs.visibility = View.VISIBLE
            } else {
                textViewNoSongs.visibility = View.GONE
                recyclerView.layoutManager = LinearLayoutManager(appContext)
                recyclerView.adapter = MusicListAdapter(songsList, appContext)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}