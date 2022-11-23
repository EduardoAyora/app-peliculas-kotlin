package com.example.movies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.movies.adapters.CustomAdapterComments
import com.example.movies.adapters.CustomAdapterFavorites
import com.example.movies.model.Comment
import com.example.movies.model.Global
import com.example.movies.model.ItemModel

class ReviewsActivity : AppCompatActivity() {
    private var commentsList: ArrayList<Comment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        val btnAddComment: Button = findViewById(R.id.btnAddComment)
        val txtComment: EditText = findViewById(R.id.txtCommentToAdd)

        val imdbID = intent.getStringExtra("imdbID")

        var customeAdapter: CustomAdapterComments? = null
        fun reloadItems() {
            if (imdbID != null) {
                val db = DBHelper(this, null)
                this.commentsList = db.getMovieComments(imdbID)
            }
            customeAdapter = commentsList?.let { CustomAdapterComments(this, it, ::reloadItems) }
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