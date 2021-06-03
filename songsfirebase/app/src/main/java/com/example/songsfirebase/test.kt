package com.example.songsfirebase

import android.app.*
import android.content.*
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.gauravk.audiovisualizer.visualizer.BarVisualizer
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class test: AppCompatActivity(), MusicListAdapter.OnItemClickListener {
   lateinit var db:DBHelper

    private var layoutManager: RecyclerView.LayoutManager?=null
    var adapter: MusicListAdapter? =null
   lateinit var hidden:Button
   lateinit var barmusic:BarVisualizer
          lateinit var dura:TextView
          lateinit var title:TextView
    lateinit var playBtn:Button
     lateinit var nextBtn:Button
    lateinit var preBtn:Button
    lateinit var seekBar:SeekBar
    lateinit var progress:TextView
    lateinit var recycle:RecyclerView
    lateinit var loopBtn:Button
    lateinit var musiclayout:LinearLayout
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "com.example.vicky.notificationexample"
    private val description = "Test notification"

    var listmusic=ArrayList<Song>()

    var curnt:Int=0
    lateinit var layout: LinearLayout
    var play=false
    var m:Int=0
    var isBound = false
    var mService: MusicService? =null

    var testplay=false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listyeuthich)
        val fonticon = Typeface.createFromAsset(assets, "fontawesome-webfont.ttf")
        val intent = Intent(this, MusicService::class.java)
      bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
       hidden=findViewById(R.id.hidden)
       playBtn=findViewById(R.id.playBtn)
        nextBtn=findViewById(R.id.nextBtn)
        preBtn=findViewById(R.id.preBtn)
         seekBar=findViewById(R.id.seekBar)
     progress=findViewById(R.id.progress)
        loopBtn=findViewById(R.id.loopBtn)
        musiclayout=findViewById(R.id.musiclayout)
 barmusic=findViewById(R.id.barmusic)
        dura=findViewById(R.id.dura)
        title=findViewById(R.id.title)
       recycle=findViewById(R.id.recyclerView)
       hidden.typeface=fonticon

        playBtn.typeface=fonticon
       nextBtn.typeface=fonticon
       preBtn.typeface=fonticon

        db = DBHelper(this)

        val emp: List<Song> = db.viewEmployee()

        listmusic.addAll(emp)
      adapter=MusicListAdapter(fonticon,listmusic,this,R.layout.item_yeuthich)
        var layoutManager= LinearLayoutManager(this)
        recycle.layoutManager=layoutManager
        recycle.adapter=adapter


      seekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            mService!!.media.seekTo(progress)

                        }
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                }
        )

        var handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                var currentPosition = msg.what

                if(currentPosition!=null){
                    var min:Int = currentPosition.toInt() / 1000 / 60
                    var sec:Int = currentPosition.toInt() / 1000 % 60
                    if(sec<10){
                      progress.text=""+min+":0"+sec
                    }
                    else{
                    progress.text=""+min+":"+sec
                    }

                }

            }
        }

        var handlernext = object : Handler() {
            override fun handleMessage(msg: Message) {
                var current=msg.what
                if(current==0){
                  playBtn.text="\uF04B"
                }
                else if(current==-1){
                  playBtn.text="\uF04C"
                }
                else{
                    nextplay()
                }

            }
        }


        Thread {
            while (true) {
                while (testplay){
                    try {
                        runOnUiThread {
//                        gocquay += 10f
//                        var gocquay1 = gocquay - 10f
//                        val rotate = RotateAnimation(gocquay1, gocquay, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
//                        rotate.duration = 1000
//                        rotate.interpolator = LinearInterpolator()
//                        binding.anhnen1.startAnimation(rotate)
                            if(mService!=null){
                                if (mService!!.check.equals("next")){
                                    mService!!.check=""
                                    var msg = Message()
                                    msg.what=-2
                                    handlernext.sendMessage(msg)

                                }
                            }
                        }


                        if (mService?.dat() != null) {
                        seekBar.progress = mService!!.dat()
                            var msg = Message()
                            msg.what = mService!!.dat()
                            handler.sendMessage(msg)

                        }
                        if(mService!=null){

                            if(mService!!.media.isLooping==false){
                                if(mService!!.media.isPlaying){
                                    play=false
                                }
                                if(mService!!.media.isPlaying==false){
                                    var msg = Message()
                                    msg.what=0
                                    handlernext.sendMessage(msg)

                                    if(play==false){

                                        if(mService!!.media.duration-mService!!.media.currentPosition<1000 && mService!!.media.duration>0)
                                        {
                                            play=true
                                            var msg = Message()
                                            msg.what=1
                                            handlernext.sendMessage(msg)
                                            Log.d("zxcv","dsfds"+mService!!.media.duration+" "+mService!!.media.currentPosition)
                                        }

                                    }

                                }
                                else{
                                    var msg = Message()
                                    msg.what=-1
                                    handlernext.sendMessage(msg)

                                }
                            }


                        }
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                    }
                }
            }
        }.start()


      loopBtn.typeface=fonticon
        loopBtn.setOnClickListener {
            if( mService!!.media.isLooping==false){
                mService!!.media.isLooping=true
              loopBtn.setTextColor(Color.BLUE)
            }
            else{
                mService!!.media.isLooping=false
             loopBtn.setTextColor(Color.BLACK)
            }

        }


      hidden.setOnClickListener { view->
          musiclayout.visibility=View.INVISIBLE

        }







        playBtn.setOnClickListener {
            if(mService!!.mediaoff!=null || mService!!.mediaoff.isPlaying)
            {
                mService!!.mediaoff.pause()
            }
            if(mService!!.media.isPlaying==false){
                testplay=true
             playBtn.text="\uF04C"
                mService!!.media.start()
            }
            else{
                testplay=false
         playBtn.text="\uF04B"
                mService!!.media.pause()
            }

        }



     nextBtn.setOnClickListener {
            play=true
            nextplay()
        }
     preBtn.setOnClickListener {
            play=true
            preplay()
        }

    }





    private val myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            val binder = service as MusicService.MyLocalBinder
            mService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }



    fun nextplay(){
        play=true
        if(m==listmusic.size-1){
            m=0
        }
        else{
            m++
        }
        if(mService!!.mediaoff!=null || mService!!.mediaoff.isPlaying)
        {
            mService!!.mediaoff.pause()
        }
        musicPlay(m)

    }




    fun preplay(){
        if(m==0){
            m=listmusic.size-1
        }
        else{
            m--
        }
        if(mService!!.mediaoff!=null || mService!!.mediaoff.isPlaying)
        {
            mService!!.mediaoff.pause()
        }
        musicPlay(m)

    }

    override fun onItemClick(position: Int) {
//        Picasso.get().load(list[position].image).into(binding.anhnen1)
        play=true
        musiclayout.visibility=View.VISIBLE

        m=position
        musicPlay(position)
    }

    override fun playrecl(position: Int) {
        play=true
        m=position
        musicPlay(position)
    }
    fun musicPlay(position: Int){

        chanselect(position)
       seekBar.progress=0
     title.text=listmusic[position].title
       playBtn.text="\uF04B"
        if(mService!!.mediaoff!=null || mService!!.mediaoff.isPlaying)
        {
            mService!!.mediaoff.pause()
        }
        mService!!.media.pause()
        mService!!.media= MediaPlayer()
        mService!!.media= MediaPlayer.create(this, Uri.parse(listmusic[position].uri))
        mService!!.media.setOnPreparedListener(
                MediaPlayer.OnPreparedListener {
                    mService!!.media.start()
                }
        )
       barmusic.setAudioSessionId(mService!!.media.audioSessionId)

      progress.text="0:00"
    seekBar.max= mService!!.media.duration
        var min =  mService!!.media.duration / 1000 / 60
        var sec =  mService!!.media.duration / 1000 % 60
        dura.text=""+ min + ":"+sec
        mService!!.media.start()
        testplay=true
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MusicListFragment::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val contentView = RemoteViews(packageName, R.layout.notification)
        contentView.setTextViewText(R.id.titlenoti,listmusic[position].title)
        contentView.setOnClickPendingIntent(R.id.playBtnnoti, Start())
        contentView.setOnClickPendingIntent(R.id.preBtnnoti, Stop())
        contentView.setOnClickPendingIntent(R.id.nextBtnnoti, Next())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = Notification.Builder(this@test, channelId)
                    .setContent(contentView)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)

                    .setContentIntent(pendingIntent)

        }else{
            builder = Notification.Builder(this@test)
                    .setContent(contentView)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)

                    .setContentIntent(pendingIntent)

        }
        notificationManager.notify(1234, builder.build())
    }



    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded)
            return
        activity?.runOnUiThread(action)
    }

    fun Stop(): PendingIntent {
        val intentbtn= Intent("stop")
        intentbtn.putExtra("play", "stop")
        return PendingIntent.getBroadcast(this@test, 0, intentbtn, 0)
    }
    fun Start(): PendingIntent {
        val intentbtn= Intent("play")
        intentbtn.putExtra("playsong", "stop")
        return PendingIntent.getBroadcast(this@test, 0, intentbtn, 0)
    }
    fun Next(): PendingIntent {
        val intentbtn= Intent("next")
        intentbtn.putExtra("playsong", "stop")
        return PendingIntent.getBroadcast(this@test, 0, intentbtn, 0)
    }
    fun chanselect(index:Int){
        adapter?.notifyItemChanged(adapter!!.getposition())
        Log.d("zxcv","zxcxz"+adapter!!.getposition()+" cvc"+index)
        curnt=index
        adapter?.selectposition(curnt)
        adapter?.notifyItemChanged(curnt)
    }
    override  fun addSong(position: Int){
        var db:DBHelper= DBHelper(this)
        val song=Song(listmusic[position].title,listmusic[position].uri,listmusic[position].image,listmusic[position].author,listmusic[position].lyrics)
        db.deleteData(song)
        val emp: List<Song> = db.viewEmployee()
        listmusic.clear()
        listmusic.addAll(emp)
        val fonticon = Typeface.createFromAsset(assets, "fontawesome-webfont.ttf")
       adapter=MusicListAdapter(fonticon,listmusic,this,R.layout.item_yeuthich)
        var layoutManager= LinearLayoutManager(this)
        recycle.layoutManager=layoutManager
        recycle.setAdapter(adapter)
    }
}


