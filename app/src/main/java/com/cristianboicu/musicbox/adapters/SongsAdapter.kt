package com.cristianboicu.musicbox.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cristianboicu.musicbox.data.Song
import com.cristianboicu.musicbox.databinding.ItemSongBinding

class SongsAdapter(private val songClickListener: SongPlayClickListener) :
    ListAdapter<Song, SongViewHolder>(SongsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position), songClickListener, position)
    }
}

class SongsDiffCallback : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }
}


class SongPlayClickListener(private val playClickListener: (song: Song, position: Int) -> Unit) {
    fun onPlayClickListener(song: Song, position: Int) = playClickListener(song, position)
}


class SongViewHolder private constructor(private val binding: ItemSongBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Song, playClickListener: SongPlayClickListener, position: Int) {
        binding.song = item
        binding.position = position
        binding.songPlayClickListener = playClickListener
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): SongViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemSongBinding.inflate(layoutInflater, parent, false)
            return SongViewHolder(binding)
        }
    }
}
