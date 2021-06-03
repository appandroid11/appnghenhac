package com.example.songsfirebase


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat


open class NotificationReceiver: BroadcastReceiver() {



    override fun onReceive(context: Context?, intent: Intent?) {

        var intent1 = Intent(context, MusicService::class.java)
        intent1.putExtra("musicnoti", ""+ intent?.action)

        if (context != null) {
            ContextCompat.startForegroundService(context, intent1)
        }
//        if (test.equals("stop")) {
//
//             var intent1=Intent(context, MusicService::class.java)
//            intent1.putExtra("music","stop")
//            if (context != null) {
//                ContextCompat.startForegroundService(context, intent1)
//            }
//        }
//        if (test.equals("start")) {
//            Log.d("test","oke")

//            var intent1=Intent(context, MusicService::class.java)
//            intent1.putExtra("music","start")
//            if (context != null) {
//                ContextCompat.startForegroundService(context, intent1)
//            }
//        }
//        if (test != null) {
//            if (test.length>20) {
//                var intent1=Intent(context, MusicService::class.java)
//                intent1.putExtra("music",test)
//                if (context != null) {
//                    ContextCompat.startForegroundService(context, intent1)
//                }
//            }
//        }

    }
 }