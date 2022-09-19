package com.cristianboicu.musicbox.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MediaService: Service() {
    companion object{
        private const val TAG = "MediaService"
    }

    val mediaPlayerHolder = MediaPlayerHolder(this)

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
        Log.d(TAG, "onCreate: called.");
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: called.");
    }
}