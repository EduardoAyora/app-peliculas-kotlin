package com.example.movies

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.movies.model.User

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                USER_COl + " TEXT," +
                PASSWORD_COL + " TEXT" + ")")

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    // This method is for adding data in our database
    fun addUser(user : String, password : String ){
        val values = ContentValues()

        values.put(USER_COl, user)
        values.put(PASSWORD_COL, password)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getUser(username: String): User? {
        var user: User? = null
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $USER_COl = '$username'"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                user = User()
                user.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID_COL)))
                user.username = cursor.getString(cursor.getColumnIndex(USER_COl))
                user.password = cursor.getString(cursor.getColumnIndex(PASSWORD_COL))
            }
        }
        cursor.close()
        return user
    }

    companion object {
        private val DATABASE_NAME = "peliculas"
        private val DATABASE_VERSION = 3
        val TABLE_NAME = "usuario"
        val ID_COL = "id"
        val USER_COl = "username"
        val PASSWORD_COL = "password"
    }
}