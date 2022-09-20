package com.cristianboicu.musicbox.service

import com.cristianboicu.musicbox.data.Song

interface IMediaPlayerHolder {

    fun initMediaPlayer()

    fun setDeviceSongs(songs: MutableList<Song>)

    fun setCurrentSong(song: Song)

    fun setCurrentSongPosition(position: Int)

    fun pauseOrResume()

    fun skipNext()

    fun skipPrevious()
}
