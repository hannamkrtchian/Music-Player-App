package com.example.musicplayerapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.musicplayerapp.AudioModel

@Entity(tableName = "playlist")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,

    // relationship with songs
)