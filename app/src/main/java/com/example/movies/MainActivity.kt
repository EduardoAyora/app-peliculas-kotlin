package com.example.movies


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {
    private val a = 3
    private val b = 3
    private var lv: ListView? = null
    private var customeAdapter: CustomAdapter? = null
    private var imageModelArrayList: ArrayList<ImageModel>? = null
    private var moviePosterList = arrayOf<String>()
    private var movieNameList = arrayOf<String>()
    private var movieYearList = arrayOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnBuscar: Button = findViewById(R.id.btnBuscar)
        val txtBuscar: EditText = findViewById(R.id.txtBuscar)

        lv = findViewById(R.id.listMovies) as ListView

        btnBuscar.setOnClickListener() {
            movieNameList = arrayOf<String>()
            movieYearList = arrayOf<String>()
            moviePosterList = arrayOf<String>()
            getMoviesInformation(txtBuscar.text.toString())
        }
    }

    private fun populateList(): ArrayList<ImageModel> {
        val list = ArrayList<ImageModel>()
        for (i in 0..movieNameList.size - 1) {
            val imageModel = ImageModel()
            imageModel.setNames(movieNameList[i])
            imageModel.setYears(movieYearList[i])
            imageModel.setImagesUrl(moviePosterList[i])
            list.add(imageModel)
        }
        return list
    }

    fun getMoviesInformation(search: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.omdbapi.com/?apikey=bcc3bc37&s=" + search

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val movies = response.getJSONArray("Search")
                for (i in 0 .. movies.length() - 1) {
                    val movie = movies.optJSONObject(i)
                    val title = movie.getString("Title")
                    val year = movie.getString("Year")
                    val poster = movie.getString("Poster")
                    movieNameList += title
                    movieYearList += year
                    moviePosterList += poster
                }
                imageModelArrayList = populateList()
                customeAdapter = CustomAdapter(this, imageModelArrayList!!)
                lv!!.adapter = customeAdapter
            },
            Response.ErrorListener { error ->
                Log.e("Error", error.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }

}