package com.cristianboicu.musicbox.service

import android.content.ContentUris
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.interfaces.IMediaPlayerObserver
import com.cristianboicu.musicbox.other.Constants.MEDIA_SERVICE

class MediaPlayerHolder(private val mediaService: MediaService) : IMediaPlayerHolder,
    MediaPlayer.OnPreparedListener {

    private val TAG: String = "MediaPlayerHolder"

    private final val mediaPlayer: MediaPlayer = MediaPlayer()
    private var mediaPlayerState = PlayerState.IDLE
    private var mediaPlayerListeners = mutableListOf<Pair<String, IMediaPlayerObserver>>()

    private var deviceSongs = mutableListOf<Song>()
    private var currentSong: Song? = null

    private var currentSongPosition: Int = -1

    override fun initMediaPlayer() {
        if (mediaPlayerState == PlayerState.IDLE) {
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
    }

    private fun mySetDataSource() {
        val contentUri: Uri =
            ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currentSong!!.id)
        mediaPlayer.apply {
            reset()
            setDataSource(mediaService, contentUri)
            updatePlayerState(PlayerState.INITIALIZED)
            prepareAsync()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        updatePlayerState(PlayerState.PREPARED)
        mp?.start()
        updateProgress()
        updatePlayerState(PlayerState.STARTED)
    }

    override fun setCurrentSong(song: Song) {
        currentSong = song
        currentSong?.let {
            notifyListenersCurrentSongChanged(it)
            mySetDataSource()
        }
    }

    override fun setCurrentSongPosition(position: Int) {
        currentSongPosition = position
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun setDeviceSongs(songs: MutableList<Song>) {
        deviceSongs = songs
    }

    private fun pause() {
        mediaPlayer.pause()
        updatePlayerState(PlayerState.PAUSED)
    }

    private fun resume() {
        mediaPlayer.start()
        updateProgress()
        updatePlayerState(PlayerState.STARTED)
    }

    override fun pauseOrResume() {
        if (mediaPlayerState == PlayerState.STARTED && mediaPlayer.isPlaying) {
            pause()
        } else if (mediaPlayerState == PlayerState.PAUSED && !mediaPlayer.isPlaying) {
            resume()
        }
    }

    private fun updateCurrentSong() {
        currentSong = deviceSongs[currentSongPosition]
        currentSong?.let {
            notifyListenersCurrentSongChanged(it)
        }
    }

    override fun skipNext() {
        if (currentSongPosition != -1 && currentSongPosition + 1 < deviceSongs.size) {
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
            notifyListenersProgressChanged(mediaPlayer.currentPosition)
        }
    }

    override fun registerObserver(id: String, listener: IMediaPlayerObserver) {
        mediaPlayerListeners.add(Pair(id, listener))
    }

    override fun removeObserver(id: String) {
        mediaPlayerListeners.removeIf {
            it.first == id
        }
    }

    override fun getCurrentSongProgress(): Int {
        return mediaPlayer.currentPosition
    }

    override fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    private fun notifyListenersCurrentSongChanged(song: Song) {
        mediaPlayerListeners.forEach {
            it.second.onCurrentSongChanged(song)
        }
    }

    private fun notifyListenersProgressChanged(progress: Int) {
        mediaPlayerListeners.forEach {
            it.second.onCurrentSongProgressChanged(progress)
        }
    }

    private fun notifyListenersPlayerStateChanged(state: PlayerState) {
        mediaPlayerListeners.forEach {
            it.second.onPlayerStateChanged(state)
        }
    }

    private fun notifyListenersProgressChangedExceptService(progress: Int) {
        mediaPlayerListeners.forEach {
            if (it.first != MEDIA_SERVICE) {
                it.second.onCurrentSongProgressChanged(progress)
            }
        }
    }

    override fun setPlaybackProgress(progress: Int) {
        mediaPlayer.seekTo(progress)
        notifyListenersProgressChanged(progress)
    }

    private fun updateProgress() {
        Thread {
            while (mediaPlayer.isPlaying)
                try {
                    notifyListenersProgressChangedExceptService(mediaPlayer.currentPosition)
                    Thread.sleep(500)
                } catch (e: Exception) {
                    mediaPlayer.seekTo(0)
                }
        }.start()
    }

    private fun updatePlayerState(state: PlayerState) {
        mediaPlayerState = state
        Log.d(TAG, mediaPlayerState.toString())
        if (state == PlayerState.PAUSED || state == PlayerState.STARTED) {
            notifyListenersPlayerStateChanged(state)
        }
    }

    enum class PlayerState {
        IDLE,
        INITIALIZED,
        PREPARED,
        STARTED,
        PAUSED,
        STOPPED
    }

}