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


}
