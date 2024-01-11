# Music Player App

Frameworks and libraries used:
- Glide for image loading (album cover of a specific song) - https://github.com/bumptech/glide
- Room for local data storage
- JUnit for testing
- OkHttp to fetch lyrics from the internet (lyrics.ovh API)
- kotlinx.coroutines for asynchronously retrieving data from the database and fetching lyrics from the internet.

To use the app, music must be available on the device, stored under the 'music' folder. If music is downloaded, it needs to be moved to this folder first.
Note: on some Android API's (for example API 24 and 25) it is not possible to access or move files in the intern storage. This means that the app will work, but the songs will not show.

Tutorial used to access music from the device and play/pause music, etc:
- Music Player Application | Android Studio Tutorial | 2024 - https://www.youtube.com/watch?v=1D1Jo1sLBMo 

## Layouts (xml):

- activity main
- activity music player: activity when a song is played (also in landscape mode)
- activity show song list: all songs of a playlist displayed in a recycler view
- fragment all songs: all songs displayed in a recycler view
- fragment artists: not implemented
- fragment lyrics dialog: dialog where lyrics is showed
- fragment playlists: all playlists displayed in a recycler view
- playlist dialog: name input and list of all songs to create or update a playlist
- recycler item: layout of one item in a recycler view

## UI classes:

1. all songs
  - AllSongsFragment

First we get the recyclerview and the text view “no songs” from the layout. Then we make a projection (song data that has to be stored in AudioModel) and a selection (only music) to use in “cursor”. Then we call the function “fetchSongs”, that is located in the AllSongsViewModel, with this cursor. This function fills the songsList with the songs found on the device. Then we observe the songsList. If it’s empty we make the textView “no songs” visible. Else we add to each song in the list the album art (cover) of the song and then we fill the recycler view by calling the MusicListAdapter (see further).

  - AllSongsViewModel

The “fetchSongs” function takes a cursor and a context to fetch the songs on the phone and project these in an array list: songsList.

The “getArtUriFromMusicFile” function takes a context and a file to return a string of the album art uri.

2. artists

Has not been implemented because the logic is similar to the playlists folder.

3. playlists
  - CreatePlaylistDialogFragment

In this Fragment, the song list is fetched, and a dialog is generated. Within the dialog, there is an input field for defining the playlist title, along with a RecyclerView displaying the available songs. Each song is accompanied by a checkbox. Upon clicking "Create", the selected name and checked songs are transmitted back to the originating activity or fragment within a Bundle named "result".

  - PlaylistAdapter

The PlaylistAdapter receives a list of playlists and a context, and returns a RecyclerView.  This class is used to put each playlist in a recycler item. Used in the PlaylistsFragment, it sets a clickListener to each “item”, allowing the opening of the ShowSongListActivity when a playlist is clicked. It uses an Intent to pass the playlist ID along.

  - PlaylistMusicAdapter

The PlaylistMusicListAdapter takes a list of songs and a context, producing a RecyclerView. This class is used to put all songs in a recycler item. Used in the CreatePlaylistDialogFragment, it sets a CheckedChangeListener on the checkbox of each song, facilitating the addition of a song to the playlist.

  - PlaylistsFragment

In the PlaylistsFragment, the user's created playlists are displayed, along with a button to add a new playlist. Clicking this button opens the CreatePlaylistDialogFragment. Upon receiving data from the dialog, it undergoes a verification check. If everything is correct, a new playlist is created using the "onCreatePlaylist" method. This method requires a name (String) and songs (List<Song>) to create a playlist in the database. To accomplish this, it uses the "insertPlaylist" method from the PlaylistsViewModel. Additionally, it inserts a PlaylistSongCrossRef for each song in the playlist, using both the playlist ID and song ID.

  - PlaylistsViewModel

The PlaylistsViewModel contains methods to interact with the repositories to get, insert, update or delete data from the database.

4. LyricsDialogFragment

The LyricsDialogFragment is responsible for presenting the lyrics of a song. It contains the "displayLyrics" method, which initially calls the "extractLyricsFromJson" method. Then, it formats the lyrics for improved readability. The "extractLyricsFromJson" method transforms the JSON object into a string containing only the text of the lyrics.

5. MusicPlayerActivity

Music Player Activity is the activity to play, pause, skip, etc. the music. It contains the logic for the “back” button, the seek bar, and setting resources related to the music.

The method "setResourcesWithMusic" is responsible for displaying the artist and title of the current song, along with its cover image using Glide. It establishes click listeners for the pause button, as well as the previous and next buttons. It will also play the music. The following functions are called within this method:

playMusic(): starts the music and sets the seek bar progress to zero.
playNextSong(): checks if the current song is the last one, to set the first song as the next one. If it’s not the last song that’s being played, it just plays the next song.
playPreviousSong(): checks if the current song is the first one, to set the last song as the previous one. If it’s not the first song that’s being played, it just plays the previous song.
pausePlay(): pause or resume the mediaPlayer

The activity also features methods to display lyrics:

showLyricsDialog: generates a LyricsDialogFragment and sends the lyrics within a Bundle
fetchLyrics: invokes "fetchLyricsFromInternet" within a coroutine scope and subsequently calls "showLyricsDialog".
fetchLyricsFromInternet: uses the OkHttpClient to request lyrics from lyrics.ovh with the parameters “title” and “artist”

6. ShowSongListActivity

This activity is designed to display songs within a specific playlist. It accomplishes this by invoking the MusicListAdapter, providing it with a list of songs associated with the playlist. The "fetchSongsForPlaylist" method utilizes the PlaylistSongCrossRefRepository to retrieve song IDs from the PlaylistSongCrossRef table corresponding to the playlist. Using these song IDs, the method obtains the title and artist information for each song from the Song table and subsequently fetches the corresponding songs from the device based on their titles and artists.

The "openDialog" function is responsible for creating the CreatePlaylistDialogFragment, but in this instance, it is intended for updating the playlist. Upon verifying the result from the dialog, the "updatePlaylist" method is invoked.

## Data classes (room):

1. entities
  - Playlist: a Playlist with an ID and a name
  - PlaylistSongCrossRef: many to many relationship between song and playlist
  - Song: a song with an ID, title and artist
2. PlaylistDao

This class provides a set of methods for the retrieval, insertion, updating, and deletion of playlists. The "getPlaylistNameById" method accepts a playlist ID and exclusively returns the name of the associated playlist. The "existingPlaylist" method determines the existence of a playlist with a given name, taking the playlist name as input and returning the playlist if it already exists. Lastly, the "getPlaylist" method retrieves the entire playlist based on the provided playlist ID.

3. PlaylistDatabase

This class defines the Room Database for the application, including the entities and their corresponding DAOs. It incorporates the Singleton pattern to ensure a single, thread-safe instance of the database is used throughout the application.

4. PlaylistRepository

This class is an abstraction layer between the data access operations and the rest of the application. It encapsulates the logic for interacting with playlists, providing a clean API for the rest of the application to perform operations on the playlists.

5. PlaylistSongCrossRefDao

This class offers methods for inserting and deleting entries. The "getSongsForPlaylist" method, given a playlist ID, retrieves all associated song IDs for that playlist. The "existingEntry" method checks the existence of an entry with a given playlist ID and song ID. It returns a list containing the playlist ID if the entry already exists. Additionally, the "deleteAllSongsFromPlaylist" method, when provided with a playlist ID, removes all entries associated with that playlist.

6. PlaylistSongCrossRefRepository

This repository class abstracts away the details of database operations related to playlist-song associations, providing a clear and straightforward interface for the application to manage these relationships.

7. SongDao

This class offers methods for getting, inserting and deleting songs. The "getSongIdByTitleAndArtist" method, given a title and artist, retrieves the associated song ID. The "getSongById" method takes a song ID and returns a song.

8. SongRepository

This class provides a high-level interface for working with songs, handling database operations, and exposing observed data changes through a Flow.


## Other classes:
- AudioModel: model class that holds information about audio files
- MainActivity

Here we ask for permissions to read the external storage/media files.

- MusicApplication

This class initializes the database and the repositories.

- MusicListAdapter

The MusicListAdapter receives an arraylist of AudioModels and a context, and returns a RecyclerView.  This class is used to put each song (AudioModel) in a recycler item. Used in the AllSongsFragment, it sets a clickListener to each “item”, allowing the opening of the MusicPlayerActivity when a song is clicked. It uses an Intent to pass the songs list along.

- MyMediaPlayer

This class is designed to provide a single instance of MediaPlayer throughout the application using the Singleton pattern.
