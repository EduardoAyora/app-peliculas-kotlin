package com.example.movies

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
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
                "FOREIGN KEY(" + USER_ID_COl + ") REFERENCES " + USER_TABLE_NAME + "(" + ID_COL + "))")

        db.execSQL(queryUser)
        db.execSQL(queryMovie)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
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

    fun addOrRemoveFavourite(userId: Int, movieId: String) {
        val db = this.writableDatabase

        val userMovies = this.getUserMovies(userId)
        if (userMovies.any { it.id == movieId }) {
            db.delete(MOVIE_TABLE_NAME, "$ID_COL =\"?\", $USER_ID_COl =?",
                arrayOf(movieId, userId.toString())
            )
        } else {
            val values = ContentValues()
            values.put(ID_COL, movieId)
            values.put(USER_ID_COl, userId)
            db.insert(MOVIE_TABLE_NAME, null, values)
        }
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
            while (cursor.moveToNext()) {
                val movie = Movie()
                movie.id = cursor.getString(cursor.getColumnIndex(ID_COL))
                val ratingString = cursor.getString(cursor.getColumnIndex(RATING_COl))
                if (ratingString != null && ratingString != "") movie.rating = Integer.parseInt(ratingString)
                val review = cursor.getString(cursor.getColumnIndex(REVIEW_COl))
                if (review != null) movie.review = review
                movie.userId = userId
                movies.add(movie)
            }
        }
        cursor.close()
        return movies
    }

    companion object {
        private val DATABASE_NAME = "peliculas"
        private val DATABASE_VERSION = 8
        val USER_TABLE_NAME = "usuario"
        val ID_COL = "id"
        val USER_COl = "username"
        val PASSWORD_COL = "password"

        val MOVIE_TABLE_NAME = "pelicula"
        val RATING_COl = "rating"
        val REVIEW_COl = "review"
        val USER_ID_COl = "user_id"
    }
}