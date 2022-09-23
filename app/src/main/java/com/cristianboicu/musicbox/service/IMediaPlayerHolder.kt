package com.cristianboicu.musicbox.service

import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.interfaces.IMediaPlayerObserver
import com.cristianboicu.musicbox.viewmodels.MainViewModel

interface IMediaPlayerHolder {

    fun initMediaPlayer(mediaPlayerObserver: IMediaPlayerObserver)

    fun setDeviceSongs(songs: MutableList<Song>)

    fun setCurrentSong(song: Song)

    fun setCurrentSongPosition(position: Int)

    fun isPlaying(): Boolean

    fun pauseOrResume()

    fun skipNext()

    fun skipPrevious()
}
