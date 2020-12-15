package com.l.kotlinexample.utils

import android.content.Context
import com.example.musicplayer.R

fun Context.setData(key: String, data: String) {
    var preference = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
    val editor = preference.edit()
    editor.putString(key, data)
    editor.commit()

}

fun Context.getData(key: String): String =
    getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
        .getString(key, "").toString();
