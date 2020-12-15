package com.example.musicplayer.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.musicplayer.Constant.*
import com.example.musicplayer.R
import com.example.musicplayer.eventBus
import com.example.musicplayer.fragment.FragmentMusic
import com.example.musicplayer.mediaPlayer
import com.example.musicplayer.model.Album
import com.example.musicplayer.model.List_Album
import com.example.musicplayer.model.list_lyrics
import com.example.musicplayer.utils.*
import com.l.kotlinexample.utils.getData
import com.l.kotlinexample.utils.setData
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*

lateinit var list_music: ArrayList<Album>
var index: Int = 0

class MainActivity : AppCompatActivity() {
    lateinit var myPagerAdapter: MyPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Receive()
        eventBus.register(this)


        ic_back_main.setOnClickListener(View.OnClickListener {
            var intent: Intent = Intent(this, UIMain::class.java)
            intent.putExtra(KEY_MAIN_POSITION, index)
            finish()
        })
        tv_song.isSelected = true
        //init list_music
//        var mediaPlayer = MediaPlayer()
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
//        sb_time.max = mediaPlayer.duration
//        Timer().schedule(object : TimerTask() {
//            override fun run() {
//                Log.e("XXXX",mediaPlayer.currentPosition.toString())
//                sb_time.progress = mediaPlayer.currentPosition
//            }
//        }, 1000, 1000)

        if (getData(LIST).equals("")) {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    try {
                        val sharedPref = getSharedPreferences(KEY, Context.MODE_PRIVATE)
                        Log.e("shared", sharedPref.getString(KEY_ALBUM, "").toString())
                        AndroidNetworking.get(URL_ALBUM)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsObject(
                                List_Album::class.java,
                                object : ParsedRequestListener<List_Album> {
                                    override fun onResponse(album: List_Album) {
                                        val json: String = album.toString()
                                        Log.e("listCoppy", json)
                                        list_music = ArrayList()
                                        list_music = album.data
                                        var list = List_Album(list_music)
                                        Log.e(LIST, list_music.toString())
                                        setData(LIST, list.toJSON())
                                        var l = getData(LIST).toObject<List_Album>()
                                        //setData(LIST, list_music.toJSON())
                                        Log.e("AndroidNetworkingabc", l.data[0].toString())
                                    }

                                    override fun onError(anError: ANError) {

                                    }
                                })
                        cancel()
                    } catch (a: Exception) {

                    }
                }
            }, 100)

        } else {
            list_music = ArrayList()
            var l = getData(LIST).toObject<List_Album>()
            Log.e("list_music.l", l.data.toString())
            list_music = l.data
        }


        // init first value of list_music
        Timer().schedule(object : TimerTask() {
            override fun run() {
                try {
                    var x = index + 1
                    AndroidNetworking.get(URL_LYRICS + x)
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsObject(
                            list_lyrics::class.java,
                            object : ParsedRequestListener<list_lyrics> {
                                override fun onResponse(lyric: list_lyrics) {
                                    val json: String = lyric.toString()
                                    Log.e("listCoppy", json)
                                    val sharedPref = getSharedPreferences(KEY, Context.MODE_PRIVATE)
                                    val editor = sharedPref.edit()
                                    // init tv_song
                                    try {
                                        tv_song.text = list_music[index].title
                                    } catch (a: java.lang.Exception) {

                                    }
                                    eventBus.post(MessageMusicEvent("NEXT", lyric.data[0]))
                                    editor.apply()
                                    Log.e("AndroidNetworking", lyric.toString())
                                }

                                override fun onError(anError: ANError) {

                                }
                            })
                    KhoiTaoMedia()
                    cancel()
                } catch (a: Exception) {

                }
            }
        }, 2000, 300)

        val typeface =
            Typeface.createFromAsset(applicationContext?.assets, "fonts/font_fragment.ttf")
        tv_song.typeface = typeface
        myPagerAdapter = MyPageAdapter(
            supportFragmentManager
        )
        viewpager.adapter = myPagerAdapter
        viewpager.offscreenPageLimit = 3
        tab_layout.setupWithViewPager(viewpager)

        btn_previous.setOnClickListener(View.OnClickListener {
            index--
            if (index == -1) {
                index = list_music.size - 1
            }
            eventBus.post(MessageEvent_Index(PREVIOUS, index))

            Log.e("mediaPlayer", mediaPlayer.toString())
            KhoiTaoMedia()
            convert_time1()
            btn_play_pause.setImageResource(R.drawable.ic_play_main)
            Log.e("btn_previous", URL_LYRICS + (index + 1))
            var id_lyrics = index + 1
            if (getData(KEY_MP3 + id_lyrics).equals("")) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        try {
                            AndroidNetworking.get(URL_LYRICS + id_lyrics)
                                .setPriority(Priority.LOW)
                                .build()
                                .getAsObject(
                                    list_lyrics::class.java,
                                    object : ParsedRequestListener<list_lyrics> {
                                        override fun onResponse(lyric: list_lyrics) {
                                            val json: String = lyric.toString()
                                            Log.e("listCoppy", json)
                                            tv_song.text = list_music[index].title
//                                            eventBus.post(
//                                                MessageMusicEvent(
//                                                    "NEXT",
//                                                    lyric.data.get(0)
//                                                )
//                                            )
                                            setData(KEY_MP3 + id_lyrics, lyric.toJSON())
                                            eventBus.post(
                                                MessageEventLyric(
                                                    "",
                                                    lyric.data.get(0)
                                                )
                                            )
                                            Log.e("AndroidNetworking", lyric.toString())
                                        }

                                        override fun onError(anError: ANError) {

                                        }
                                    })
                            cancel()
                        } catch (a: Exception) {

                        }
                    }
                }, 4000, 100)
            } else {
                var l = getData(KEY_MP3 + id_lyrics).toObject<list_lyrics>().data
                eventBus.post(
                    MessageMusicEvent(
                        "",
                        l[0]
                    )
                )
            }
        })

        btn_play_pause.setOnClickListener(View.OnClickListener {
            eventBus.post(MessageMusicEvent1(PLAY_PAUSE, 0))
            if (mediaPlayer.isPlaying) {
                btn_play_pause.setImageResource(R.drawable.ic_play_main)
            } else {
                btn_play_pause.setImageResource(R.drawable.ic_pause_main)
                UpdateTimeSong()
            }
            setData(KEY_SAVED, index.toString())
        })

        btn_next.setOnClickListener(View.OnClickListener {
            index = (index + 1) % list_music.size
            setData(KEY_SAVED, index.toString())
            //UpdateTimeSong()
            //mediaPlayer.stop()
            //mediaPlayer = MediaPlayer()
            eventBus.post(MessageEvent_Index(NEXT, index))
            var id_mp3 = index + 1
            //mediaPlayer.setDataSource("http://grankwin.com/project/lyrics/audio/1/" + id_mp3 + ".mp3")

            Log.e("mediaPlayer", mediaPlayer.toString())
            KhoiTaoMedia()
            convert_time1()
            btn_play_pause.setImageResource(R.drawable.ic_play_main)

            //set data to fragment
            var id_lyrics = index + 1
            if (getData(KEY_MP3 + id_lyrics).equals("")) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        try {
                            AndroidNetworking.get(URL_LYRICS + id_lyrics)
                                .setPriority(Priority.LOW)
                                .build()
                                .getAsObject(
                                    list_lyrics::class.java,
                                    object : ParsedRequestListener<list_lyrics> {
                                        override fun onResponse(lyric: list_lyrics) {
                                            val json: String = lyric.toString()
                                            Log.e("listCoppy", json)
                                            tv_song.text = list_music[index].title
                                            eventBus.post(
                                                MessageEventLyric(
                                                    "NEXT",
                                                    lyric.data.get(0)
                                                )
                                            )
                                            setData(KEY_MP3 + id_lyrics, lyric.toJSON())
//                                            eventBus.post(
//                                                MessageMusicEvent(
//                                                    "",
//                                                    lyric.data.get(0)
//                                                )
//                                            )
                                            Log.e("AndroidNetworking", lyric.toString())
                                        }

                                        override fun onError(anError: ANError) {

                                        }
                                    })
                            cancel()
                        } catch (a: Exception) {

                        }
                    }
                }, 4000, 100)
            } else {
                var l = getData(KEY_MP3 + id_lyrics).toObject<list_lyrics>().data
                eventBus.post(
                    MessageEventLyric(
                        "",
                        l[0]
                    )
                )
            }

        })
        sb_time.setOnSeekBarChangeListener((object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.e("mediaPlayer", "onStopTrackingTouch" + p0!!.progress.toString())
                eventBus.post(MessageEvent("MOVE_TIME", p0!!.progress.toString()))
            }

        }))

    }

    fun KhoiTaoMedia() {
        //sb_time.max = mediaPlayer.duration
        //Log.e("KhoiTaoMedia", "" + mediaPlayer.duration)
        convert_time1()
        tv_song.text = list_music.get(
            index
        ).title
    }

    class MyPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var fragment_names: Array<String> = arrayOf("Original", "Rom", "English")
        override fun getItem(position: Int): Fragment {
            return FragmentMusic(position)
        }

        override fun getCount(): Int = 3

        override fun getPageTitle(position: Int): CharSequence? {
            return fragment_names[position]
        }
    }

    fun UpdateTimeSong() {
//        val handler = Handler()
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                var simpledate = SimpleDateFormat("mm:ss")
//                tv_time_playing.text = simpledate.format(mediaPlayer.currentPosition)
//                sb_time.progress = mediaPlayer.currentPosition
//                mediaPlayer.setOnCompletionListener {
//                    index = (index + 1) % list_music.size
//                    setData(KEY_SAVED, index.toString())
//                    if (mediaPlayer.isPlaying) {
//                        mediaPlayer.stop()
//                    }
//                    mediaPlayer = MediaPlayer()
//                    mediaPlayer.setDataSource("http://grankwin.com/project/lyrics/audio/1/" + (index + 1) + ".mp3")
//                    try {
//                        mediaPlayer.prepare()
//                    } catch (e: java.lang.Exception) {
//
//                    }
//                    KhoiTaoMedia()
//                    convert_time1()
//                    var id_lyrics = index + 1
//
//                    if (getData(KEY_MP3 + id_lyrics).equals("")) {
//                        Timer().schedule(object : TimerTask() {
//                            override fun run() {
//                                try {
//                                    AndroidNetworking.get(URL_LYRICS + id_lyrics)
//                                        .setPriority(Priority.LOW)
//                                        .build()
//                                        .getAsObject(
//                                            list_lyrics::class.java,
//                                            object : ParsedRequestListener<list_lyrics> {
//                                                override fun onResponse(lyric: list_lyrics) {
//                                                    val json: String = lyric.toString()
//                                                    Log.e("listCoppy", json)
//                                                    tv_song.text = list_music[index].title
//                                                    eventBus.post(
//                                                        MessageMusicEvent(
//                                                            "NEXT",
//                                                            lyric.data.get(0)
//                                                        )
//                                                    )
//                                                    setData(KEY_MP3 + id_lyrics, lyric.toJSON())
//                                                    eventBus.post(
//                                                        MessageMusicEvent(
//                                                            "",
//                                                            lyric.data.get(0)
//                                                        )
//                                                    )
//                                                    Log.e("AndroidNetworking", lyric.toString())
//                                                }
//
//                                                override fun onError(anError: ANError) {
//
//                                                }
//                                            })
//                                    cancel()
//                                } catch (a: Exception) {
//
//                                }
//                            }
//                        }, 1000, 100)
//                    } else {
//                        var l = getData(KEY_MP3 + id_lyrics).toObject<list_lyrics>().data
//                        eventBus.post(
//                            MessageMusicEvent(
//                                "",
//                                l[0]
//                            )
//                        )
//                    }
//                    btn_play_pause.setImageResource(R.drawable.ic_baseline_pause_main)
//                    mediaPlayer.start()
//                }
//                handler.postDelayed(this, 500)
//            }
//        }, 100)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEventTime) {
        var simpledate = SimpleDateFormat("mm:ss")
        tv_time_playing.text = simpledate.format(event.time_current)
        Log.e("MessageEventTime", event.time_current.toString())
        sb_time.progress = event.time_current
    }

    fun load_data() {
        AndroidNetworking.get(
            URL_LYRICS + list_music.get(
                index
            ).id
        )
            .setPriority(Priority.LOW)
            .build()
            .getAsObject(list_lyrics::class.java, object : ParsedRequestListener<list_lyrics> {
                override fun onResponse(lyric: list_lyrics) {
                    eventBus.post(MessageMusicEvent("", lyric.data[0]))
                }

                override fun onError(anError: ANError) {

                }
            })

    }

    fun Receive() {
        var intent: Intent = getIntent()
        index = intent.getIntExtra(KEY_INDEX, 0)
        eventBus.post(MessageMusicEvent1(INIT, index))
    }

    fun convert_time1() {
        try {
            var simpleDate = SimpleDateFormat("mm:ss")
            tv_alltime.text = simpleDate.format(mediaPlayer.duration)
            sb_time.max = mediaPlayer.duration
        } catch (a: Exception) {

        }
    }
}