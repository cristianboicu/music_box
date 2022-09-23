package com.cristianboicu.musicbox.other

object Util {
    fun convertMsToMinutes(ms: Int): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val remainedSeconds = (ms - (minutes * 60 * 1000)) / 1000

        return if (remainedSeconds < 10) {
            "${minutes}:0${remainedSeconds}"
        } else {
            "${minutes}:${remainedSeconds}"
        }
    }
}