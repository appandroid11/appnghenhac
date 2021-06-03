package com.example.songsfirebase

import android.R
import android.app.DownloadManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions


class MusicService: Service() {
 var media: MediaPlayer=MediaPlayer()
    var mediaoff: MediaPlayer=MediaPlayer()
    var check=""
    private val myBinder:IBinder = MyLocalBinder()
    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var song=intent?.getStringExtra("music")
        check= intent?.getStringExtra("musicnoti").toString()
        if(check.equals("stop")){
            media.pause()
        }
        else if(check.equals(("play"))){
            media.start()
        }
        if(song.equals("stopoff")){
            mediaoff.pause()
        }
        if(song.equals("playoff")){
            mediaoff.start()
        }
        if (song != null) {
            if(song.length>20){
               mediaoff= MediaPlayer()
                mediaoff.setDataSource(song)
                mediaoff.prepare()
                mediaoff.start()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
    fun dat():Int{
        return media.currentPosition
    }



    override fun startService(service: Intent?): ComponentName? {
        return super.startService(service)
    }

    inner class MyLocalBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }

    }
}
