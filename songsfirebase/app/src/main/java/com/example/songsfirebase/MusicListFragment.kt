package com.example.songsfirebase

import android.app.*
import android.content.*
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.*
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.songsfirebase.databinding.FragmentListmusicBinding
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList


class MusicListFragment(var fonticon: Typeface, var list: ArrayList<Song>, var list1: ArrayList<Song>) : Fragment(R.layout.fragment_listmusic), MusicListAdapter.OnItemClickListener  {

    private var layoutManager: RecyclerView.LayoutManager?=null
    var adapter: MusicListAdapter? =null

    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "com.example.vicky.notificationexample"
    private val description = "Test notification"

    var listmusic=ArrayList<Song>()
    var speech: String=""
    var curnt:Int=0
    lateinit var layout: LinearLayout
    var play=false
    var m:Int=0
    var isBound = false
    var mService: MusicService? =null
    lateinit var binding: FragmentListmusicBinding
    var testplay=false


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding= FragmentListmusicBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(context, MusicService::class.java)
        activity?.bindService(intent, myConnection, Context.BIND_AUTO_CREATE)

        binding.hidden.typeface=fonticon
        binding.speech.typeface=fonticon
        binding.playBtn.typeface=fonticon
        binding.nextBtn.typeface=fonticon
        binding.preBtn.typeface=fonticon
        binding.download.typeface=fonticon
        listmusic.clear()
        listmusic.addAll(list)
        listmusic.addAll(list1)
        recycler(listmusic)

        var gocquay=0f





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
                        binding.seekBar.progress = mService!!.dat()
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


     binding.loopBtn.typeface=fonticon
        binding.loopBtn.setOnClickListener {
            if( mService!!.media.isLooping==false){
                mService!!.media.isLooping=true
                binding.loopBtn.setTextColor(Color.BLUE)
            }
            else{
                mService!!.media.isLooping=false
                binding.loopBtn.setTextColor(Color.BLACK)
            }

        }



        binding.hidden.setOnClickListener { view->
            binding.musiclayout.visibility=View.INVISIBLE
            binding.speech.visibility=View.VISIBLE
        }

        binding.speech.setOnClickListener {
            askSpeechInput()
        }

        binding.search.setOnSearchClickListener {
               binding.jcplay.visibility=View.INVISIBLE
           }

        binding.search.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        binding.search.clearFocus()
                        listmusic.clear()
                        for (i in list) {
                            var name = i.title.toLowerCase()
                            var author = i.author.toLowerCase()
                            if (name.contains(query.toString().toLowerCase()) or author.contains(query.toString().toLowerCase())) {
                                listmusic.add(i)
                            }
                        }
                        for (i in list1) {
                            var name = i.title.toLowerCase()
                            var author = i.author.toLowerCase()
                            if (name.contains(query.toString().toLowerCase()) or author.contains(query.toString().toLowerCase())) {
                                listmusic.add(i)
                            }
                        }
                        recycler(listmusic)

                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }

                },
        )

    binding.download.setOnClickListener {
        var myDownload:Long=0
        var request = DownloadManager.Request(
                Uri.parse(listmusic[m].uri))
            .setTitle("download")
            .setDescription("Downloading...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverMetered(true)
    request.allowScanningByMediaScanner()

    var dm =requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    myDownload = dm.enqueue(request)
    var br = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id==myDownload){
                Toast.makeText(context, "DOWNLOAD", Toast.LENGTH_LONG).show()
            }
        }
    }
  requireActivity().registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
}

     binding.playBtn.setOnClickListener {
         if(mService!!.mediaoff!=null || mService!!.mediaoff.isPlaying)
         {
             mService!!.mediaoff.pause()
         }
            if(mService!!.media.isPlaying==false){
                 testplay=true
                 binding.playBtn.text="\uF04C"
                 mService!!.media.start()
        }
         else{
                testplay=false
                binding.playBtn.text="\uF04B"
                mService!!.media.pause()
         }

     }



        binding.nextBtn.setOnClickListener {
            play=true
            nextplay()
        }
        binding.preBtn.setOnClickListener {
            play=true
            preplay()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==300 && resultCode== Activity.RESULT_OK){
            val result:ArrayList<String>?=data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            speech=result?.get(0).toString().toLowerCase()
            listmusic.clear()
            for(i in list){
                var name=i.title.toLowerCase()
                var author=i.author.toLowerCase()
                if(name.contains(speech)||author.contains(speech)){
                    listmusic.add(i)
                }
            }
            for(i in list1){
                var name=i.title.toLowerCase()
                var author=i.author.toLowerCase()
                if(name.contains(speech)||author.contains(speech)){
                    listmusic.add(i)
                }
            }
            recycler(listmusic)
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

    fun recycler(listsong: ArrayList<Song>){
        adapter= MusicListAdapter(fonticon,listsong, this, R.layout.musicitem)
        layoutManager= LinearLayoutManager(activity)
        binding.recyclerView.layoutManager=layoutManager
        binding.recyclerView.setAdapter(adapter)
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
        Picasso.get().load(list[position].image).into(binding.anhnen1)
        play=true
        binding.musiclayout.visibility=View.VISIBLE
        binding.speech.visibility=View.INVISIBLE
        m=position
        musicPlay(position)
    }

    override fun playrecl(position: Int) {
        play=true
        m=position
      musicPlay(position)
    }
fun musicPlay(position: Int){
    binding.jcplay.visibility=View.VISIBLE
    chanselect(position)
    binding.seekBar.progress=0
    binding.title.text=listmusic[position].title
    binding.playBtn.text="\uF04B"
    if(mService!!.mediaoff!=null || mService!!.mediaoff.isPlaying)
    {
        mService!!.mediaoff.pause()
    }
    mService!!.media.pause()
    mService!!.media=MediaPlayer()
    mService!!.media=MediaPlayer.create(context, Uri.parse(listmusic[m].uri))
    mService!!.media.setOnPreparedListener(
            OnPreparedListener {
                mService!!.media.start()
            }
    )
    binding.barmusic.setAudioSessionId(mService!!.media.audioSessionId)

    binding.progress.text="0:00"
    binding.seekBar.max= mService!!.media.duration
    var min =  mService!!.media.duration / 1000 / 60
    var sec =  mService!!.media.duration / 1000 % 60
    binding.dura.text=""+ min + ":"+sec
    mService!!.media.start()
    testplay=true
    notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val intent = Intent(activity, MusicListFragment::class.java)
    val pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    val contentView = RemoteViews(requireActivity().packageName, R.layout.notification)
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
        val intentbtn=Intent("stop")
        intentbtn.putExtra("play", "stop")
        return PendingIntent.getBroadcast(context, id, intentbtn, 0)
}
    fun Start():PendingIntent{
        val intentbtn=Intent("play")
        intentbtn.putExtra("playsong", "stop")
        return PendingIntent.getBroadcast(context, id, intentbtn, 0)
    }
    fun Next():PendingIntent{
        val intentbtn=Intent("next")
        intentbtn.putExtra("playsong", "stop")
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
        val song=Song(listmusic[position].title,listmusic[position].uri,listmusic[position].image,listmusic[position].author,listmusic[position].lyrics)
        db.addSong(song)
    }
}




