package com.gaurav.musicapp

import android.Manifest
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),View.OnClickListener {
    private val REQ = 1
    lateinit var mp: MediaPlayer
    lateinit var musics: Musics
    var playingsong = 0
    var prevsong = 0
    var prevview: View? = null
    var nowview: View? = null
    lateinit var mHandler: Handler
    lateinit var nameArray: ArrayList<String>
    lateinit var artistArray: ArrayList<String>
    lateinit var idArray: ArrayList<Int>
    lateinit var uriArray: ArrayList<Uri>
    lateinit var durationArrayList: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>
    lateinit var texts: Texts
    lateinit var  builder:NotificationCompat.Builder

    private val mNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (mp != null && mp.isPlaying) {
mp.pause()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(mNoisyReceiver, filter)
        setContentView(R.layout.activity_main)
        texts = Texts(this)
        musics = Musics(this)
        button.setOnClickListener(this)
        button2.setOnClickListener(this)
        mp = MediaPlayer()
        mHandler = Handler()
        songslist.showDividers=LinearLayout.SHOW_DIVIDER_BEGINNING

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQ
            )
        } else {

            dothings()
        }



        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && mp.isPlaying) {
                    playedtime.text = texts.time(progress.toLong() / 1000)
                    remainingtime.text =
                        texts.time((mp.duration.toLong() - progress.toLong()) / 1000)
                    mp.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
         builder = NotificationCompat.Builder(this@MainActivity).apply {
        setOngoing(true)
            setPriority(NotificationCompat.PRIORITY_HIGH)
        }
        runOnUiThread(object : Runnable {
            override fun run() {
                notifyl()
                if (mp.isPlaying) {

                    val mCurrentPosition: Int = mp.currentPosition
                    playedtime.text = texts.time(mCurrentPosition.toLong() / 1000)
                    remainingtime.text =
                        texts.time((mp.duration - mCurrentPosition).toLong() / 1000)
               builder.setLargeIcon(musics.getsongalbumart(uriArray[playingsong]))
                   .setContentText(nameArray[playingsong])
                   .setContentTitle(nameArray[playingsong])
                   .setContentText(remainingtime.text.toString())
                   .setSmallIcon(R.drawable.logo1)

                    NotificationManagerCompat.from(this@MainActivity).apply {

                        builder
                            .setProgress(mp.duration, mCurrentPosition, false)
                        notify(4, builder.build())
                    }
                    seekBar.progress = mCurrentPosition

                }
                mHandler.postDelayed(this, 1000)
            }
        })
        mp.setOnCompletionListener {
            if (playingsong<uriArray.size-1) {
                playsong(playingsong + 1, this, it, songslist.getChildAt(playingsong + 1))
            }else{
                playsong(0,this,it,songslist.getChildAt(0))
            }

        }


    }

    private fun dothings() {
        nameArray = ArrayList()
        uriArray = ArrayList()
        idArray = ArrayList()
        artistArray= ArrayList()
        durationArrayList = ArrayList()
        getsongs()
        for (i in uriArray.indices) {
            val inflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = inflater.inflate(R.layout.row, null, false)
            val name = view.findViewById<TextView>(R.id.songnamerecycler)
            val vc= view.findViewById<CardView>(R.id.row)
            val details = view.findViewById<TextView>(R.id.details)
            val img=view.findViewById<ImageView>(R.id.itemImage)
            val  btn=view.findViewById<Button>(R.id.ok)
            btn.setOnClickListener {
                texts.showDetails(musics.getsongalbumart(uriArray[i]),artistArray[i],nameArray[i],durationArrayList[i])
            }
            img.setImageBitmap(musics.getsongalbumart(uriArray[i]))
            details.text = durationArrayList[i]
            name.text = texts.names(nameArray[i])
            view.setOnClickListener {
                playsong(i, this, mp, it)
            }
            view.id = idArray[i]
            songslist.addView(view)
        }

    }


    fun getsongs() {
        var contentResolver = contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            var songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            var author = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            var artist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            var id = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            var duration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)


            do {
                var secondst = songCursor.getLong(duration)

                if (secondst > 30000) {
                    var durationtime = texts.time(secondst / 1000)
                    nameArray.add(songCursor.getString(songTitle))
                    artistArray.add(songCursor.getString(artist))
                    uriArray.add(Uri.withAppendedPath(songUri, "" + songCursor.getLong(id)))
                    idArray.add(songCursor.getInt(id))
                    durationArrayList.add(durationtime)

                }
            } while (songCursor.moveToNext())

        }
        uriArray.reverse()
        nameArray.reverse()
        idArray.reverse()
        artistArray.reverse()
        durationArrayList.reverse()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            REQ -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        dothings()
                    }
                } else {
                    Toast.makeText(this, "No Permission Granted", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun playsong(position: Int, context: Context, mp: MediaPlayer, view: View) {
        prevsong = playingsong
        playingsong = position
        prevview = nowview
        if (prevview != null) {
            prevview!!.findViewById<TextView>(R.id.details).text = durationArrayList[prevsong]
        }
        nowview = view
        view.findViewById<TextView>(R.id.details).text = "Playing"
        mp.stop()
        imageView.setImageBitmap(musics.getsongalbumart(uriArray[position]))
        mp.reset()
        mp.setDataSource(this@MainActivity, uriArray[position])
        mp.prepare()
        mp.start()
        seekBar.max = mp.duration
        playingsongname.text = texts.playingsongname(nameArray[position])
    }

    override fun onClick(v: View?) {
       when(v) {
           button->{
mp.pause()
           }
           button2->mp.start()
       }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mNoisyReceiver)
        cancel()

        }

    override fun onStop() {
        super.onStop()
    }
        fun cancel(){
            builder.setOngoing(false)
            NotificationManagerCompat.from(this@MainActivity).apply {
                cancel(4)
        }

    }
fun notifyl(){
    if (!mp.isPlaying){
        builder.setOngoing(true)

    }else{
        builder.setOngoing(false)

    }


    }

}


