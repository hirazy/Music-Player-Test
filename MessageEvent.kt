package com.example.musicplayer.utils

import com.example.musicplayer.model.lyrics

data class MessageEvent(var key: String, var message: String) {

}

data class MessageEvent_Index(var key: String, var index: Int){

}

data class MessageEventTime(var key: String, var time_current: Int, var time_duration: Int){

}

data class MessageEventLyric(var key:String, var lyric: lyrics){

}

data class MessageMusicEvent(var key: String, var lyric: lyrics) {

}

data class MessageMusicEvent1(var key: String, var ind: Int)   {

}