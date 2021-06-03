package com.example.songsfirebase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper


class DBHelper(context:Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VER) {
    companion object{
        private  val DATABASE_VER=1
        private val DATABASE_NAME="Song.db"
        private val TABLE_NAME="Song"
        private val COL_ID="Id"
        private val COL_TITLE="title"
        private val COL_URI="uri"
        private val COL_IMAGE="image"
        private val COL_AUTHOR="author"
        private val COL_LYRIC="lyric"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY:String=("CREATE TABLE $TABLE_NAME($COL_ID INTEGER PRIMARY KEY,$COL_TITLE VARCHAR(256),$COL_URI VARCHAR(5000),$COL_IMAGE VARCHAR(5000),$COL_AUTHOR VARCHAR(256),$COL_LYRIC VARCHAR(5000))")
        db?.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    fun addSong(song: Song){
        val db=this.writableDatabase
        db.delete(TABLE_NAME,"title ='${song.title}'",null)
        var values=ContentValues()
        values.put(COL_TITLE,song.title)
        values.put(COL_URI,song.uri)
        values.put(COL_IMAGE,song.image)
        values.put(COL_AUTHOR,song.author)
        values.put(COL_LYRIC,song.lyrics)
        db.insert(TABLE_NAME,null,values)
        db.close()

    }
    fun deleteData(song: Song) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME,"title ='${song.title}'",null)
    }
    fun viewEmployee():List<Song>{
        val empList:ArrayList<Song> = ArrayList<Song>()
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }


        if (cursor.moveToFirst()) {
            do {
                var title = cursor.getString(cursor.getColumnIndex(COL_TITLE))
                var uri = cursor.getString(cursor.getColumnIndex(COL_URI))
                var image = cursor.getString(cursor.getColumnIndex(COL_IMAGE))
                var author = cursor.getString(cursor.getColumnIndex(COL_AUTHOR))
                var lyric = cursor.getString(cursor.getColumnIndex(COL_LYRIC))

//                var userID = cursor.getString(cursor.getColumnIndex("Id")).toInt()
                val emp= Song(title,uri,image,author,lyric)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }
}