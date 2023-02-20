package com.gaurav.musicapp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class Texts(c: Context) {
    var context: Context = c

    fun showText(y: String) {
        Toast.makeText(this.context, y, Toast.LENGTH_LONG).show()
    }

    fun time( seconds: Long): String {
        var minutesString: String = ""
        var secondsString: String = ""
        var minutes = seconds / 60
        if (minutes < 10) minutesString = "0$minutes" else minutesString = minutes.toString()
        var seconds = seconds % 60
        if (seconds < 10) secondsString = "0$seconds" else secondsString = seconds.toString()
        var finaltime = "$minutesString:$secondsString"
        return finaltime
    }
    fun names(name:String):String{
        var s1=""
        if (name.length>20){
             s1= name.slice(IntRange(0,20))
        }else{
            s1=name.slice(IntRange(0,name.length-1))
        }
        var dotdotdot="..."
        var s2=""
        if (name.length>40){
            s2 =   "\n"+name.slice(IntRange(20,40))

        }else if (name.length>=20){
            s2 =   "\n"+name.slice(IntRange(20,name.length-1))
            dotdotdot=""
        }else{
            s2=""
            dotdotdot="..."
        }
        return "$s1$s2$dotdotdot"
    }
    fun playingsongname(name: String):String{
        var s1=""
        s1 = if (name.length>30){
            name.slice(IntRange(0,30))+"..."

        }else{
            name.slice(IntRange(0,name.length-1))
        }
        return s1
    }

    fun  showDetails(bitmap: Bitmap?,artist:String,name: String,duration:String){
        var mArtist=artist
        val builder=AlertDialog.Builder(context)
        val layoutInflater=LayoutInflater.from(context)
        val view=layoutInflater.inflate(R.layout.dialog,null)
        var imageView=view.findViewById<ImageView>(R.id.imageView2)
        var nameview=view.findViewById<TextView>(R.id.namedetails)
        var artistView=view.findViewById<TextView>(R.id.artistDetails)
        var durationView=view.findViewById<TextView>(R.id.durationDetails)
nameview.text=name
        if (name.contains("soorya", true)){
            mArtist="Sooryagayathri"
        }
        artistView.text="artist:$mArtist"
        durationView.text=duration
        imageView.setImageBitmap(bitmap)
        builder.setView(view)
      .setPositiveButton("OK",
          DialogInterface.OnClickListener { dialog, id ->

dialog.dismiss()          })

        builder.show()
    }
}


