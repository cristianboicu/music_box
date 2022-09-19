package com.cristianboicu.musicbox

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cristianboicu.musicbox.service.MediaService
import com.cristianboicu.musicbox.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val MEDIA_PERMISSION = 1
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()

        viewModel.mBinder.observe(this) {
            Log.d("MainActivity", "opa")
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MEDIA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MEDIA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission refused", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startService()
    }

    override fun onStop() {
        super.onStop()
        unbindService(viewModel.serviceConnection)
    }

    private fun startService() {
        val serviceIntent = Intent(this, MediaService::class.java)
//        if (Build.VERSION.SDK_INT >= 26){
//            startForegroundService(serviceIntent)
//        } else{
        startService(serviceIntent)
//        }
        bindService()
    }

    private fun bindService() {
        val serviceBindIntent = Intent(this, MediaService::class.java)
        bindService(serviceBindIntent, viewModel.serviceConnection, Context.BIND_AUTO_CREATE)
    }

}