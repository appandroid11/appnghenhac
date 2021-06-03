package com.example.songsfirebase

import android.app.Activity
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore




import android.Manifest
import android.content.pm.PackageManager

import android.media.MediaPlayer

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.view.animation.Animation

import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.gauravk.audiovisualizer.visualizer.BarVisualizer


class ProfileActivity() : Activity() {
    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0
    var db = FirebaseFirestore.getInstance()
    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Permission to access the microphone is required for this app to record audio.")
                    .setTitle("Permission required")

                builder.setPositiveButton(
                    "OK"
                ) { dialog, id ->
                    Log.i(TAG, "Clicked")
                    makeRequest()
                }

                val dialog = builder.create()
                dialog.show()
            } else {
                makeRequest()
            }
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        setupPermissions()

        var btn: Button = findViewById(R.id.playBtn);
        var gocquay = 0f
        var volumeBar: SeekBar = findViewById(R.id.volumeBar)
        var positionBar: SeekBar = findViewById(R.id.positionBar)
        var elapsedTimeLabel: TextView = findViewById(R.id.elapsedTimeLabel)
        var remainingTimeLabel: TextView = findViewById(R.id.remainingTimeLabel)
        var anhnen: ImageView = findViewById(R.id.anhnen)
        var visualizer: BarVisualizer = findViewById(R.id.barmusic)


                mp= MediaPlayer.create(this, Uri.parse("https://firebasestorage.googleapis.com/v0/b/songsfirebase.appspot.com/o/Imagine%20Dragons%20-%20Bad%20Liar.mp3?alt=media&token=9b2103e2-b365-403f-9099-483439750ca3"))

                        mp.isLooping=true
                        mp.setVolume(0.5f, 0.5f)

                        totalTime = mp.duration
                        volumeBar.setOnSeekBarChangeListener(
                            object : SeekBar.OnSeekBarChangeListener {
                                override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                                    if (fromUser) {
                                        var volumeNum = progress / 100.0f
                                        mp.setVolume(volumeNum, volumeNum)
                                    }
                                }

                                override fun onStartTrackingTouch(p0: SeekBar?) {
                                }

                                override fun onStopTrackingTouch(p0: SeekBar?) {
                                }
                            }
                        )

                        positionBar.max = totalTime
                        positionBar.setOnSeekBarChangeListener(
                            object : SeekBar.OnSeekBarChangeListener {
                                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                    if (fromUser) {
                                        mp.seekTo(progress)
                                    }
                                }

                                override fun onStartTrackingTouch(p0: SeekBar?) {
                                }

                                override fun onStopTrackingTouch(p0: SeekBar?) {
                                }
                            }
                        )
                        fun createTimeLabel(time: Int): String {
                            var timeLabel = ""
                            var min = time / 1000 / 60
                            var sec = time / 1000 % 60

                            timeLabel = "$min:"
                            if (sec < 10) timeLabel += "0"
                            timeLabel += sec

                            return timeLabel
                        }
                        // Thread
                        var handler = object : Handler() {
                            override fun handleMessage(msg: Message) {
                                var currentPosition = msg.what

                                // Update positionBar
                                positionBar.progress = currentPosition

                                // Update Labels
                                var elapsedTime = createTimeLabel(currentPosition)

                                elapsedTimeLabel.text = elapsedTime

                                var remainingTime = createTimeLabel(totalTime - currentPosition)
                                remainingTimeLabel.text = "-$remainingTime"
                            }
                        }
                        btn.setOnClickListener(object : View.OnClickListener {
                            override fun onClick(v: View?) {
                                //code here.
                                if (mp.isPlaying) {
                                    // Stop
                                    mp?.pause()
                                    btn.setBackgroundResource(R.drawable.play)
                                } else {
                                    // Start


                                    mp!!.start()


                                    btn.setBackgroundResource(R.drawable.stop)

                                    var audiosessionid=mp.audioSessionId
                                    if(audiosessionid!=-1){



// Set your media player to the visualizer.
                                        visualizer.setAudioSessionId(audiosessionid)

                                    }
                                    Thread(Runnable {

                                        while ((mp != null)&&(mp.isPlaying)) {

                                            try {
                                                Log.d("1234","dfdsfdsa")
                                                var msg = Message()

                                                msg.what = mp.currentPosition
                                                handler.sendMessage(msg)
                                                gocquay+=10f
                                                var gocquay1=gocquay-10f
//                                                val rotate = RotateAnimation(gocquay1, gocquay, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
//                                                rotate.duration = 1000
//                                                rotate.interpolator = LinearInterpolator()
//                                                anhnen.startAnimation(rotate)

                                                Thread.sleep(5000)
                                            } catch (e: InterruptedException) {
                                            }
                                        }

                                    }).start()
                                }
                            }
                        }
                        )
                    }
                }








