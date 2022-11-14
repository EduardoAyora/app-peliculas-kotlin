package com.example.movies

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val imdbID = intent.getStringExtra("imdbID")
        if (imdbID != null) {
            getMovieInformation(imdbID)
        }
    }

    fun getMovieInformation(id: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.omdbapi.com/?apikey=bcc3bc37&i=" + id

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val title = response.getString("Title")
                val year = response.getString("Year")
                val rated = response.getString("Rated")
                val released = response.getString("Released")
                val runtime = response.getString("Runtime")
                val genre = response.getString("Genre")
                val director = response.getString("Director")
                val writer = response.getString("Writer")
                val actors = response.getString("Actors")
                val plot = response.getString("Plot")
                val language = response.getString("Language")
                val country = response.getString("Country")
                val metascore = response.getString("Metascore")
                val imdbRating = response.getString("imdbRating")
                val imdbVotes = response.getString("imdbVotes")
                val imgUrl = response.getString("Poster")
                val awards = response.getString("Awards")
                val ratings = response.getJSONArray("Ratings")
                var ratingsString = ""

                for (i in 0 .. ratings.length() - 1) {
                    val rating = ratings.optJSONObject(i)
                    val source = rating.getString("Source")
                    val value = rating.getString("Value")
                    ratingsString += source + " calificó: " + value + "\n"
                }

                val txtTitle: TextView = findViewById(R.id.txtTitleMovie)
                val txtYear: TextView = findViewById(R.id.txtYearMovie)
                val txtRated: TextView = findViewById(R.id.txtRatedMovie)
                val txtReleased: TextView = findViewById(R.id.txtReleasedMovie)
                val txtRuntime: TextView = findViewById(R.id.txtRuntimeMovie)
                val txtGenre: TextView = findViewById(R.id.txtGenreMovie)
                val txtDirector: TextView = findViewById(R.id.txtDirectorMovie)
                val txtWriter: TextView = findViewById(R.id.txtWriterMovie)
                val txtActors: TextView = findViewById(R.id.txtActorsMovie)
                val txtPlot: TextView = findViewById(R.id.txtPlotMovie)
                val txtLanguage: TextView = findViewById(R.id.txtLanguageMovie)
                val txtCountry: TextView = findViewById(R.id.txtCountryMovie)
                val txtAwards: TextView = findViewById(R.id.txtAwardsMovie)
                val txtRatings: TextView = findViewById(R.id.txtRatingsMovie)
                val txtMetascore: TextView = findViewById(R.id.txtMetascoreMovie)
                val txtImdbRating: TextView = findViewById(R.id.txtimdbRatingMovie)
                val txtImdbVotes: TextView = findViewById(R.id.txtimdbVotesMovie)

                txtTitle.text = Html.fromHtml("<b>Título</b>: " + title)
                txtYear.text = Html.fromHtml("<b>Fecha</b>: " + year)
                txtRated.text = Html.fromHtml("<b>Clasificado</b>: " + rated)
                txtReleased.text = Html.fromHtml("<b>Liberado</b>: " + released)
                txtRuntime.text = Html.fromHtml("<b>Tiempo de ejecución</b>: " + runtime)
                txtGenre.text = Html.fromHtml("<b>Género</b>: " + genre)
                txtDirector.text = Html.fromHtml("<b>Director</b>: " + director)
                txtWriter.text = Html.fromHtml("<b>Escritor</b>: " + writer)
                txtActors.text = Html.fromHtml("<b>Acotres</b>: " + actors)
                txtPlot.text = Html.fromHtml("<b>Resumen</b>: " + plot)
                txtLanguage.text = Html.fromHtml("<b>Lenguaje</b>: " + language)
                txtCountry.text = Html.fromHtml("<b>País</b>: " + country)
                txtAwards.text = Html.fromHtml("<b>Premios</b>: " + awards)
                txtRatings.text = Html.fromHtml("<b>Calificaciones</b>: \n" + ratingsString)
                txtMetascore.text = Html.fromHtml("<b>Puntuación meta</b>: " + metascore)
                txtImdbRating.text = Html.fromHtml("<b>Calificaciones Imdb</b>: " + imdbRating)
                txtImdbVotes.text = Html.fromHtml("<b>Votos Imdb</b>: " + imdbVotes)

                DownloadImageFromInternet(findViewById(R.id.imgPoster))
                    .execute(imgUrl)

            },
            Response.ErrorListener { error ->
                Log.e("Error", error.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }

    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        init {
        }
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }
}