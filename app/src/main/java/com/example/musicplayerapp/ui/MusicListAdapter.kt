package com.example.musicplayerapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R

class MusicListAdapter(private val songsList: ArrayList<AudioModel>,
                       private val context: Context
) : RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.music_title_text)
        var iconImageView: ImageView = itemView.findViewById(R.id.icon_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val songData: AudioModel = songsList[position]
        holder.titleTextView.text = songData.title
    }
}