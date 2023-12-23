package com.example.musicplayerapp

import android.net.Uri
import java.io.Serializable

data class AudioModel(
    var path: String, var title: String, var duration: String,
    var artist: String, var albumArtUri: String
) : Serializable {

}