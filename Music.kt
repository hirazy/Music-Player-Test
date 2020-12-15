package com.example.musicplayer.model

import com.example.musicplayer.utils.JSONConvertable

class Music(var name: String, var raw_id: Int, var original: String, var rom: String, var english:String) {

}

data class List_Album(var data:ArrayList<Album>):JSONConvertable

data class Album(var id: String, var title: String, var album: String, var date: String, var total: String):JSONConvertable

data class lyrics(var id: String, var original: String, var rom: String, var english: String):JSONConvertable

data class list_lyrics(var data:ArrayList<lyrics>): JSONConvertable

data class path(var mp3_path: String)

data class M(var name: String, var path: String, var list_music: ArrayList<Music>)