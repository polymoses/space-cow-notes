package com.d11.space_cow_notes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class GPSNoteAdapter(var context: Context, var gpsnotes: List<GPSNote>): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private class ViewHolder {
        lateinit var title: TextView
        lateinit var message: TextView
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

            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val tvTitle = holder.title
        val tvMessage = holder.message
        val note = gpsnotes[position]

        tvTitle.text = note.title
        tvMessage.text = note.message

        return view
    }
}