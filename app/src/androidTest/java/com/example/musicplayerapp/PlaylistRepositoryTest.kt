package com.example.musicplayerapp

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayerapp.data.database.PlaylistDao
import com.example.musicplayerapp.data.database.PlaylistDatabase
import com.example.musicplayerapp.data.database.PlaylistRepository
import com.example.musicplayerapp.data.database.entities.Playlist
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class PlaylistRepositoryTest {
    private lateinit var playlistDao: PlaylistDao
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var db: PlaylistDatabase
    private lateinit var playlist: Playlist

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, PlaylistDatabase::class.java).build()
        playlistDao = db.playlistDao()
        playlistRepository = PlaylistRepository(playlistDao)

        playlist = Playlist(1, "testPlaylist")
    }

    @After
    fun tearDown() {
        db.close()
    }
    @Test
    fun getAllPlaylists() = runBlocking {
        val secondPlaylist = Playlist(2, "secondTestPlaylist")
        playlistRepository.insert(playlist)
        playlistRepository.insert(secondPlaylist)

        val allPlaylists = playlistRepository.allPlaylists.first()

        assertTrue(allPlaylists.size == 2)
        assertTrue(allPlaylists.contains(playlist))
        assertTrue(allPlaylists.contains(secondPlaylist))
    }

    @Test
    fun insert() = runBlocking {
        playlistRepository.insert(playlist)

        val allPlaylists = playlistRepository.allPlaylists.first()
        assertTrue(allPlaylists.contains(playlist))
    }

    @Test
    fun update() = runBlocking {
        playlistRepository.insert(playlist)

        val updatedPlaylist = Playlist(1, "updatedPlaylist")
        playlistRepository.update(updatedPlaylist)

        val allPlaylists = playlistRepository.allPlaylists.first()
        assertTrue(allPlaylists.contains(updatedPlaylist))
    }

    @Test
    fun updateFalse() = runBlocking {
        playlistRepository.insert(playlist)

        // false id
        val updatedPlaylist = Playlist(5, "updatedPlaylist")
        playlistRepository.update(updatedPlaylist)

        val allPlaylists = playlistRepository.allPlaylists.first()
        assertFalse(allPlaylists.contains(updatedPlaylist))
    }

    @Test
    fun delete() = runBlocking {
        playlistRepository.insert(playlist)

        playlistRepository.delete(playlist)

        val allPlaylists = playlistRepository.allPlaylists.first()
        assertFalse(allPlaylists.contains(playlist))
    }

    @Test
    fun getPlaylistNameById() = runBlocking {
        playlistRepository.insert(playlist)

        assertEquals("testPlaylist", playlistRepository.getPlaylistNameById(1))
    }
}