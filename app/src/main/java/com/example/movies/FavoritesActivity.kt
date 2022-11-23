package com.example.movies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.example.movies.adapters.CustomAdapterFavorites
import com.example.movies.model.Global
import com.example.movies.model.ItemModel

class FavoritesActivity : AppCompatActivity() {
    private var listViewFavorites: ListView? = null
    private var customeAdapter: CustomAdapterFavorites? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        listViewFavorites = findViewById(R.id.listFavorites) as ListView

        fun changeActivity(movieImdbID: String) {
            val intent = Intent(this, MovieActivity::class.java).apply {
                putExtra("imdbID", movieImdbID)
            }
            startActivity(intent)
        }

        fun reloadItems() {
            val movieItemList = ArrayList<ItemModel>()
            val db = DBHelper(this, null)
            val movies = Global.loggedUserId?.let { db.getUserMovies(it) }
            if (movies != null) {
                for (movie in movies) {
                    val movieItem = ItemModel()
                    movieItem.setNames(movie.title)
                    movieItem.imdbID = movie.id
                    movieItem.year = movie.year
                    movieItem.imageUrl = movie.imageUrl
                    movieItem.isFavourite = true
                    movieItemList.add(movieItem)
                }
            }
            customeAdapter = CustomAdapterFavorites(this, movieItemList, ::reloadItems)
            val lv = findViewById(R.id.listFavorites) as ListView
            lv.adapter = customeAdapter
            lv!!.onItemClickListener = object : AdapterView.OnItemClickListener {
                override fun onItemClick(parent: AdapterView<*>, view: View,
                                         position: Int, id: Long) {
                    if (movies != null) {
                        changeActivity(movies.get(position).id)
                    }
                }
            }
        }
        reloadItems()
    }


}