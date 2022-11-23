package com.example.movies.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import com.example.movies.DBHelper
import com.example.movies.R
import com.example.movies.model.Global
import com.example.movies.model.ItemModel
import java.util.ArrayList

class CustomAdapter(private val context: Context, private val itemModelArrayList: ArrayList<ItemModel>) : BaseAdapter() {

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun getCount(): Int {
        return itemModelArrayList.size
    }

    override fun getItem(position: Int): Any {
        return itemModelArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.lv_item, null, true)

            holder.movieName = convertView!!.findViewById(R.id.txtTitle) as TextView
            holder.movieYear = convertView!!.findViewById(R.id.txtCommentWrited) as TextView
            holder.iv = convertView.findViewById(R.id.imgView) as ImageView
            holder.checkFavourite = convertView.findViewById(R.id.chFavourite)

            holder.star1 = convertView.findViewById(R.id.star1)
            holder.star2 = convertView.findViewById(R.id.star2)
            holder.star3 = convertView.findViewById(R.id.star3)
            holder.star4 = convertView.findViewById(R.id.star4)
            holder.star5 = convertView.findViewById(R.id.trashIcon)

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        holder.movieName!!.setText(itemModelArrayList[position].getNames())
        holder.movieYear!!.setText(itemModelArrayList[position].getYears())
        holder.checkFavourite!!.isChecked = itemModelArrayList[position].isFavourite


        val starList: View = convertView!!.findViewById(R.id.starsList)
        fun verifyStartsEnabled() {
            if (!itemModelArrayList[position].isFavourite) {
                starList.visibility = View.INVISIBLE
            } else {
                starList.visibility = View.VISIBLE
            }
        }
        verifyStartsEnabled()

        fun verifyStarsNumber() {
            holder.star1?.setImageResource(R.drawable.star_blanca)
            holder.star2?.setImageResource(R.drawable.star_blanca)
            holder.star3?.setImageResource(R.drawable.star_blanca)
            holder.star4?.setImageResource(R.drawable.star_blanca)
            holder.star5?.setImageResource(R.drawable.star_blanca)
            if (itemModelArrayList[position].rating > 0) holder.star1?.setImageResource(R.drawable.star_amarilla)
            if (itemModelArrayList[position].rating > 1) holder.star2?.setImageResource(R.drawable.star_amarilla)
            if (itemModelArrayList[position].rating > 2) holder.star3?.setImageResource(R.drawable.star_amarilla)
            if (itemModelArrayList[position].rating > 3) holder.star4?.setImageResource(R.drawable.star_amarilla)
            if (itemModelArrayList[position].rating > 4) holder.star5?.setImageResource(R.drawable.star_amarilla)

            holder.star1?.isFocusable = false
            holder.star2?.isFocusable = false
            holder.star3?.isFocusable = false
            holder.star4?.isFocusable = false
            holder.star5?.isFocusable = false

            holder.star1?.isFocusableInTouchMode = false
            holder.star2?.isFocusableInTouchMode = false
            holder.star3?.isFocusableInTouchMode = false
            holder.star4?.isFocusableInTouchMode = false
            holder.star5?.isFocusableInTouchMode = false
        }
        verifyStarsNumber()

        holder.checkFavourite!!.setOnClickListener() {
            val db = DBHelper(this.context, null)
            itemModelArrayList[position].imdbID?.let { it1 -> Global.loggedUserId?.let { it2 ->
                db.addOrRemoveFavourite(
                    it2,
                    it1,
                    itemModelArrayList[position].getNames(),
                    itemModelArrayList[position].getYears(),
                    itemModelArrayList[position].getImagesUrl(),
                    0
                )
                itemModelArrayList[position].isFavourite = !itemModelArrayList[position].isFavourite
                itemModelArrayList[position].rating = 0
                verifyStartsEnabled()
                verifyStarsNumber()
            } }
        }

        fun updateRating(rating: Int) {
            val db = DBHelper(this.context, null)
            Global.loggedUserId?.let { it1 -> itemModelArrayList[position].imdbID?.let { it2 ->
                db.updateRating(it1,
                    it2, rating)
            } }
            itemModelArrayList[position].rating = rating
            verifyStarsNumber()
        }

        holder.star1?.setOnClickListener() {
            updateRating(1)
        }
        holder.star2?.setOnClickListener() {
            updateRating(2)
        }
        holder.star3?.setOnClickListener() {
            updateRating(3)
        }
        holder.star4?.setOnClickListener() {
            updateRating(4)
        }
        holder.star5?.setOnClickListener() {
            updateRating(5)
        }

        holder.iv?.let {
            DownloadImageFromInternet(it)
                .execute(itemModelArrayList[position].getImagesUrl())
        }
        return convertView
    }

    private inner class ViewHolder {
        var movieName: TextView? = null
        var movieYear: TextView? = null
        internal var iv: ImageView? = null
        var checkFavourite: CheckBox? = null
        var star1: AppCompatImageButton? = null
        var star2: AppCompatImageButton? = null
        var star3: AppCompatImageButton? = null
        var star4: AppCompatImageButton? = null
        var star5: AppCompatImageButton? = null
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