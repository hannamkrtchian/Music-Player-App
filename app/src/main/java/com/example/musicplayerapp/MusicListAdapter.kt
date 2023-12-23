package com.example.musicplayerapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.ui.MusicPlayerActivity

class MusicListAdapter(private val songsList: ArrayList<AudioModel>,
                       private val context: Context
) : RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.music_text)
        var iconImageView: ImageView = itemView.findViewById(R.id.icon_view)

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
        val songData: AudioModel = songsList[position]

        holder.titleTextView.text = songData.title + " - " + songData.artist

        // album art
        val albumArtUri = Uri.parse(songData.albumArtUri)
        Glide.with(context)
            .load(albumArtUri)
            .placeholder(R.drawable.baseline_music_note_24) // Placeholder image
            .error(R.drawable.baseline_music_note_24) // Error image if loading fails
            .into(holder.iconImageView)

        holder.itemView.setOnClickListener {
                // start other activity and pass songslist
                MyMediaPlayer.getInstance().reset()
                MyMediaPlayer.currentIndex = position
                val intent = Intent(context, MusicPlayerActivity::class.java)
                intent.putExtra("LIST", songsList)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
        }
    }
}