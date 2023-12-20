package com.example.musicplayerapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.ui.MusicPlayerActivity

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