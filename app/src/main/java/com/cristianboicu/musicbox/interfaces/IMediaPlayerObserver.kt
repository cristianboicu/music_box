package com.cristianboicu.musicbox.interfaces

import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.service.MediaPlayerHolder

interface IMediaPlayerObserver {

    fun onPlayerStateChanged(state: MediaPlayerHolder.PlayerState)

    fun onCurrentSongChanged(song: Song)

    fun onCurrentSongProgressChanged(position: Int)

}