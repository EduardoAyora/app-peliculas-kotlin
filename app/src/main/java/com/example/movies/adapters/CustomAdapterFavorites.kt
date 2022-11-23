package com.example.movies.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.movies.DBHelper
import com.example.movies.R
import com.example.movies.model.Global
import com.example.movies.model.ItemModel
import java.util.ArrayList

class CustomAdapterFavorites(private val context: Context, private val itemModelArrayList: ArrayList<ItemModel>, private val reloadListItems: () -> Unit) : BaseAdapter() {

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

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        holder.movieName!!.setText(itemModelArrayList[position].getNames())
        holder.movieYear!!.setText(itemModelArrayList[position].getYears())
        holder.checkFavourite!!.isChecked = itemModelArrayList[position].isFavourite

        val starList: View = convertView!!.findViewById(R.id.starsList)
        starList.visibility = View.INVISIBLE

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
                reloadListItems()
            } }
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