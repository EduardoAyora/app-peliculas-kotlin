package com.example.movies

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
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
                + ID_COL + " INTEGER PRIMARY KEY, " +
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

    // This method is for adding data in our database
    fun addUser(user : String, password : String ): Boolean {
        val values = ContentValues()

        values.put(USER_COl, user)
        values.put(PASSWORD_COL, password)

        val db = this.writableDatabase
        val _success = db.insert(USER_TABLE_NAME, null, values)
        db.close()
        return (Integer.parseInt("$_success") != -1)
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

    companion object {
        private val DATABASE_NAME = "peliculas"
        private val DATABASE_VERSION = 6
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