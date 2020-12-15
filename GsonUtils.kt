package com.example.musicplayer.utils

import com.example.musicplayer.model.List_Album
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*




class GsonUtils<T> {
    fun arrayToString(list: ArrayList<T>?): String {
        val g = Gson()
        val listType =
            object : TypeToken<List<List_Album?>?>() {}.type
        return g.toJson(list, listType)
    }

    fun stringToArray(s: String?): ArrayList<T> {
        val g = Gson()
        val listType =
            object : TypeToken<ArrayList<List_Album?>?>() {}.type
        return g.fromJson(s, listType)
    }

    fun stringToObj(s: String?): T {
        val g = Gson()
        val listType = object : TypeToken<T>() {}.type
        return g.fromJson(s, listType)
    }
}