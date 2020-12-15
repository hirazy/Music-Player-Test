package com.example.musicplayer.ui

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.musicplayer.Constant.URL_MP3
import com.example.musicplayer.R
import java.util.*

class Main2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_test)
        var mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        try {
            mediaPlayer.setDataSource(URL_MP3 + "1.mp3")
            mediaPlayer.prepare()
        } catch (a: Exception) {

        }
        mediaPlayer.start()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                Log.e("XXXX",mediaPlayer.currentPosition.toString())
            }
        }, 1000, 1000)
    }
}