package com.example.musicplayer.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.musicplayer.BuildConfig
import com.example.musicplayer.Constant.*
import com.example.musicplayer.R
import com.example.musicplayer.adpater.Album_Adapter
import com.example.musicplayer.eventBus
import com.example.musicplayer.mediaPlayer
import com.example.musicplayer.model.Album
import com.example.musicplayer.model.List_Album
import com.example.musicplayer.model.list_lyrics
import com.example.musicplayer.utils.*
import com.l.kotlinexample.utils.getData
import com.l.kotlinexample.utils.setData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ui_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

var list_album_all = ArrayList<Album>()

class UIMain : AppCompatActivity() {

    lateinit var typeface: Typeface

    var KEY_MAIN: Int = 1
    var index: Int = 0

    lateinit var adapter: Album_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_main)
        typeface =
            Typeface.createFromAsset(applicationContext?.assets, "fonts/font_fragment.ttf")
        tv_app.typeface = typeface
        sv_album.typeface = typeface
        tv_bts.typeface = typeface
        tv_name_saved.isSelected = true

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

        Log.e("XXXXX", "MessageMusicEvent1")

        rcclv_album.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    fab_upward.setVisibility(View.GONE)
                } else {
                    fab_upward.setVisibility(View.VISIBLE)
                    fab_upward.setClickable(true)
                }
            }
        })



        fab_upward.setOnClickListener(View.OnClickListener {
            rcclv_album.smoothScrollToPosition(0)
        })

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
                                        if (!getData(KEY_SAVED).equals("")) {
                                            index = getData(KEY_SAVED).toInt()
                                            Log.e("KEY_SAVED", index.toString())
                                            var id_mp3 = index + 1
                                            tv_name_saved.text = list_music.get(index).title
                                            if (mediaPlayer.isPlaying) {
                                                btn_main_play_pause.setImageResource(R.drawable.ic_baseline_pause_24)
                                                UpdateTimeSong()
                                            }
                                        } else {
                                            tv_name_saved.text = list_music.get(index).title
                                            mediaPlayer = MediaPlayer()
                                            var id_mp3 = index + 1
                                            mediaPlayer.setDataSource("http://grankwin.com/project/lyrics/audio/1/" + id_mp3 + ".mp3")
                                            try {
                                                mediaPlayer.prepare()
                                            } catch (a: java.lang.Exception) {

                                            }
                                        }
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
            list_music = l.data
            list_album_all = ArrayList(list_music)
            Log.e("list_album_all", list_album_all.toString())
            adapter = Album_Adapter(this, list_music) {
                var intent: Intent = Intent(this, MainActivity::class.java)
                intent.putExtra(KEY_INDEX, it)
                startActivityForResult(intent, KEY_MAIN)
            }
            if (!getData(KEY_SAVED).equals("")) {
                index = getData(KEY_SAVED).toInt()
                eventBus.post(MessageMusicEvent1(INIT, index))
                Log.e("KEY_SAVED", index.toString())
                tv_name_saved.text = list_music.get(index).title
                if (mediaPlayer.isPlaying) {
                    btn_main_play_pause.setImageResource(R.drawable.ic_baseline_pause_24)
                    UpdateTimeSong()
                }
            } else {
                tv_name_saved.text = list_music.get(index).title
                var id_mp3 = index + 1
                eventBus.post(MessageMusicEvent1(INIT, index))
            }
            rcclv_album.layoutManager = LinearLayoutManager(this)
            rcclv_album.adapter = adapter
        }


        //PREVIOUS
        btn_main_previous.setOnClickListener(View.OnClickListener {
            index--
            if (index == -1) {
                index = list_music.size - 1
            }

            var id_mp3 = index + 1
            eventBus.post(MessageEvent_Index(PREVIOUS, index))
            Log.e("mediaPlayer", mediaPlayer.toString())
            KhoiTaoMedia()
            convert_time1()
            btn_play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
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
                                            eventBus.post(
                                                MessageMusicEvent(
                                                    "NEXT",
                                                    lyric.data.get(0)
                                                )
                                            )
                                            setData(KEY_MP3 + id_lyrics, lyric.toJSON())
                                            eventBus.post(
                                                MessageMusicEvent(
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

        // PLAY_PAUSE
        btn_main_play_pause.setOnClickListener(View.OnClickListener {
            Log.e("btn_main_play_pause", "KEY_SAVED")
            if (mediaPlayer.isPlaying) {
                btn_main_play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            } else {
                btn_main_play_pause.setImageResource(R.drawable.ic_baseline_pause_24)
            }
            eventBus.post(MessageMusicEvent1(PLAY_PAUSE, 0))
            setData(KEY_SAVED, index.toString())
        })

        btn_main_next.setOnClickListener(View.OnClickListener {
            index = (index + 1) % list_music.size
            setData(KEY_SAVED, index.toString())
            //UpdateTimeSong()
            var id_mp3 = index + 1
            eventBus.post(MessageEvent_Index(NEXT, index))
            Log.e("mediaPlayer", mediaPlayer.toString())
            KhoiTaoMedia()
            convert_time1()
            btn_main_play_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
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
                                            tv_name_saved.text = list_music[index].title
                                            setData(KEY_MP3 + id_lyrics, lyric.toJSON())
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
            }

        })

        sv_album.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.e("o nTextChanged", list_album_all.toString())
                var text = p0.toString().toUpperCase()
                list_music.clear()
                var list = list_album_all.filter {
                    it.title.toUpperCase().contains(text)
                } as ArrayList<Album>
                list_music.addAll(list)
                adapter.notifyDataSetChanged()
            }

        })
        //list_music = getData(LIST).toObject<List_Album>().data
        ic_menu.setOnClickListener(View.OnClickListener {
            val popup = PopupMenu(this, ic_menu)
            //Inflating the Popup using xml file
            popup.menuInflater.inflate(R.menu.menu_main, popup.menu)
            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.it_rate) {

                } else {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                    var shareMessage = "\nLet me recommend you this application\n\n"
                    shareMessage =
                        """
                                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                                """.trimIndent()
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    startActivity(Intent.createChooser(shareIntent, "Choose One"))
                }
                true
            }

            popup.show()//
        })
    }

    fun UpdateTimeSong() {
//        val handler = Handler()
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                var simpledate = SimpleDateFormat("mm:ss")
//                pb_main.progress = mediaPlayer.currentPosition
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
//                    Log.e("mediaPlayer", mediaPlayer.toString())
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
//                                                    tv_name_saved.text = list_music[index].title
//                                                    setData(KEY_MP3 + id_lyrics, lyric.toJSON())
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
//                        }, 4000, 100)
//                    } else {
//                        var l = getData(KEY_MP3 + id_lyrics).toObject<list_lyrics>().data
//                    }
//                    btn_main_play_pause.setImageResource(R.drawable.ic_baseline_pause_24)
//                    mediaPlayer.start()
//                }
//                handler.postDelayed(this, 500)
//            }
//        }, 100)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEventTime) {
        var simpledate = SimpleDateFormat("mm:ss")
        //tv_time_playing.text = simpledate.format(event.time)
        Log.e("MessageEventTime", event.time_current.toString())
        pb_main.max = event.time_duration
        pb_main.progress = event.time_current
    }

    fun KhoiTaoMedia() {
//        pb_main.max = mediaPlayer.duration
//        Log.e("KhoiTaoMedia", "" + mediaPlayer.duration)
//        convert_time1()
//        tv_name_saved.text = list_music.get(
//            index
//        ).title
    }

    fun convert_time1() {
        try {
            var simpleDate = SimpleDateFormat("mm:ss")
            //tv_alltime.text = simpleDate.format(mediaPlayer.duration)
            //sb_time.max = mediaPlayer.duration
        } catch (a: Exception) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            index = data.getIntExtra(KEY_MAIN_POSITION, 0)
        }
    }
}