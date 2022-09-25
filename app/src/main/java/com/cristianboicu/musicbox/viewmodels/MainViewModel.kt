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
import com.cristianboicu.musicbox.interfaces.IMediaPlayerObserver
import com.cristianboicu.musicbox.other.Event
import com.cristianboicu.musicbox.service.IMediaPlayerHolder
import com.cristianboicu.musicbox.service.MediaPlayerHolder
import com.cristianboicu.musicbox.service.MediaService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
) : ViewModel(), IMediaPlayerObserver {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private var _mediaPlayerHolder: IMediaPlayerHolder? = null

    private val _mBinder: MutableLiveData<MediaService.MyBinder?> =
        MutableLiveData<MediaService.MyBinder?>()
    val mBinder = _mBinder

    private val _serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            Log.d(TAG, "ServiceConnection: connected to service.")
            val binder: MediaService.MyBinder = iBinder as MediaService.MyBinder
            _mBinder.postValue(binder)
            _mediaPlayerHolder = binder.service.mediaPlayerHolder

            _mediaPlayerHolder?.initMediaPlayer()
            _mediaPlayerHolder?.registerObserver("mainViewModel", this@MainViewModel)
            deviceSongs.value?.let {
                _mediaPlayerHolder?.setDeviceSongs(it as MutableList<Song>)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "ServiceConnection: disconnected from service.")
            _mBinder.postValue(null)
        }
    }

    private val _playerState = MutableLiveData(MediaPlayerHolder.PlayerState.PAUSED)
    val playerState = _playerState

    val serviceConnection = _serviceConnection

    private val _deviceSongs = MutableLiveData<List<Song>>()
    val deviceSongs = _deviceSongs

    private val _currentSong = MutableLiveData<Song>()
    val currentSong = _currentSong

    private val _openSongFragment = MutableLiveData<Event<Unit>>()
    val openSongFragment = _openSongFragment

    private val _playbackProgress = MutableLiveData(0)
    val playbackProgress = _playbackProgress

    init {
        _deviceSongs.value = musicRepository.searchDeviceSongs()
        if (currentSong.value == null) {
            _currentSong.value = deviceSongs.value?.get(0)
        }
    }

    fun playSong(song: Song, position: Int) {
        Log.d(TAG, position.toString())

        _currentSong.postValue(song)
        _mediaPlayerHolder?.setCurrentSong(song)
        _mediaPlayerHolder?.setCurrentSongPosition(position)
    }

    fun pauseOrResume() {
        _mediaPlayerHolder?.pauseOrResume()
    }

    fun next() {
        _mediaPlayerHolder?.skipNext()
    }

    fun previous() {
        _mediaPlayerHolder?.skipPrevious()
    }

    fun updatePlaybackPosition(position: Int) {
        _mediaPlayerHolder?.setPlaybackProgress(position)
    }

    fun openSongFragment() {
        _openSongFragment.value = Event(Unit)
    }

    override fun onPlayerStateChanged(state: MediaPlayerHolder.PlayerState) {
        _playerState.value = state
    }

    override fun onCurrentSongChanged(song: Song) {
        _currentSong.value = song
    }

    override fun onCurrentSongProgressChanged(position: Int) {
        _playbackProgress.postValue(position)
    }
}