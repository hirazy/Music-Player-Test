package com.example.musicplayer.utils

import com.example.musicplayer.mediaPlayer

fun convert_time(duration: Int): String {
    var set_time: String = ""
    var all_time: Int = duration
    if (all_time >= 3600) {
        var hour: Int = all_time / 3600
        set_time.plus(hour).plus(":")
    }
    var minute: Int = (all_time % 3600) / 60
    if (minute == 0) {
        set_time.plus(minute).plus(":")
    } else {
        set_time.plus("00").plus(":")
    }
    var sec: Int = (all_time % 3600) % 60
    if (sec == 0) {
        set_time.plus(sec).plus(":")
    } else {
        set_time.plus("00").plus(":")
    }
    return set_time
}