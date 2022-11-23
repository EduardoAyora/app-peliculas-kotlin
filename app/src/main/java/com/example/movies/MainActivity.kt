package com.example.movies


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import com.example.movies.adapters.CustomAdapter
import com.example.movies.model.Global
import com.example.movies.model.ItemModel

class MainActivity : AppCompatActivity() {
    private var lv: ListView? = null
    private var customeAdapter: CustomAdapter? = null
    private var itemModelArrayList: ArrayList<ItemModel>? = null
    private var pagina: Int = 1
    private val resultsPerPage: Int = 10
    private var totalResults: Int = 0
    private var moviePosterList = arrayOf<String>()
    private var movieNameList = arrayOf<String>()
    private var movieYearList = arrayOf<String>()
    private var movieImdbIDList = arrayOf<String>()

    override fun onStart() {
        super.onStart()
        if (Global.loggedUserId == null) {
            val intent = Intent(this, LoginActivity::class.java).apply {}
            startActivity(intent)
        }
        reloadItems()
    }

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

    private fun populateList(): ArrayList<ItemModel> {
        val db = DBHelper(this, null)
        val dbMovies = Global.loggedUserId?.let { db.getUserMovies(it) }
        val list = ArrayList<ItemModel>()
        for (i in 0..movieNameList.size - 1) {
            val itemModel = ItemModel()
            itemModel.setNames(movieNameList[i])
            itemModel.setYears(movieYearList[i])
            itemModel.setImagesUrl(moviePosterList[i])
            itemModel.imdbID = movieImdbIDList[i]
            val findedMovie = dbMovies?.find { it -> it.id == movieImdbIDList[i] }
            if (findedMovie != null) {
                itemModel.rating = findedMovie.rating
            }

            val db = DBHelper(this, null)
            val userMovies = Global.loggedUserId?.let { db.getUserMovies(it) }
            if (userMovies != null) {
                itemModel.isFavourite = userMovies.any { it.id == movieImdbIDList[i] }
            }
            list.add(itemModel)
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
                reloadItems()
            },
            Response.ErrorListener { error ->
                Log.e("Error", error.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }

    fun reloadItems() {
        itemModelArrayList = populateList()
        customeAdapter = CustomAdapter(this, itemModelArrayList!!)
        lv!!.adapter = customeAdapter
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.game_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuFavoritos -> {
                val intent = Intent(this, FavoritesActivity::class.java).apply {}
                startActivity(intent)
                true
            }
            R.id.menuSalir -> {
                Global.loggedUserId = null
                val intent = Intent(this, LoginActivity::class.java).apply {}
                startActivity(intent)
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

}