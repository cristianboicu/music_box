package com.cristianboicu.musicbox.service

import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.interfaces.IMediaPlayerObserver

interface IMediaPlayerHolder {

    fun initMediaPlayer()

    fun setDeviceSongs(songs: MutableList<Song>)

    fun setCurrentSong(song: Song)

    fun setCurrentSongPosition(position: Int)

    fun setPlaybackProgress(progress: Int)

    fun isPlaying(): Boolean

    fun pauseOrResume()

    fun skipNext()

    fun skipPrevious()

    fun registerObserver(id: String, listener: IMediaPlayerObserver)

    fun removeObserver(id: String)

    fun getCurrentSongProgress(): Int

    fun seekTo(position: Int)
}
