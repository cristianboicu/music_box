package com.cristianboicu.musicbox.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cristianboicu.musicbox.databinding.FragmentSongBinding
import com.cristianboicu.musicbox.other.Util.convertMsToMinutes
import com.cristianboicu.musicbox.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongFragment : Fragment() {

    private lateinit var binding: FragmentSongBinding
    private val viewModel: MainViewModel by activityViewModels()

    private var shouldUpdateProgress = true
    private lateinit var seekBar: SeekBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSongBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.currentSong = viewModel.currentSong.value

        setUpObservers()

        seekBar = binding.seekBarSong
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    binding.tvSongProgress.text = convertMsToMinutes(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateProgress = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    viewModel.updatePlaybackPosition(it.progress)
                    shouldUpdateProgress = true
                }
            }

        })


        return binding.root
    }

    private fun setUpObservers() {
        viewModel.currentSong.observe(viewLifecycleOwner) {
            binding.currentSong = it
            binding.executePendingBindings()
            seekBar.max = it.duration
            binding.tvSongDuration.text = convertMsToMinutes(it.duration)
        }

        viewModel.playbackProgress.observe(viewLifecycleOwner) {
            if (shouldUpdateProgress) {
                seekBar.progress = it
                binding.tvSongProgress.text = convertMsToMinutes(it)
            }
        }
    }

}