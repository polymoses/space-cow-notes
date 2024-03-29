package com.d11.space_cow_notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OverviewActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var gpsnoteDao: GPSNoteDao? = null
    private var adapter: GPSNoteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locations_list)

        setSupportActionBar(findViewById(R.id.tbMain))

        // Initialize Room DB
        val db = Room.databaseBuilder(
            applicationContext,
            GPSNoteDatabase::class.java, "gpsnotes"
        ).allowMainThreadQueries().build()
        gpsnoteDao = db.noteDao()

        // Find view by Ids
        val lvNotes = findViewById<ListView>(R.id.lvNotes)
        val btAdd = findViewById<FloatingActionButton>(R.id.fab)

        adapter = GPSNoteAdapter(this, gpsnoteDao!!.getAll())
        lvNotes.adapter = adapter
        lvNotes.onItemClickListener = this


        btAdd.setOnClickListener{
            val intent = Intent(this, GPSNoteEditActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()

        // Update View
        adapter?.gpsnotes = gpsnoteDao!!.getAll()
        adapter?.notifyDataSetChanged()
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, id: Long) {
        val intent = Intent(this, GPSNoteEditActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }
}