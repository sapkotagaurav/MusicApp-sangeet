package com.gaurav.musicapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row.view.*


class RecyclerAdapter(
    var nameList: ArrayList<String>,
    var detailsList: ArrayList<String>,
    var uriList: ArrayList<Uri>,
    var idList:ArrayList<Int>,
    var context: Context
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    var musics = Musics(context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.songnamerecycler
        var details: TextView = itemView.details
        var alart: ImageView = itemView.itemImage
        var lin=itemView.lin



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater: LayoutInflater = LayoutInflater.from(parent.context)
        var view = inflater.inflate(R.layout.row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var name = nameList[position]
        var details = detailsList[position]
        var image = musics.getsongalbumart(uriList[position])
        holder.details.text = details
        holder.name.text = name
        holder.alart.setImageBitmap(image)

    }

}


