package com.example.musicplayerapp.ui.playlists

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.musicplayerapp.R

class CreatePlaylistDialogFragment : DialogFragment() {

    interface CreatePlaylistDialogListener {
        fun onCreatePlaylist(name: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.playlist_dialog, null)

        val editTextPlaylistName = dialogView.findViewById<EditText>(R.id.editTextPlaylistName)

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(dialogView)
                .setTitle(R.string.title_of_the_playlist)
                .setPositiveButton(R.string.create) { _, _ ->
                    val playlistName = editTextPlaylistName.text.toString()
                    val result = Bundle().apply {
                        putString("playlistName", playlistName)
                    }
                    parentFragmentManager.setFragmentResult("requestKey", result)
                    dismiss()
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
