package com.cristianboicu.musicbox.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cristianboicu.musicbox.adapters.SongPlayClickListener
import com.cristianboicu.musicbox.adapters.SongsAdapter
import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.databinding.FragmentMainBinding
import com.cristianboicu.musicbox.other.EventObserver
import com.cristianboicu.musicbox.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainFragment() : Fragment() {
    companion object {
        private const val TAG = "MainFragment"
    }

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentMainBinding
    private lateinit var songsAdapter: SongsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        songsAdapter = SongsAdapter(SongPlayClickListener(::playSong))
        binding.rvPlaylist.adapter = songsAdapter
        binding.viewModel = viewModel

        setUpObservers()
        return binding.root
    }

    private fun playSong(song: Song, position: Int) {
        viewModel.playSong(song, position)
    }

    private fun setUpObservers() {
        viewModel.deviceSongs.observe(viewLifecycleOwner) {
            songsAdapter.submitList(it)
            Log.d(TAG, "Device songs: $it")
        }

        viewModel.currentSong.observe(viewLifecycleOwner) {
            binding.currentSong = it
            binding.executePendingBindings()
        }

        viewModel.openSongFragment.observe(viewLifecycleOwner, EventObserver {
            this.findNavController(
            ).navigate(MainFragmentDirections.openSongFragment())
        })
//
//        viewModel.playerState.observe(viewLifecycleOwner){
//            binding.executePendingBindings()
//        }
    }
}

