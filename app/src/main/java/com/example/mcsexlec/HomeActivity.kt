// HomeActivity.kt
package com.example.mcsexlec

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dojomovie.adapters.FilmAdapter
import com.example.dojomovie.models.Film
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var filmAdapter: FilmAdapter
    private val filmList = mutableListOf<Film>()
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        dbHelper = DatabaseHelper(this)

        // Cek apakah data film sudah ada di database, kalau belum tambahkan sample
        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_FILMS, null, null, null, null, null, null
        )
        if (cursor.count == 0) {
            insertSampleFilms()  // CHANGED: tambahkan sample film jika database kosong
        }
        cursor.close()

        // Map setup
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // RecyclerView setup
        filmAdapter = FilmAdapter(filmList)
        findViewById<RecyclerView>(R.id.rvFilms).apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = filmAdapter

            filmAdapter.setOnItemClickListener { film ->
                val intent = Intent(this@HomeActivity, DetailFilmActivity::class.java)
                intent.putExtra("film", film)
                startActivity(intent)
            }
        }

        // Search setup
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        setupFindLocation()

        // Load films
        loadFilmsFromDatabase()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val dojoLocation = LatLng(-6.2088, 106.8456)
        mMap.addMarker(MarkerOptions().position(dojoLocation).title("DoJo Movie"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dojoLocation, 12f))
    }

    private fun setupFindLocation() {
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString()
            if (query.isEmpty()) return@setOnClickListener

            val geocoder = Geocoder(this@HomeActivity)
            geocoder.getFromLocationName(query, 1) { addresses ->
                if (addresses.isNotEmpty()) {
                    val result = addresses[0]
                    val coord = LatLng(result.latitude, result.longitude)

                    runOnUiThread {
                        mMap.clear()
                        mMap.addMarker(MarkerOptions().position(coord).title(query))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coord, 12f))
                    }
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadFilmsFromDatabase() {
        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_FILMS, null, null, null, null, null, null
        )

        filmList.clear()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FILM_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FILM_TITLE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FILM_DESCRIPTION))
            val imageName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FILM_IMAGE))
            val price = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FILM_PRICE))

            // Ubah nama drawable ke resource id
            val posterResId = resources.getIdentifier(imageName, "drawable", packageName) // CHANGED

            val film = Film(id, title, description, posterResId, price)
            filmList.add(film)
        }
        cursor.close()
        filmAdapter.notifyDataSetChanged()
    }

    private fun insertSampleFilms() {
        val sampleFilms = listOf(
            Film(0, "Kongzilla", "Monster raksasa yang mengamuk di kota.", 0, 50000),
            Film(0, "Final Fantalion", "Petualangan fantasi epik di dunia lain.", 0, 45000)
        )
        for (film in sampleFilms) {
            dbHelper.addFilm(film.title, film.description, if (film.title == "Kongzilla") "kongzilla" else "final_fantalion", film.price)
        }
    }
}
