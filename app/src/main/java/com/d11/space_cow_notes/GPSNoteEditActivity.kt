package com.d11.space_cow_notes

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.squareup.picasso.Picasso

class GPSNoteEditActivity : AppCompatActivity(), DialogInterface.OnClickListener, LocationListener {

    private var gpsnoteDao: GPSNoteDao? = null
    private var gpsnote: GPSNote? = null

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var latitude: Double = Double.NaN
    private var longitude: Double = Double.NaN
    private var imageString: String? = null
    private var gpsAlertDialog: AlertDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_edit)

        // Set toolbar
        setSupportActionBar(findViewById(R.id.tbEdit))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        // Find views by Id
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val ibImage = findViewById<ImageButton>(R.id.ibImage)

        // Initialize Room DB
        val db = Room.databaseBuilder(
            applicationContext,
            GPSNoteDatabase::class.java, "gpsnotes"
        ).allowMainThreadQueries().build()
        gpsnoteDao = db.noteDao()

        // Get gpsnote id from Intent
        val id = intent.getLongExtra("id", -1)
        if (id >= 0) {
            gpsnote = gpsnoteDao!!.loadAllByIds(id.toInt())[0]
            etTitle?.setText(gpsnote?.title)
            etMessage?.setText(gpsnote?.message)
            latitude = gpsnote?.latitude!!
            longitude = gpsnote?.longitude!!
            imageString = gpsnote?.image
            updateMap()
        }

        // Update current GPS GPSNote
        getLocation()

        // Set OnClickListener
        btnSave.setOnClickListener{
            val title = etTitle?.text.toString()
            val message = etMessage?.text.toString()

            if (gpsnote != null) {
                gpsnote!!.title = title
                gpsnote!!.message = message
                gpsnote!!.latitude = latitude
                gpsnote!!.longitude = longitude

                gpsnoteDao?.update(gpsnote!!)
            } else {
                gpsnoteDao!!.insertAll(GPSNote(title, message,latitude,longitude, ""))
            }

            // Show toast for user
            Toast.makeText(this, gpsnoteDao!!.getAll().toString(), Toast.LENGTH_LONG).show()

            finish()
        }

        ibImage.setOnClickListener{

            val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                   ibImage.setImageURI(uri)
                } else {

                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.delete -> showDeleteDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_message))
            .setPositiveButton(getString(R.string.yes), this)
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        gpsnote?.let {
            gpsnoteDao?.delete(it)

            // Display Toast
            Toast.makeText(this, R.string.delete_message, Toast.LENGTH_LONG).show()

            finish()
        }
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: android.location.Location) {
        if(latitude.isNaN() && longitude.isNaN()) {
            // falls noch keine gps daten gesetzt wurden ...
            latitude = location.latitude
            longitude = location.longitude
            updateMap()
        } else if(latitude != location.latitude && longitude != location.longitude) {
            // falls gps daten sich ändern
            gpsAlertDialog?.cancel() // close dialog if one is already open
            gpsAlertDialog = AlertDialog.Builder(this)
                .setTitle(R.string.gps_location_changed)
                .setMessage(R.string.gps_location_changed_question)
                .setPositiveButton(android.R.string.yes) { _, _ -> run {
                        latitude = location.latitude
                        longitude = location.longitude
                        updateMap()
                    }
                }
                .setNegativeButton(android.R.string.no) { _, _ -> run {
                        gpsAlertDialog = null
                    }
                }
                .show()
        }

    }

    private fun updateMap() {
        val url = "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=18&size=800x300&key=AIzaSyDqNUFsJhnTthP_WY0ISaCpp-56UuvOwPE&markers=size:tiny|" + latitude + "," + longitude + ""
        val iv = findViewById<ImageView>(R.id.ivMap)

        iv.setImageResource(R.drawable.loadinglocation)

        Picasso.get().load(url).into(iv);
    }

}