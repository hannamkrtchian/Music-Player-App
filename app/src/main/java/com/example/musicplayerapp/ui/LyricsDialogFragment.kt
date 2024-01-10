package com.example.musicplayerapp.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Html.fromHtml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.musicplayerapp.R
import org.json.JSONException
import org.json.JSONObject
import okhttp3.*

class LyricsDialogFragment : DialogFragment() {

    private var lyricsTextView: TextView? = null
    private lateinit var backBtn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lyrics_dialog, container, false)
        lyricsTextView = view.findViewById(R.id.lyrics)
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val window = dialog.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backBtn = view.findViewById(R.id.back)

        // Retrieve lyrics from arguments and display in TextView
        val lyrics = arguments?.getString("lyrics")
        lyricsTextView?.let {
            displayLyrics(lyrics)
        }

        backBtn.setOnClickListener {
            dialog?.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayLyrics(jsonLyrics: String?) {
        val lyrics = jsonLyrics?.let { extractLyricsFromJson(it) }

        if (!lyrics.isNullOrEmpty()) {
            val formattedLyrics = lyrics
                .replace("\r\n", "<br><br>")
                .replace("\n", "<br>")
            // Set the lyrics to the TextView
            lyricsTextView?.text = fromHtml(formattedLyrics)
        } else {
            lyricsTextView?.text = "Lyrics not available"
        }
    }

    private fun extractLyricsFromJson(jsonResponse: String): String? {
        return try {
            // Parse the JSON string
            val jsonObject = JSONObject(jsonResponse)

            // Retrieve the value associated with the lyrics key
            jsonObject.getString("lyrics")
        } catch (e: JSONException) {
            e.printStackTrace()
            null // Return null if there's an exception
        }
    }

}