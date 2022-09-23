package com.cristianboicu.musicbox.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cristianboicu.musicbox.databinding.FragmentSongBinding
import com.cristianboicu.musicbox.other.setPlayerState
import com.cristianboicu.musicbox.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongFragment : Fragment() {

    private lateinit var binding: FragmentSongBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSongBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.currentSong = viewModel.currentSong.value

        setUpObservers()


        return binding.root
    }

    private fun setUpObservers() {
        viewModel.currentSong.observe(viewLifecycleOwner) {
            binding.currentSong = it
            binding.executePendingBindings()
        }
    }

}