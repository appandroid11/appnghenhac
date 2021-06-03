package com.example.songsfirebase



import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log

import android.view.View
import android.widget.ImageView

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


import androidx.viewpager.widget.ViewPager


import com.example.songsfirebase.fragment.adapter.PageAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import org.jsoup.Jsoup

import kotlin.concurrent.thread

class MainActivity: AppCompatActivity(), RecyclerAdapter.OnItemClickListener {
   private var songList=ArrayList<Song>()
    private var songList1=ArrayList<Song>()
    private var songListsong=ArrayList<Song>()
    private var songListoff=ArrayList<Song>()
    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101

//    private fun setupPermissions() {
//        val permission = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//        )
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "Permission to record denied")
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                            this,
//                            Manifest.permission.RECORD_AUDIO
//                    )
//            ) {
//                val builder = AlertDialog.Builder(this)
//                builder.setMessage("Permission to access the microphone is required for this app to record audio.")
//                        .setTitle("Permission required")
//
//                builder.setPositiveButton(
//                        "OK"
//                ) { dialog, id ->
//                    Log.i(TAG, "Clicked")
//                    makeRequest()
//                }
//
//                val dialog = builder.create()
//                dialog.show()
//            } else {
//                makeRequest()
//            }
//        }
//    }
//
//    private fun makeRequest() {
//        ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.RECORD_AUDIO),
//                RECORD_REQUEST_CODE
//        )
//    }



    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            loadSong()
//        when (requestCode) {
//            RECORD_REQUEST_CODE -> {
//
//                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//
//                    Log.i(TAG, "Permission has been denied by user")
//                } else {
//                    Log.i(TAG, "Permission has been granted by user")
//                }
//            }
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)
        var img:ImageView=findViewById(R.id.nen)
        img.visibility=View.VISIBLE

        var applayout:AppBarLayout=findViewById(R.id.appbarlayout)
        applayout.visibility=View.INVISIBLE
//        setupPermissions()
        if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    111
            )

        } else {
            loadSong()

        }
        getListsong()


    }
   fun getListsong( ) {
        thread{

            val doc =
                    Jsoup.connect("https://chiasenhac.vn")
                            .get()
            var songGrid = doc.getElementsByClass("bxh")
            var songItems = songGrid[0].getElementsByTag("li")
            var dem=0;
            for(i in 0..8){
                if(dem<9) {

                    val authorlist = songItems[i].getElementsByClass("author")
                    var linkpage = songItems[i].getElementsByTag("a")[0].absUrl("href").toString()
                    var songName = songItems[i].getElementsByTag("a")[1].text()
                    var authorname: String = ""
                    for (nameauthor in authorlist) {
                        authorname += nameauthor.text()
                    }
                    val songImageUrl =songItems[i].getElementsByTag("img")[0].absUrl("src").toString()
                    val doc1 = Jsoup.connect(linkpage).get()
                    val linksong = doc1.getElementsByClass("download_status")
                    var loi:String=doc1.getElementById("fulllyric").toString()
                    loi=loi.replace("<div id=\"fulllyric\">","")
                    loi=loi.replace("</div>","")
                    loi=loi.replace("<br>","")
                    Log.d("loibaihat", loi.toString())

                    val link = linksong[0].getElementsByTag("a")
                    val linkdownload = link[1].absUrl("href").toString()
                    songList.add(Song(songName, linkdownload, songImageUrl, authorname,loi))

                 dem++
                }

            }
//             craw album yeu thich
             songGrid = doc.getElementsByClass("col-md-9")
             songItems = songGrid[0].getElementsByClass("card1")
            dem=0
            for(songItem in songItems){
                if(dem<=8){
                    val author = songItem.getElementsByTag("a")[2].text().toString()
                    var linkpage = songItem.getElementsByTag("a")[0].absUrl("href").toString()
                    var songName = songItem.getElementsByTag("a")[1].text().toString()
//image link
                    var doc1=Jsoup.connect(linkpage).get()
                    var linkimg = doc1.getElementsByClass("card-details")
                    var link=linkimg[0].getElementsByTag("img")
                    var songImageUrl=link[0].absUrl("src").toString()

                     val linksong = doc1.getElementsByClass("download_status")
                    var loi:String=doc1.getElementById("fulllyric").toString()
                    loi=loi.replace("<div id=\"fulllyric\">","")
                    loi=loi.replace("</div>","")
                    loi=loi.replace("<br>","")
                    Log.d("loibaihat", loi.toString())
                    link=linksong[0].getElementsByTag("a")
                    var linkdownload= link[1].absUrl("href").toString()

                    songList1.add(Song(songName,linkdownload,songImageUrl,author,loi))
                    dem++
                }

            }

            this.runOnUiThread{
                setUptabs()
            }
        }

    }
    fun setUptabs(){

        val db= FirebaseFirestore.getInstance()
        var dem=0
        val docRef = db.collection("songs")
        docRef.get()
                .addOnSuccessListener { result ->
                    for (document in result) {

                        val docRef1 = db.collection("songs").document(document.id)
                        docRef1.get()
                                .addOnSuccessListener { songdata ->
                                    if (songdata!= null) {

                                            songListsong.add(
                                                    Song(
                                                            songdata.getString("title").toString(),
                                                            songdata.getString(
                                                                    "songurl"
                                                            ).toString(),
                                                            songdata.getString("image").toString(),
                                                            songdata.getString("author").toString(),
                                                    "Không lời")
                                            )
                                    }
                                    val adapter=PageAdapter(supportFragmentManager)
                                    val fonticon = Typeface.createFromAsset(assets, "fontawesome-webfont.ttf")

                                    adapter.addFragment(HomeFragment(songList,songList1),"Home")
//                                    adapter.addFragment(Test(fonticon,songList,songListsong),"")
                                    adapter.addFragment(MusicListFragment(fonticon,songList,songListsong),"Online")
                                    adapter.addFragment(MusicOffFragment(fonticon,songListoff),"Offline")
                                    val viewpage:ViewPager=findViewById(R.id.viewPage)
                                    val tabs:TabLayout=findViewById(R.id.tabs)
                                    viewpage.adapter=adapter
                                    tabs.setupWithViewPager(viewpage)
                                    tabs.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_home_24)
                                    tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_library_music_24)
                                    tabs.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_library_music_24)
                                }

                    }



                }

        var img:ImageView=findViewById(R.id.nen)
        var applayout:AppBarLayout=findViewById(R.id.appbarlayout)
        img.visibility=View.INVISIBLE

        applayout.visibility=View.VISIBLE


    }

    fun loadSong(){
        var uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var selecttion= MediaStore.Audio.Media.IS_MUSIC +"!=0"
        var rs=contentResolver.query(uri,null,selecttion,null,null)
        if(rs!=null){
            while(rs!!.moveToNext()){
                var url=rs!!.getString(rs!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                var author=rs!!.getString(rs!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                var title=rs!!.getString(rs!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                songListoff.add(Song(title,url,"https://media.travelmag.vn/files/thuannguyen/2020/04/25/cach-chup-anh-dep-tai-da-lat-1-2306.jpeg",author,"Không lời"))

            }
        }


    }

    override fun onItemClick(position: Int) {

    }

}