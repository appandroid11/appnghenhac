package com.example.songsfirebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.songsfirebase.databinding.FragmentHomeBinding
import com.example.songsfirebase.fragment.adapter.SlideAdapter



class HomeFragment(var songList: ArrayList<Song>,var songList1: ArrayList<Song>) : Fragment(R.layout.fragment_home), RecyclerAdapter.OnItemClickListener ,RecyclerVIew2.OnItemClickListener{
    lateinit var binding:FragmentHomeBinding
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "Music"
    private val description = "Test notification"
    var imagelist= ArrayList<String>()
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
    var m:Int=0
    var isBound = false
    var mService: MusicService? =null
    var testplay=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }
    override  fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
                imagelist.add("https://intphcm.com/data/upload/poster-am-nhac-dien-tu.jpg")
                 imagelist.add("https://mtrend.vn/wp-content/uploads/2015/09/hinh-nen-am-nhac-cho-dien-thoai-e1443603648990.jpg")
        imagelist.add("https://mtrend.vn/wp-content/uploads/2015/09/hinh-nen-am-nhac-cho-dien-thoai-e1443603648990.jpg")
        val intent = Intent(context, MusicService::class.java)
        activity?.bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
                binding.viewPager2.adapter= SlideAdapter(imagelist)
                binding.viewPager2.orientation=ViewPager2.ORIENTATION_HORIZONTAL
                binding.indicator.setViewPager(binding.viewPager2)

        var recyclerViewAdapter = RecyclerAdapter(songList, this, R.layout.gird2)
                binding.bxh.layoutManager = GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false)
                binding.bxh.adapter = recyclerViewAdapter

        var recyclerViewAdapter1 = RecyclerVIew2(songList1, this, R.layout.grid)
                binding.album.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                binding.album.adapter = recyclerViewAdapter1

        val slide=Handler()
                binding.viewPager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
               slide.removeCallbacks(getImage)
                slide.postDelayed(getImage,3000)
            }
    })
         Thread{
             while (true){
                 try{
                     runOnUiThread {
                         if (mService != null) {
                             if (mService!!.check.equals("nexthome")) {
                                 if (m == songList.size - 1) {
                                     m = 0
                                 } else {
                                     m++
                                 }
                                 mService!!.check = ""
                                 Log.d("xzcxz", "zxcxzc")
                                 play(m)

                             }
                             if(mService!!.check.equals("playhome")){
                                 mService!!.media.start()
                                 mService!!.check = ""
                                 noti(m)
                             }
                             if(mService!!.check.equals("stophome")){
                                 mService!!.media.pause()
                                 mService!!.check = ""
                                 noti(m)
                             }
                         }
                     }
                Thread.sleep(1000)
                 }catch (e: InterruptedException){

                 }

             }
         }.start()

   binding.next.setOnClickListener {
       var int= Intent(requireContext(),test::class.java)
       startActivity(int)
   }
    }
    val getImage= Runnable {
        binding.viewPager2.currentItem=binding.viewPager2.currentItem+1
    }
    override fun onItemClick(position: Int) {
        m=position
play(position)
    }
    fun play(position: Int){
        if(mService!!.mediaoff!=null || mService!!.mediaoff.isPlaying)
        {
            mService!!.mediaoff.pause()
        }
        mService!!.media.pause()
        mService!!.media= MediaPlayer()
        mService!!.media= MediaPlayer.create(context, Uri.parse(songList[position].uri))
        mService!!.media.setOnPreparedListener(
                MediaPlayer.OnPreparedListener {
                    mService!!.media.start()
                }
        )

        mService!!.media.start()
        testplay=true
       noti(position)
    }
    fun noti(position: Int){
        notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(activity, MusicListFragment::class.java)
        val pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var contentView= RemoteViews(requireActivity().packageName, R.layout.notihome1)
        if(mService!!.media.isPlaying){
            contentView = RemoteViews(requireActivity().packageName, R.layout.notihome1)
            contentView.setTextViewText(R.id.titlenoti,songList[position].title)
            contentView.setOnClickPendingIntent(R.id.playBtnnotihome, Stop())
            contentView.setOnClickPendingIntent(R.id.nextBtnnotihome, Next())
        }
        else{
            contentView = RemoteViews(requireActivity().packageName, R.layout.notihome2)
            contentView.setTextViewText(R.id.titlenoti,songList[position].title)
            contentView.setOnClickPendingIntent(R.id.playBtnnotihome, Start())
            contentView.setOnClickPendingIntent(R.id.nextBtnnotihome, Next())
        }

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
    fun Stop():PendingIntent{
        val intentbtn=Intent("stophome")

        return PendingIntent.getBroadcast(context, id, intentbtn, 0)
    }
    fun Start():PendingIntent{
        val intentbtn=Intent("playhome")

        return PendingIntent.getBroadcast(context, id, intentbtn, 0)
    }
    fun Next():PendingIntent{
        val intentbtn=Intent("nexthome")

        return PendingIntent.getBroadcast(context, id, intentbtn, 0)
    }
    override fun onItemClick2(position: Int) {

    }
    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded)
            return
        activity?.runOnUiThread(action)
    }
}








