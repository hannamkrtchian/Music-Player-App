package com.example.musicplayerapp.ui.allSongs

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R
import com.example.musicplayerapp.databinding.FragmentAllSongsBinding

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
            ViewModelProvider(this).get(AllSongsViewModel::class.java)

        _binding = FragmentAllSongsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AllSongsViewModel::class.java)

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

        //
        val contentResolver = requireActivity().contentResolver

        // cursor
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, selection, null, null
        )

        viewModel.fetchSongs(cursor)

        // Show "error" text view if there are no songs
        viewModel.songsList.observe(viewLifecycleOwner) { songsList ->
            if (songsList.isEmpty()) {
                textViewNoSongs.visibility = View.VISIBLE
            } else {
                textViewNoSongs.visibility = View.GONE
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                //recyclerView.adapter = YourAdapter(songsList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}