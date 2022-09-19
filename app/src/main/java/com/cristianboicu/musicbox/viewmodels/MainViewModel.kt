package com.cristianboicu.musicbox.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cristianboicu.musicbox.data.MusicRepository
import com.cristianboicu.musicbox.data.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
) : ViewModel() {

    private val _deviceSongs = MutableLiveData<List<Song>>()
    val deviceSongs = _deviceSongs

    init {
        _deviceSongs.postValue(musicRepository.searchDeviceSongs())
    }

}