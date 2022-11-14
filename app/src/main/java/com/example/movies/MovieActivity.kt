package com.example.movies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val imdbID = intent.getStringExtra("imdbID")
        if (imdbID != null) {
            getMovieInformation(imdbID)
        }
    }

    fun getMovieInformation(id: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.omdbapi.com/?apikey=bcc3bc37&i=" + id

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val mov = response.getString("Title")

                Log.i("mov", mov)

                val txtTitle: TextView = findViewById(R.id.txtTitleMovie)
                txtTitle.text = mov

            },
            Response.ErrorListener { error ->
                Log.e("Error", error.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }
}