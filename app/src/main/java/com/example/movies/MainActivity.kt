package com.example.movies


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {
    private var lv: ListView? = null
    private var customeAdapter: CustomAdapter? = null
    private var imageModelArrayList: ArrayList<ImageModel>? = null
    private var pagina: Int = 1
    private val resultsPerPage: Int = 10
    private var totalResults: Int = 0
    private var moviePosterList = arrayOf<String>()
    private var movieNameList = arrayOf<String>()
    private var movieYearList = arrayOf<String>()
    private var movieImdbIDList = arrayOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnBuscar: Button = findViewById(R.id.btnBuscar)
        val btnSiguiente: Button = findViewById(R.id.btnSiguiente)
        val btnAnterior: Button = findViewById(R.id.btnAnterior)
        val txtBuscar: EditText = findViewById(R.id.txtBuscar)
        val txtPagina: TextView = findViewById(R.id.txtPagina)

        fun changeActivity(movieImdbID: String) {
            val intent = Intent(this, MovieActivity::class.java).apply {
                putExtra("imdbID", movieImdbID)
            }
            startActivity(intent)
        }

        lv = findViewById(R.id.listMovies) as ListView
        lv!!.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View,
                                     position: Int, id: Long) {
                changeActivity(movieImdbIDList[position])
            }
        }

        btnBuscar.setOnClickListener() {
            this.pagina = 1
            txtPagina.text = "Página: " + this.pagina
            getMoviesInformation(txtBuscar.text.toString())
        }

        btnSiguiente.setOnClickListener() {
            onClicSiguiente(txtBuscar.text.toString())
        }

        btnAnterior.setOnClickListener() {
            onClicAnterior(txtBuscar.text.toString())
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
        movieNameList = arrayOf<String>()
        movieYearList = arrayOf<String>()
        moviePosterList = arrayOf<String>()
        movieImdbIDList = arrayOf<String>()

        val queue = Volley.newRequestQueue(this)
        val url = "https://www.omdbapi.com/?apikey=bcc3bc37&s=" + search + "&page=" + this.pagina
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val movies = response.getJSONArray("Search")
                this.totalResults = response.getInt("totalResults")
                for (i in 0 .. movies.length() - 1) {
                    val movie = movies.optJSONObject(i)
                    val title = movie.getString("Title")
                    val year = movie.getString("Year")
                    val poster = movie.getString("Poster")
                    val imdbID = movie.getString("imdbID")
                    movieNameList += title
                    movieYearList += year
                    moviePosterList += poster
                    movieImdbIDList += imdbID
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

    fun onClicSiguiente(search: String) {
        if(this.pagina == (this.totalResults / this.resultsPerPage).toInt()) {
            return Toast.makeText(applicationContext,"Está en la última página",Toast.LENGTH_LONG).show()
        }
        this.pagina += 1
        val txtPagina: TextView = findViewById(R.id.txtPagina)
        txtPagina.text = "Página: " + this.pagina
        getMoviesInformation(search)
    }

    fun onClicAnterior(search: String) {
        if (this.pagina == 1) {
            return Toast.makeText(applicationContext,"Está en la primera página",Toast.LENGTH_LONG).show()
        }
        this.pagina -= 1
        val txtPagina: TextView = findViewById(R.id.txtPagina)
        txtPagina.text = "Página: " + this.pagina
        getMoviesInformation(search)
    }

}