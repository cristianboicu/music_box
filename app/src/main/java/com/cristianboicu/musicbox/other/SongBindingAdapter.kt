package com.cristianboicu.musicbox.other

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import com.cristianboicu.musicbox.R
import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.viewmodels.MainViewModel

@BindingAdapter("setSongTitle")
fun TextView.setSongTitle(song: Song?) {
    text = song?.title
}

@BindingAdapter("setSongArtist")
fun TextView.setSongArtist(song: Song?) {
    text = song?.artistName
}

@BindingAdapter("setSongAlbumCoverArt")
fun ImageView.setSongAlbumCoverArt(song: Song?) {
    if (song?.albumArtUri != null) {
        this.load(song.albumArtUri)
    } else {
        this.load(R.drawable.default_cover_art)
    }
}

@BindingAdapter("setPlayerState")
fun ImageView.setPlayerState(playerState: MainViewModel.PlayerState) {
    when (playerState) {
        MainViewModel.PlayerState.PAUSED -> {
            this.isSelected = false
        }
        MainViewModel.PlayerState.RESUMED -> {
            this.isSelected = true
        }
    }
}