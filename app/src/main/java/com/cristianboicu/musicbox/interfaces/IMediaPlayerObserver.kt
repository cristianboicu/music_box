package com.cristianboicu.musicbox.interfaces

import com.cristianboicu.musicbox.data.Song

interface IMediaPlayerObserver {

    fun onCurrentSongChanged(song: Song)

    fun onCurrentSongProgressChanged(position: Int)

}