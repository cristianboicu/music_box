package com.cristianboicu.musicbox.service

import android.content.ContentUris
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.interfaces.IMediaPlayerObserver

class MediaPlayerHolder(private val mediaService: MediaService) : IMediaPlayerHolder,
    MediaPlayer.OnPreparedListener {

    private final val mediaPlayer: MediaPlayer = MediaPlayer()
    private lateinit var mediaPlayerObserver: IMediaPlayerObserver

    private var deviceSongs = mutableListOf<Song>()
    private var currentSong: Song? = null
    private var currentSongPosition: Int = -1

    override fun initMediaPlayer(mediaPlayerObserver: IMediaPlayerObserver) {
        this.mediaPlayerObserver = mediaPlayerObserver
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
        updateProgress()
    }

    override fun setCurrentSong(song: Song) {
        currentSong = song
        mySetDataSource()
    }

    override fun setCurrentSongPosition(position: Int) {
        currentSongPosition = position
        Log.d("TAG serve", currentSongPosition.toString())
    }


    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun setDeviceSongs(songs: MutableList<Song>) {
        deviceSongs = songs
    }

    private fun pause() {
        mediaPlayer.pause()
    }

    private fun resume() {
        mediaPlayer.start()
        updateProgress()
    }

    override fun pauseOrResume() {
        if (mediaPlayer.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    private fun updateCurrentSong() {
        currentSong = deviceSongs[currentSongPosition]
        currentSong?.let {
            mediaPlayerObserver.onCurrentSongChanged(it)
        }
    }

    override fun skipNext() {
        if (currentSongPosition != -1 && currentSongPosition < deviceSongs.size) {
            currentSongPosition++
            updateCurrentSong()
            mySetDataSource()
        }
    }

    override fun skipPrevious() {
        if (currentSongPosition != -1 && currentSongPosition > 0 && mediaPlayer.currentPosition < 5000) {
            currentSongPosition--
            updateCurrentSong()
            mySetDataSource()
        } else if (mediaPlayer.currentPosition >= 5000) {
            mediaPlayer.seekTo(0)
        }
    }

    override fun setPlaybackProgress(progress: Int) {
        mediaPlayer.seekTo(progress)
    }

    private fun updateProgress() {
        Thread {
            while (mediaPlayer.isPlaying)
                try {
                    Log.d("Service", mediaPlayer.currentPosition.toString())
                    mediaPlayerObserver.onCurrentSongProgressChanged(mediaPlayer.currentPosition)
                    Thread.sleep(200)
                } catch (e: Exception) {
                    mediaPlayer.seekTo(0)
                }
        }.start()
    }


}