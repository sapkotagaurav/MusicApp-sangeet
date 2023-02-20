package com.gaurav.musicapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri


class Musics(var context: Context) {
    var defaultArt = BitmapFactory.decodeResource(context.resources,R.drawable.logo1)
fun getsongalbumart(uri: Uri): Bitmap? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context,uri)
        val data = mmr.embeddedPicture
        return if (data != null) BitmapFactory.decodeByteArray(data, 0, data.size) else defaultArt
    }


}