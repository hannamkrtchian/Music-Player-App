package com.example.musicplayerapp.ui.playlists

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.bumptech.glide.Glide
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.AudioModel
import com.example.musicplayerapp.MyMediaPlayer
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.database.entities.Song
import com.example.musicplayerapp.ui.MusicPlayerActivity

class PlaylistMusicListAdapter(private var songsList: List<Song>, private val context: Context
) : RecyclerView.Adapter<PlaylistMusicListAdapter.ViewHolder>() {

    private val checkedSongs: MutableList<Song> = mutableListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.music_text)
        var checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val songData: Song = songsList[position]

        if (songData.artist.contains("unknown")) {
            holder.titleTextView.text = songData.title
        } else {
            holder.titleTextView.text = songData.title + " - " + songData.artist
        }

        holder.checkBox.visibility = View.VISIBLE

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedSongs.add(songData)
            } else {
                checkedSongs.remove(songData)
            }
        }
    }

    fun getCheckedSongs(): List<Song> {
        return checkedSongs.toList()
    }
}