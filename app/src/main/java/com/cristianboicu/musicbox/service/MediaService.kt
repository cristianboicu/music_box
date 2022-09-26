package com.cristianboicu.musicbox.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat.from
import androidx.media.session.MediaButtonReceiver
import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.interfaces.IMediaPlayerObserver
import com.cristianboicu.musicbox.other.Constants.FOREGROUND_SERVICE_ID
import com.cristianboicu.musicbox.other.Constants.MEDIA_SERVICE
import com.cristianboicu.musicbox.other.Constants.NOTIFICATION_CHANNEL_ID
import com.cristianboicu.musicbox.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.cristianboicu.musicbox.other.Constants.NOTIFICATION_ID

class MediaService : Service() {
    companion object {
        private const val TAG = "MediaService"
    }

    private lateinit var notification: Notification
    private lateinit var mediaSessionCompat: MediaSessionCompat

    private var currentSong: Song? = null
    private var playerState: MediaPlayerHolder.PlayerState = MediaPlayerHolder.PlayerState.PAUSED

    val mediaPlayerHolder: IMediaPlayerHolder = MediaPlayerHolder(this)
    private val mBinder: IBinder = MyBinder()

    inner class MyBinder : Binder() {
        val service: MediaService
            get() = this@MediaService
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    private val mediaPlayerObserver: IMediaPlayerObserver = object : IMediaPlayerObserver {
        override fun onPlayerStateChanged(state: MediaPlayerHolder.PlayerState) {
            playerState = state
            when (state) {
                MediaPlayerHolder.PlayerState.STARTED -> {
                    setMediaSessionPlaybackState(PlaybackStateCompat.STATE_PLAYING,
                        mediaPlayerHolder.getCurrentSongProgress(),
                        1.0F)
                }
                MediaPlayerHolder.PlayerState.PAUSED -> {
                    setMediaSessionPlaybackState(PlaybackStateCompat.STATE_PAUSED,
                        mediaPlayerHolder.getCurrentSongProgress(),
                        0F)
                }
                else -> {
                }
            }
            buildNotification()
        }

        override fun onCurrentSongChanged(song: Song) {
            currentSong = song
            currentSong?.let {
                setMediaSessionMetaData(it)
                setMediaSessionPlaybackState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0F)
                buildNotification()
            }
        }

        override fun onCurrentSongProgressChanged(position: Int) {
            if (playerState == MediaPlayerHolder.PlayerState.PAUSED) {
                setMediaSessionPlaybackState(PlaybackStateCompat.STATE_PAUSED,
                    position,
                    0F)
                buildNotification()
            } else {
                setMediaSessionPlaybackState(PlaybackStateCompat.STATE_PLAYING,
                    position,
                    1.0F)
                buildNotification()
            }
        }

    }


    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            mediaPlayerHolder.setPlaybackProgress(pos.toInt())
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            mediaPlayerHolder.skipPrevious()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            mediaPlayerHolder.skipNext()
        }

        override fun onPause() {
            super.onPause()
            mediaPlayerHolder.pauseOrResume()
        }

        override fun onPlay() {
            super.onPlay()
            mediaPlayerHolder.pauseOrResume()
        }
    }

    override fun onCreate() {
        super.onCreate()
        initMediaPlayer()
        initMediaSession()
        startForegroundService()
    }

    private fun initMediaPlayer() {
        mediaPlayerHolder.initMediaPlayer()
        mediaPlayerHolder.registerObserver(MEDIA_SERVICE, mediaPlayerObserver)
    }

    private fun initMediaSession() {
        mediaSessionCompat = MediaSessionCompat(this, "My Music")
        mediaSessionCompat.setCallback(mediaSessionCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent)
        return START_STICKY
    }

    private fun buildNotification() {
        notification = MediaStyleNotificationHelper.from(this, mediaSessionCompat, playerState)
            .build()

        from(this@MediaService).notify(NOTIFICATION_ID, notification)
    }

    private fun setMediaSessionPlaybackState(
        state: Int,
        position: Int,
        playbackSpeed: Float,
    ) {
        val playbackStateCompat = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_SEEK_TO)
            .setState(state, position.toLong(), playbackSpeed)
            .build()
        mediaSessionCompat.setPlaybackState(playbackStateCompat)
    }

    private fun setMediaSessionMetaData(song: Song) {
        val builder = MediaMetadataCompat.Builder()
        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artistName)
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
            song.albumArtUri.toString())
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration.toLong())
        mediaSessionCompat.setMetadata(builder.build())
    }

    private fun startForegroundService() {
        createNotificationChannel()
        buildNotification()
        startForeground(FOREGROUND_SERVICE_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}