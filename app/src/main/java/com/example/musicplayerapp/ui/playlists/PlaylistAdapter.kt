package com.example.musicplayerapp.ui.playlists

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerapp.R
import com.example.musicplayerapp.ui.ShowSongListActivity
import com.example.musicplayerapp.data.database.entities.Playlist


class PlaylistAdapter(private val playlistList: List<Playlist>,
                      private val context: Context
) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.playlist_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlistList[position]

        holder.titleTextView.text = playlist.name

        holder.itemView.setOnClickListener {
            // start other activity and pass playlists
            val intent = Intent(context, ShowSongListActivity::class.java)
            intent.putExtra("PLAYLIST_ID", playlist.id)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }


}