package com.example.movies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.movies.model.Global
import com.example.movies.model.ItemModel

class FavoritesActivity : AppCompatActivity() {
    private var listViewFavorites: ListView? = null
    private var customeAdapter: CustomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        listViewFavorites = findViewById(R.id.listFavorites) as ListView

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
                movieItemList.add(movieItem)
            }
        }

        customeAdapter = CustomAdapter(this, movieItemList)
        val lv = findViewById(R.id.listFavorites) as ListView
        lv.adapter = customeAdapter
    }


}