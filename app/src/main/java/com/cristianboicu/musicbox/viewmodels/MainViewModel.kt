package com.cristianboicu.musicbox.viewmodels

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cristianboicu.musicbox.data.MusicRepository
import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.service.MediaService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
) : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private var _mediaService: MediaService? = null

    private val _mBinder: MutableLiveData<MediaService.MyBinder?> =
        MutableLiveData<MediaService.MyBinder?>()
    val mBinder = _mBinder

    private val _serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            Log.d(TAG, "ServiceConnection: connected to service.")
            val binder: MediaService.MyBinder = iBinder as MediaService.MyBinder
            _mBinder.postValue(binder)
            _mediaService = binder.service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "ServiceConnection: disconnected from service.")
            _mBinder.postValue(null)
        }
    }

    val serviceConnection = _serviceConnection

    private val _deviceSongs = MutableLiveData<List<Song>>()
    val deviceSongs = _deviceSongs

    private val _currentSong = MutableLiveData<Song>()
    val currentSong = _currentSong

    init {
        _deviceSongs.value = musicRepository.searchDeviceSongs()
        if (currentSong.value == null) {
            _currentSong.value = deviceSongs.value?.get(0)
        }
    }

    fun playSong(song: Song, position: Int) {
        _currentSong.postValue(song)
        _mediaService?.mediaPlayerHolder?.initMediaPlayer()
        _mediaService?.mediaPlayerHolder?.setCurrentSong(song)
        _mediaService?.mediaPlayerHolder?.mySetDataSource()
    }

    fun pauseOrResume() {
        _mediaService?.mediaPlayerHolder?.pauseOrResume()
    }

}