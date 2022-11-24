package com.example.movies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.movies.adapters.CustomAdapterComments
import com.example.movies.adapters.CustomAdapterFavorites
import com.example.movies.model.Comment
import com.example.movies.model.Global
import com.example.movies.model.ItemModel
import com.example.movies.model.Movie

class ReviewsActivity : AppCompatActivity() {
    private var commentsList: ArrayList<Comment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        val btnAddComment: Button = findViewById(R.id.btnAddComment)
        val txtComment: EditText = findViewById(R.id.txtCommentToAdd)

        val imdbID = intent.getStringExtra("imdbID")

        val db = DBHelper(this, null)
        val movies = Global.loggedUserId?.let { db.getUserMovies(it) }
        val movieInFavorites: Movie? = movies?.find { it.id == imdbID }
        if (movieInFavorites == null) btnAddComment.isEnabled = false
        if (movieInFavorites != null && movieInFavorites.rating == 0) btnAddComment.isEnabled = false

        fun sendMessage(message: String) {
            val db = DBHelper(this, null)
            val movies = Global.loggedUserId?.let { db.getUserMovies(it) }
            val movieTitle: String = movies?.find { it.id == imdbID }?.title ?: ""
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, "La película $movieTitle tiene la siguiente reseña: $message")
            startActivity(intent)
        }

        var customeAdapter: CustomAdapterComments? = null
        fun reloadItems() {
            if (imdbID != null) {
                val db = DBHelper(this, null)
                this.commentsList = db.getMovieComments(imdbID)
            }
            customeAdapter = commentsList?.let { CustomAdapterComments(this, it, ::reloadItems, ::sendMessage) }
            val lv = findViewById(R.id.listComments) as ListView
            lv.adapter = customeAdapter
        }
        reloadItems()

        btnAddComment.setOnClickListener() {
            val db = DBHelper(this, null)
            if (imdbID != null) {
                db.addComment(txtComment.text.toString(), imdbID)

            }
            txtComment.setText("")
            reloadItems()
        }
    }
}