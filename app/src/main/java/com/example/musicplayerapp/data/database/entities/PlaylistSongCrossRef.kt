package com.example.musicplayerapp.data.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "songId"])
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long
)