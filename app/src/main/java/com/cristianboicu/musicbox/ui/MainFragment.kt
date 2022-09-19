package com.cristianboicu.musicbox.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.cristianboicu.musicbox.R
import com.cristianboicu.musicbox.adapters.SongPlayClickListener
import com.cristianboicu.musicbox.adapters.SongsAdapter
import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.databinding.FragmentMainBinding
import com.cristianboicu.musicbox.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment() : Fragment() {
    companion object{
        private const val TAG = "MainFragment"
    }
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding
    private lateinit var songsAdapter: SongsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(inflater)


        songsAdapter = SongsAdapter(SongPlayClickListener(::playSong))
        binding.rvPlaylist.adapter = songsAdapter

        setUpObservers()
        return binding.root
    }


    private fun playSong(song: Song, i: Int) {

    }

    private fun setUpObservers() {
        viewModel.deviceSongs.observe(viewLifecycleOwner){
            songsAdapter.submitList(it)
            Log.d(TAG, "Device songs: $it")
        }
    }
}

