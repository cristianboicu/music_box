package com.cristianboicu.musicbox.service

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.cristianboicu.musicbox.R
import com.cristianboicu.musicbox.other.Constants


class MediaStyleNotificationHelper {

    companion object {
        fun from(
            context: Context,
            mediaSession: MediaSessionCompat,
            playbackState: MediaPlayerHolder.PlayerState,
        ): NotificationCompat.Builder {

            val controller = mediaSession.controller
//            val mediaMetadata = controller.metadata
//            val description = mediaMetadata.description

            val pauseResumeButton = if (playbackState == MediaPlayerHolder.PlayerState.STARTED) {
                R.drawable.ic_baseline_pause_circle_24
            } else {
                R.drawable.ic_baseline_play_circle_24
            }

            val skipNextAction =
                NotificationCompat.Action(R.drawable.ic_baseline_skip_next_24, "SkipNext",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT))

            val pauseResumeAction = NotificationCompat.Action(pauseResumeButton, "PauseResume",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                    PlaybackStateCompat.ACTION_PLAY))

            val skipPrevAction =
                NotificationCompat.Action(R.drawable.ic_baseline_skip_previous_24, "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))


            val builder: NotificationCompat.Builder = NotificationCompat.Builder(context,
                Constants.NOTIFICATION_CHANNEL_ID)
            builder
                .setSmallIcon(R.drawable.default_cover_art)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSession.sessionToken))
                .setContentIntent(controller.sessionActivity)
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                    PlaybackStateCompat.ACTION_STOP))
                .addAction(skipPrevAction)
                .addAction(pauseResumeAction)
                .addAction(skipNextAction)
                .setOnlyAlertOnce(true)

            return builder
        }
    }

}