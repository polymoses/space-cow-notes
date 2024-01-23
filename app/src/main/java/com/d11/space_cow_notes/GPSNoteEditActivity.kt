package com.d11.space_cow_notes

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.room.Room
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.Base64


class GPSNoteEditActivity : AppCompatActivity(), DialogInterface.OnClickListener, LocationListener {

    private var gpsnoteDao: GPSNoteDao? = null
    private var gpsnote: GPSNote? = null

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var latitude: Double = Double.NaN
    private var longitude: Double = Double.NaN
    private var gpsAlertDialog: AlertDialog? = null



    @RequiresApi(Build.VERSION_CODES.O)
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
        val ivMap = findViewById<ImageView>(R.id.ivMap)
        val loadingContainer = findViewById<LinearLayout>(R.id.loadingContainer)

        // hide map image until loaded
        ivMap.visibility = View.GONE

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
            if(gpsnote?.image != null && gpsnote?.image!!.size > 0) {
                val bmap: Bitmap = BitmapFactory.decodeByteArray(gpsnote?.image, 0, gpsnote?.image!!.size)
                ibImage.setImageBitmap(bmap)
            }


            updateMap()
        }

        // initialize Media Library Picker
        val contract = ActivityResultContracts.PickVisualMedia()
        val pickMedia = registerForActivityResult(contract) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                ibImage.setImageURI(uri)
            } else {
                Toast.makeText(this, R.string.no_image_selected, Toast.LENGTH_LONG).show()
            }
        }

        // Update current GPS GPSNote
        getLocation()

        // Set OnClickListener
        btnSave.setOnClickListener{
            val title = etTitle?.text.toString()
            val message = etMessage?.text.toString()

            var bmapByteArray:ByteArray = ByteArray(0)
            if(ibImage.drawable != null) {
                val bmap: Bitmap = ibImage.drawable.toBitmap()
                val stream = ByteArrayOutputStream()
                bmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                bmapByteArray = stream.toByteArray()
            }

            if (gpsnote != null) {
                gpsnote!!.title = title
                gpsnote!!.message = message
                gpsnote!!.latitude = latitude
                gpsnote!!.longitude = longitude
                gpsnote!!.image = bmapByteArray
                gpsnoteDao?.update(gpsnote!!)
            } else {
                gpsnoteDao!!.insertAll(GPSNote(title, message,latitude,longitude, bmapByteArray))
            }

            // Show toast for user
            Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show()

            finish()
        }

        ibImage.setOnClickListener{
            pickMedia.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ImageOnly)
                    .build()
            )

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
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
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
        val loader = findViewById<LinearLayout>(R.id.loadingContainer)
        loader.visibility = View.GONE
        iv.visibility = View.VISIBLE
        Picasso.get().load(url).into(iv);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.size > 0 && grantResults[0] >= 0) {
            getLocation()
        }


    }

}