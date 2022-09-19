package com.cristianboicu.musicbox.data

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val trackNumber: Int,
    val year: Int,
    val duration: Int,
    val uri: String,
    val albumName: String,
    val artistId: Int,
    val artistName: String,
    val albumArtUri: Uri?,
)
