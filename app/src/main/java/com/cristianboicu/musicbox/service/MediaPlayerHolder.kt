package com.cristianboicu.musicbox.service

import android.content.ContentUris
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import com.cristianboicu.musicbox.data.Song

class MediaPlayerHolder(private val mediaService: MediaService) : IMediaPlayerHolder,
    MediaPlayer.OnPreparedListener {

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    private var deviceSongs = mutableListOf<Song>()
    private var currentSong: Song? = null
    private var currentSongPosition: Int? = null

    override fun initMediaPlayer() {
        Log.d("MainViewModel.TAG", "Init media player")
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setWakeMode(mediaService, PowerManager.PARTIAL_WAKE_LOCK)
        }
        mediaPlayer.setOnPreparedListener(this)
    }

    private fun mySetDataSource() {
        Log.d("MainViewModel.TAG", "set data source")
        val contentUri: Uri =
            ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currentSong!!.id)
        mediaPlayer.apply {
            reset()
            setDataSource(mediaService, contentUri)
            prepareAsync()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    override fun setCurrentSong(song: Song) {
        currentSong = song
        mySetDataSource()
    }

    override fun setCurrentSongPosition(position: Int) {
        currentSongPosition = position
    }

    override fun setDeviceSongs(songs: MutableList<Song>) {
        deviceSongs = songs
    }

    private fun pause() {
        mediaPlayer.pause()
    }

    private fun resume() {
        mediaPlayer.start()
    }

    override fun pauseOrResume() {
        if (mediaPlayer.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    override fun skipNext() {
    }

    override fun skipPrevious() {
    }


}