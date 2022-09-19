package com.cristianboicu.musicbox.service

import android.content.ContentUris
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.viewmodels.MainViewModel

class MediaPlayerHolder(private val mediaService: MediaService): MediaPlayer.OnPreparedListener {

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    private val deviceSongs = mutableListOf<Song>()
    private var currentSong: Song? = null

    fun setCurrentSong(song: Song){
        currentSong = song
    }

    fun initMediaPlayer() {
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

    fun mySetDataSource() {
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

    private fun pause(){
            mediaPlayer.pause()
    }

    private fun resume(){
        mediaPlayer.start()
    }

    fun pauseOrResume(){
        if (mediaPlayer.isPlaying){
            pause()
        } else{
            resume()
        }
    }



}