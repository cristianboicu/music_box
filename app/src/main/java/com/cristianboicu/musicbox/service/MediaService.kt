package com.cristianboicu.musicbox.service

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cristianboicu.musicbox.MainActivity
import com.cristianboicu.musicbox.R
import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.interfaces.IMediaPlayerObserver
import com.cristianboicu.musicbox.other.Constants.FOREGROUND_SERVICE_ID
import com.cristianboicu.musicbox.other.Constants.NOTIFICATION_CHANNEL_ID
import com.cristianboicu.musicbox.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.cristianboicu.musicbox.other.Constants.NOTIFICATION_ID


class MediaService : Service() {
    companion object {
        private const val TAG = "MediaService"
        private const val ACTION_PAUSE_RESUME = "pauseResume"
        private const val ACTION_SKIP_NEXT = "skipNext"
        private const val ACTION_SKIP_PREV = "skipPrevious"
    }

    private lateinit var notification: Notification
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaPlayerObserver: IMediaPlayerObserver

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

    override fun onCreate() {
        super.onCreate()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mediaPlayerObserver = object : IMediaPlayerObserver {
            override fun onPlayerStateChanged(state: MediaPlayerHolder.PlayerState) {
//                playerState = state
//                when (state) {
//                    MediaPlayerHolder.PlayerState.STARTED -> {
//                        updateMediaSessionPlaybackState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0F)
//                    }
//                    MediaPlayerHolder.PlayerState.PAUSED -> {
//                        updateMediaSessionPlaybackState(PlaybackStateCompat.STATE_PAUSED, 0, 0F)
//                    }
//                    else -> {
//                    }
//                }
//                constructNotification()
//                notificationManager.notify(NOTIFICATION_ID, notification)
            }

            override fun onCurrentSongChanged(song: Song) {
                currentSong = song
                currentSong?.let {
                    updateMediaSessionMetaData(it)
                    updateMediaSessionPlaybackState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0F)

                    constructNotification()
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }
            }

            override fun onCurrentSongProgressChanged(position: Int) {
            }

        }
        mediaPlayerHolder.registerObserver("mediaService", mediaPlayerObserver)
        mediaSessionCompat = MediaSessionCompat(this, "My Music")
        showNotification()
        Log.d(TAG, "onCreate: called.");
    }


    private fun updateMediaSessionPlaybackState(
        state: Int,
        position: Int,
        playbackSpeed: Float,
    ) {
        val playbackStateCompat = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_SEEK_TO)
            .setState(state, position.toLong(), playbackSpeed)
            .build()
        mediaSessionCompat.setPlaybackState(playbackStateCompat)
    }

    private fun updateMediaSessionMetaData(song: Song) {
        val builder = MediaMetadataCompat.Builder()
        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artistName)
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
            song.albumArtUri.toString())
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration.toLong())
        mediaSessionCompat.setMetadata(builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: called.");
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_PAUSE_RESUME -> {
                    mediaPlayerHolder.pauseOrResume()
                }
                ACTION_SKIP_NEXT -> {
                    mediaPlayerHolder.skipNext()
                }
                ACTION_SKIP_PREV -> {
                    mediaPlayerHolder.skipPrevious()
                }
            }
        }

        Log.d(TAG, "onStartCommand: called. ${intent?.action}")
        return START_STICKY
    }

    private fun showNotification() {
        createNotificationChannel()

        constructNotification()

        startForeground(FOREGROUND_SERVICE_ID, notification)
    }

    private fun constructNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)

        val pauseResumeIntent = Intent(this, MediaService::class.java).apply {
            action = ACTION_PAUSE_RESUME
        }
        val pauseResumePendingIntent =
            PendingIntent.getService(this, 1, pauseResumeIntent, FLAG_IMMUTABLE)

        val skipNextIntent = Intent(this, MediaService::class.java).apply {
            action = ACTION_SKIP_NEXT
        }
        val skipNextPendingIntent =
            PendingIntent.getService(this, 1, skipNextIntent, FLAG_IMMUTABLE)

        val skipPrevIntent = Intent(this, MediaService::class.java).apply {
            action = ACTION_SKIP_PREV
        }
        val skipPrevPendingIntent =
            PendingIntent.getService(this, 1, skipPrevIntent, FLAG_IMMUTABLE)

        val pauseResumeButton = if (playerState == MediaPlayerHolder.PlayerState.STARTED) {
            R.drawable.ic_baseline_pause_circle_24
        } else {
            R.drawable.ic_baseline_play_circle_24
        }

        notification = NotificationCompat.Builder(baseContext, NOTIFICATION_CHANNEL_ID)
//            .setContentTitle(currentSong?.title)
//            .setContentText(currentSong?.artistName)
            .setSmallIcon(R.drawable.default_cover_art)
//            .setLargeIcon(Util.uriToBitmap(this, currentSong?.albumArtUri))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSessionCompat.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", skipPrevPendingIntent)
            .addAction(pauseResumeButton, "Play", pauseResumePendingIntent)
            .addAction(R.drawable.ic_baseline_skip_next_24, "Next", skipNextPendingIntent)
            .build()
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