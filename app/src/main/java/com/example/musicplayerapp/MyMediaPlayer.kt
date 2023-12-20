package com.example.musicplayerapp

import android.media.MediaPlayer

class MyMediaPlayer private constructor() {
    // static
    companion object {
        private var instance: MediaPlayer? = null

        fun getInstance(): MediaPlayer {
            if (instance == null) {
                instance = MediaPlayer()
            }
            return instance!!
        }

        var currentIndex: Int = -1
    }
}