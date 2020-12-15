package com.example.musicplayer.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface JSONConvertable {
    fun toJSON(): String = Gson().toJson(this)
}

inline fun <reified T : JSONConvertable> String.toObject(): T = Gson().fromJson(this, T::class.java)

inline fun <reified T : JSONConvertable> String.toList(): List<T> {
    val groupListType = object : TypeToken<ArrayList<T>>() {}.type
    val list: ArrayList<T> = Gson().fromJson(this, groupListType)
    return list

}

inline fun <reified T : JSONConvertable> List<T>.toJSON() = Gson().toJson(this)