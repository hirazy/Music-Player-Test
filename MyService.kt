package com.example.musicplayer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.example.musicplayer.Constant.*
import com.example.musicplayer.model.Music
import com.example.musicplayer.utils.MessageEvent
import com.example.musicplayer.utils.MessageEventTime
import com.example.musicplayer.utils.MessageEvent_Index
import com.example.musicplayer.utils.MessageMusicEvent1
import com.l.kotlinexample.utils.getData
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

lateinit var mediaPlayer: MediaPlayer

class MyService : Service() {
    lateinit var list_music: ArrayList<Music>
    override fun onBind(intent: Intent): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    var index: Int = 0

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        eventBus.register(this)
        list_music = ArrayList()

//        mediaPlayer = MediaPlayer()
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//
//        try {
//            mediaPlayer.setDataSource(URL_MP3 + "1.mp3")
//            mediaPlayer.prepare()
//        } catch (a: Exception) {
//
//        }
//        mediaPlayer.start()

        Log.e("MyService", "onStart")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event.key) {
            SCROLL_SB -> {
                mediaPlayer.seekTo(event.message.toInt())
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent_Index) {
        when (event.key) {
            PREVIOUS -> {
                Previous(event)
            }
            NEXT -> {
                Next(event)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageMusicEvent1) {
        Log.e("event", event.toString())
        when (event.key) {
            INIT -> {
                Init(event)
            }
            PLAY_PAUSE -> {
                Play_pause(event)
            }
        }
    }

    fun Init(event: MessageMusicEvent1) {
        Log.e("Service", "Init")
        if (!getData(KEY_SAVED).equals("")) {
            index = getData(KEY_SAVED).toInt()
        }
        mediaPlayer = MediaPlayer()
        //list_music = event.music.list_music
        var x = index + 1
        Log.e("Init", "" + x)
        mediaPlayer.setDataSource(URL_MP3 + event.ind + ".mp3")
        try {
            mediaPlayer.prepare()
        } catch (a: java.lang.Exception) {

        }
        mediaPlayer.start()
        OnLoad()
    }

    fun Play_pause(event: MessageMusicEvent1) {
        Log.e("PLAY_PAUSE", "MessageMusicEvent1")
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }

    fun Next(event: MessageEvent_Index) {
        index = event.index
        if (index == list_music.size) {
            index = 0
        }
        var x = index + 1
        mediaPlayer.stop()
        Log.e("NEXT_SERVICE", "mediaPlayer" + x)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                mediaPlayer.setDataSource(URL_MP3 + x + ".mp3")
                try {
                    mediaPlayer.prepare()
                } catch (a: Exception) {
                }
                mediaPlayer.start()
                cancel()
            }
        }, 500, 1000)
    }

    fun Previous(event: MessageEvent_Index) {
        index = event.index
        if (index == -1) {
            index = list_music.size - 1
        }
        var x = index + 1
        mediaPlayer.setDataSource(URL_MP3 + x + ".mp3")
        try {
            mediaPlayer.prepare()
        } catch (a: Exception) {

        }
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus.unregister(this)
    }

    fun OnLoad() {

        Timer().schedule(object : TimerTask() {
            override fun run() {
                eventBus.post(MessageEventTime(TIME_SB, mediaPlayer.currentPosition, mediaPlayer.duration))
                mediaPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener {

                })
            }
        }, 1000, 1000)
    }

    fun init_mediaplayer(ct: Context, raw_id: Int) {
        mediaPlayer = MediaPlayer.create(ct, raw_id)
    }
}
