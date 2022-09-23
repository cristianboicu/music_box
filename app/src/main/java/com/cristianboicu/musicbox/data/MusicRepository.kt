package com.cristianboicu.musicbox.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log

class MusicRepository(private val appContext: Context) {

    fun searchDeviceSongs(): List<Song> {
        val cursor: Cursor? = createCursorFromBaseProjection(appContext)
        return getSongs(cursor)
    }

    private fun createCursorFromBaseProjection(context: Context): Cursor? {
        return try {
            context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                baseProjection, null, null, null)
        } catch (e: SecurityException) {
            null
        }
    }

    private fun getSongs(cursor: Cursor?): List<Song> {
        val songs = mutableListOf<Song>()
        when {
            cursor == null -> {
                Log.d("MusicRepository", "Cursor error")
            }
            !cursor.moveToFirst() -> {
                Log.d("MusicRepository", "No media found on device")
            }
            else -> {
                do {
                    val song = getSongFromCursorImpl(cursor)
                    Log.d("Repository", song.albumArtUri.toString())
                    songs.add(song)
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return songs
    }

    private fun getSongFromCursorImpl(cursor: Cursor): Song {
        val id = cursor.getLong(ID)
        val title = cursor.getString(TITLE)
        val trackNumber = cursor.getInt(TRACK)
        val year = cursor.getInt(YEAR)
        val duration = cursor.getInt(DURATION)
        val uri = cursor.getString(PATH)
        val albumName = cursor.getString(ALBUM)
        val albumId = cursor.getLong(ALBUM_ID)
        val artistId = cursor.getInt(ARTIST_ID)
        val artistName = cursor.getString(ARTIST)

        val albumArtUri = returnImageUriIfAvailable(cursor, albumId)

        return Song(id,
            title,
            trackNumber,
            year,
            duration,
            uri,
            albumName,
            artistId,
            artistName,
            albumArtUri)

    }

    private fun returnImageUriIfAvailable(cursor: Cursor, albumId: Long): Uri? {
        val metadata: Int = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val pathId: String = cursor.getString(metadata)
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(pathId)
        val cover = retriever.embeddedPicture

        return if (cover == null) {
            null
        } else {
            ContentUris.withAppendedId(albumArtUri, albumId)
        }
    }

    companion object {
        private const val TAG = "SER_MusicRepository"
        private const val ID = 0
        private const val TITLE = 1
        private const val TRACK = 2
        private const val YEAR = 3
        private const val DURATION = 4
        private const val PATH = 5
        private const val ALBUM = 6
        private const val ALBUM_ID = 7
        private const val ARTIST_ID = 8
        private const val ARTIST = 9
        private val albumArtUri: Uri = Uri.parse("content://media/external/audio/albumart")
    }

    private val baseProjection = arrayOf(
        MediaStore.Audio.AudioColumns._ID,//0
        MediaStore.Audio.AudioColumns.TITLE, // 1
        MediaStore.Audio.AudioColumns.TRACK, // 2
        MediaStore.Audio.AudioColumns.YEAR, // 3
        MediaStore.Audio.AudioColumns.DURATION, // 4
        MediaStore.Audio.AudioColumns.DATA, // 5
        MediaStore.Audio.AudioColumns.ALBUM, // 6
        MediaStore.Audio.AudioColumns.ALBUM_ID, // 7
        MediaStore.Audio.AudioColumns.ARTIST_ID, // 8
        MediaStore.Audio.AudioColumns.ARTIST)// 9

}