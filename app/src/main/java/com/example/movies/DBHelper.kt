package com.example.movies

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.movies.model.Comment
import com.example.movies.model.Movie
import com.example.movies.model.User

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        val queryUser = ("CREATE TABLE " + USER_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                USER_COl + " TEXT," +
                PASSWORD_COL + " TEXT" + ")")

        val queryMovie = ("CREATE TABLE " + MOVIE_TABLE_NAME + " ("
                + ID_COL + " TEXT PRIMARY KEY, " +
                RATING_COl + " INTEGER," +
                USER_ID_COl + " INTEGER," +
                REVIEW_COl + " TEXT," +
                TITLE_COl + " TEXT," +
                YEAR_COl + " TEXT," +
                URL_COl + " TEXT," +
                "FOREIGN KEY(" + USER_ID_COl + ") REFERENCES " + USER_TABLE_NAME + "(" + ID_COL + "))")

        val queryComment = ("CREATE TABLE " + COMMENT_TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TEXT_COL + " TEXT," +
                MOVIE_ID_COL + " TEXT," +
                "FOREIGN KEY(" + MOVIE_ID_COL + ") REFERENCES " + MOVIE_TABLE_NAME + "(" + ID_COL + "))")

        db.execSQL(queryUser)
        db.execSQL(queryMovie)
        db.execSQL(queryComment)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + COMMENT_TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + MOVIE_TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME)
        onCreate(db)
    }

    fun addUser(user : String, password : String ) {
        val values = ContentValues()

        values.put(USER_COl, user)
        values.put(PASSWORD_COL, password)

        val db = this.writableDatabase
        db.insert(USER_TABLE_NAME, null, values)
        db.close()
    }

    fun addOrRemoveFavourite(userId: Int, movieId: String, movieTitle: String, movieYear: String, movieImageUrl: String, rating: Int) {
        val db = this.writableDatabase

        val userMovies = this.getUserMovies(userId)
        if (userMovies.any { it.id == movieId }) {
            db.delete(MOVIE_TABLE_NAME, "$ID_COL = \"$movieId\" AND $USER_ID_COl = ${userId.toString()}", null)
        } else {
            val values = ContentValues()
            values.put(ID_COL, movieId)
            values.put(USER_ID_COl, userId)
            values.put(TITLE_COl, movieTitle)
            values.put(YEAR_COl, movieYear)
            values.put(URL_COl, movieImageUrl)
            values.put(RATING_COl, rating)
            db.insert(MOVIE_TABLE_NAME, null, values)
        }
        db.close()
    }

    fun updateRating(userId: Int, movieId: String, rating: Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(RATING_COl, rating)
        db.update(MOVIE_TABLE_NAME, values, "$ID_COL = \"$movieId\" AND $USER_ID_COl = ${userId.toString()}", null)
        db.close()
    }

    @SuppressLint("Range")
    fun getUser(username: String): User? {
        var user: User? = null
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $USER_TABLE_NAME WHERE $USER_COl = \"$username\""
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            cursor.moveToFirst()
            while (cursor.isAfterLast == false) {
                user = User()
                user.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID_COL)))
                user.username = cursor.getString(cursor.getColumnIndex(USER_COl))
                user.password = cursor.getString(cursor.getColumnIndex(PASSWORD_COL))
                cursor.moveToNext()
            }
        }
        cursor.close()
        return user
    }

    @SuppressLint("Range")
    fun getUserMovies(userId: Int): List<Movie> {
        val movies = ArrayList<Movie>()
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $MOVIE_TABLE_NAME WHERE $USER_ID_COl = $userId"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            cursor.moveToFirst()
            while (cursor.isAfterLast == false) {
                val movie = Movie()
                movie.id = cursor.getString(cursor.getColumnIndex(ID_COL))
                val ratingString = cursor.getString(cursor.getColumnIndex(RATING_COl))
                if (ratingString != null && ratingString != "") movie.rating = Integer.parseInt(ratingString)
                val review = cursor.getString(cursor.getColumnIndex(REVIEW_COl))
                if (review != null) movie.review = review
                movie.userId = userId
                movie.title = cursor.getString(cursor.getColumnIndex(TITLE_COl))
                movie.year = cursor.getString(cursor.getColumnIndex(YEAR_COl))
                movie.imageUrl = cursor.getString(cursor.getColumnIndex(URL_COl))
                movies.add(movie)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return movies
    }

    fun addComment(text: String, movieId: String) {
        val values = ContentValues()

        values.put(TEXT_COL, text)
        values.put(MOVIE_ID_COL, movieId)

        val db = this.writableDatabase
        db.insert(COMMENT_TABLE_NAME, null, values)
        db.close()
    }

    fun deleteComment(id: Int) {
        val db = this.writableDatabase
        db.delete(COMMENT_TABLE_NAME, "$ID_COL = $id", null)
        db.close()
    }

    @SuppressLint("Range")
    fun getMovieComments(movieId: String): ArrayList<Comment> {
        val comments = ArrayList<Comment>()
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $COMMENT_TABLE_NAME WHERE $MOVIE_ID_COL = \"$movieId\""
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            cursor.moveToFirst()
            while (cursor.isAfterLast == false) {
                val comment = Comment()
                val idString = cursor.getString(cursor.getColumnIndex(ID_COL))
                Log.i("id get all", idString)
                if (idString != null && idString != "") comment.id = Integer.parseInt(idString)
                comment.text = cursor.getString(cursor.getColumnIndex(TEXT_COL))
                comments.add(comment)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return comments
    }

    companion object {
        private val DATABASE_NAME = "peliculas"
        private val DATABASE_VERSION = 13
        val USER_TABLE_NAME = "usuario"
        val ID_COL = "id"
        val USER_COl = "username"
        val PASSWORD_COL = "password"

        val MOVIE_TABLE_NAME = "pelicula"
        val RATING_COl = "rating"
        val REVIEW_COl = "review"
        val TITLE_COl = "title"
        val YEAR_COl = "year"
        val URL_COl = "url"
        val USER_ID_COl = "user_id"

        val COMMENT_TABLE_NAME = "comentario"
        val TEXT_COL = "text"
        val MOVIE_ID_COL = "movie_id"
    }
}