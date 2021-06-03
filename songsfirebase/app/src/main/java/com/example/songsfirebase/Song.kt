package com.example.songsfirebase


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(val title:String,
                val uri:String,
                val image:String,
                val author:String,
                val lyrics :String):Parcelable
