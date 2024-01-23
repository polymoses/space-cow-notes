package com.d11.space_cow_notes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class GPSNoteAdapter(var context: Context, var gpsnotes: List<GPSNote>): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private class ViewHolder {
        lateinit var title: TextView
        lateinit var message: TextView
        lateinit var GPSLocation: TextView
        lateinit var image: ImageView
    }
    override fun getCount(): Int {
        return gpsnotes.size
    }

    override fun getItem(position: Int): Any {
        return gpsnotes[position]
    }

    override fun getItemId(position: Int): Long {
        return gpsnotes[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.locationlist_item_view, parent, false)

            holder = ViewHolder()
            holder.title = view.findViewById(R.id.tvTitle)
            holder.message = view.findViewById(R.id.tvMessage)
            holder.image = view.findViewById(R.id.ivImage)
            holder.GPSLocation = view.findViewById(R.id.tvGPSLocation)

            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val tvTitle = holder.title
        val tvMessage = holder.message
        val ivImage = holder.image
        val tvGPSLocation = holder.GPSLocation
        val note = gpsnotes[position]

        if(note.image != null && note?.image!!.size > 0) {
            val bmap: Bitmap = BitmapFactory.decodeByteArray(note?.image, 0, note?.image!!.size)
            ivImage.setImageBitmap(bmap)
        }


        tvTitle.text = note.title
        tvMessage.text = note.message

        // compose gps lat long output
        var latStr = note.latitude.toString()
        var longStr = note.longitude.toString()
        if(latStr.length > 9) latStr = latStr.substring(0,9)
        if(longStr.length > 9) longStr = longStr.substring(0,9)
        tvGPSLocation.text = latStr + ", " +  longStr

        return view
    }
}