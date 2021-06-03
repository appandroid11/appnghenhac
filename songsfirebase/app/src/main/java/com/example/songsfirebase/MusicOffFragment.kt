package com.example.songsfirebase

import android.app.*
import android.content.*
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.LinearLayout
import android.widget.RemoteViews

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



import kotlin.collections.ArrayList

import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import com.example.songsfirebase.databinding.FragmentListmusicBinding

import com.example.songsfirebase.databinding.FragmentMusicoffBinding
import com.squareup.picasso.Picasso
import java.util.*


class MusicOffFragment(var fonticon: Typeface, var list: ArrayList<Song>) : Fragment(R.layout.fragment_musicoff), MusicListAdapter.OnItemClickListener  {

    private var layoutManager: RecyclerView.LayoutManager?=null
    var adapter: MusicListAdapter? =null

    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = ""
    private val description = "Test notification"



    var curnt:Int=0
    lateinit var layout: LinearLayout
    var playoff=false
    var m:Int=0
    var isBound = false
    var mService: MusicService? =null
    lateinit var binding: FragmentMusicoffBinding
    var testplay=false


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMusicoffBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(context, MusicService::class.java)
        activity?.bindService(intent, myConnection, Context.BIND_AUTO_CREATE)


        binding.playBtn.typeface=fonticon
        binding.nextBtn.typeface=fonticon
        binding.preBtn.typeface=fonticon


        adapter= MusicListAdapter(fonticon,list, this, R.layout.musicitem)
        layoutManager= LinearLayoutManager(activity)
        binding.recyclerViewoff.layoutManager=layoutManager
        binding.recyclerViewoff.setAdapter(adapter)







        binding.seekBar.setOnSeekBarChangeListener(
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
                        binding.progress.text=""+min+":0"+sec
                    }
                    else{
                        binding.progress.text=""+min+":"+sec
                    }

                }

            }
        }

        var handlernext = object : Handler() {
            override fun handleMessage(msg: Message) {
                var current=msg.what
                if(current==0){
                    binding.playBtn.text="\uF04B"
                }
                else if(current==-1){
                    binding.playBtn.text="\uF04C"
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

                            if(mService!=null){
                                if (mService!!.check.equals("nextoff")){
                                    mService!!.check=""
                                    var msg = Message()
                                    msg.what=-2
                                    handlernext.sendMessage(msg)

                                }
                            }
                        }


                        if (mService?.mediaoff?.currentPosition != null) {
                            binding.seekBar.progress = mService!!.mediaoff.currentPosition
                            var msg = Message()
                            msg.what = mService!!.mediaoff.currentPosition
                            handler.sendMessage(msg)

                        }
                        if(mService!=null){

                            if(mService!!.mediaoff.isLooping==false){
                                if(mService!!.mediaoff.isPlaying){
                                    playoff=false
                                }
                                if(mService!!.mediaoff.isPlaying==false){
                                    var msg = Message()
                                    msg.what=0
                                    handlernext.sendMessage(msg)

                                    if(playoff==false){

                                        if(mService!!.mediaoff.duration-mService!!.mediaoff.currentPosition<1000 && mService!!.mediaoff.duration>0)
                                        {
                                            playoff=true
                                            var msg = Message()
                                            msg.what=1
                                            handlernext.sendMessage(msg)

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


        binding.loopBtn.typeface=fonticon
        binding.loopBtn.setOnClickListener {
            if( mService!!.mediaoff.isLooping==false){
                mService!!.mediaoff.isLooping=true
                binding.loopBtn.setTextColor(Color.BLUE)
            }
            else{
                mService!!.mediaoff.isLooping=false
                binding.loopBtn.setTextColor(Color.BLACK)
            }

        }


        binding.playBtn.setOnClickListener {
            if(mService!!.media!=null || mService!!.media.isPlaying)
            {
                mService!!.media.pause()
            }
            if(mService!!.mediaoff.isPlaying==false){
                testplay=true
                binding.playBtn.text="\uF04C"
                mService!!.mediaoff.start()
            }
            else{
                testplay=false
                binding.playBtn.text="\uF04B"
                mService!!.mediaoff.pause()
            }

        }



        binding.nextBtn.setOnClickListener {
            playoff=true
            nextplay()
        }
        binding.preBtn.setOnClickListener {
            playoff=true
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
        playoff=true
        if(m==list.size-1){
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
            m=list.size-1
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

        playoff=true

        m=position
        musicPlay(position)
    }

    override fun playrecl(position: Int) {
        playoff=true
        m=position
        musicPlay(position)
    }
    fun musicPlay(position: Int){
        chanselect(position)
        playoff=true
        binding.jcplay.visibility=View.VISIBLE
        binding.seekBar.progress=0
        binding.title.text=list[position].title
        binding.playBtn.text="\uF04B"
        m=position
        mService!!.mediaoff.pause()
        if(mService!!.media!=null || mService!!.media.isPlaying)
        {
            mService!!.media.pause()
        }
        mService!!.mediaoff=MediaPlayer()
        mService!!.mediaoff.setDataSource(list[m].uri)
        mService!!.mediaoff.prepare()
        mService!!.mediaoff.start()

        mService!!.mediaoff.isLooping=false
        binding.progress.text="0:00"
        binding.seekBar.max= mService!!.mediaoff.duration
        var min =  mService!!.mediaoff.duration / 1000 / 60
        var sec =  mService!!.mediaoff.duration / 1000 % 60
        binding.dura.text=""+ min + ":"+sec
        testplay=true
       noti(position)
    }
fun noti(position: Int){
    notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val intent = Intent(activity, MusicListFragment::class.java)
    val pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    val contentView = RemoteViews(requireActivity().packageName, R.layout.notification)

    contentView.setTextViewText(R.id.titlenoti,list[position].title)
    contentView.setOnClickPendingIntent(R.id.playBtnnoti, Start())
    contentView.setOnClickPendingIntent(R.id.preBtnnoti, Stop())
    contentView.setOnClickPendingIntent(R.id.nextBtnnoti, Next())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)
        builder = Notification.Builder(activity, channelId)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_foreground)

                .setContentIntent(pendingIntent)

    }else{
        builder = Notification.Builder(activity)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_foreground)

                .setContentIntent(pendingIntent)

    }
    notificationManager.notify(1234, builder.build())
}
    fun askSpeechInput(){
        if (!SpeechRecognizer.isRecognitionAvailable(activity)){
            Toast.makeText(activity, "speech", Toast.LENGTH_LONG).show()
        } else {
            val i= Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say som thing")
            startActivityForResult(i, 300)

        }
    }

    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded)
            return
        activity?.runOnUiThread(action)
    }

    fun Stop():PendingIntent{
        val intentbtn=Intent("stopoff")

        return PendingIntent.getBroadcast(context, id, intentbtn, 0)
    }
    fun Start():PendingIntent{
        val intentbtn=Intent("playoff")

        return PendingIntent.getBroadcast(context, id, intentbtn, 0)
    }
    fun Next():PendingIntent{
        val intentbtn=Intent("nextoff")

        return PendingIntent.getBroadcast(context, id, intentbtn, 0)
    }
    fun chanselect(index:Int){
        adapter?.notifyItemChanged(adapter!!.getposition())
        curnt=index
        adapter?.selectposition(curnt)
        adapter?.notifyItemChanged(curnt)
    }
    override  fun addSong(position: Int){
        var db:DBHelper= DBHelper(requireActivity())
        val song=Song(list[position].title,list[position].uri,list[position].image,list[position].author,list[position].lyrics)
        db.addSong(song)
    }
}



