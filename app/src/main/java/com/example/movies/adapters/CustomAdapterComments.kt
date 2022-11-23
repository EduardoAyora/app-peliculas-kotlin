package com.example.movies.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import com.example.movies.DBHelper
import com.example.movies.R
import com.example.movies.model.Comment
import kotlin.collections.ArrayList

class CustomAdapterComments(private val context: Context, private val commentsArrayList: ArrayList<Comment>, private val reloadListItems: () -> Unit) : BaseAdapter() {

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun getCount(): Int {
        return commentsArrayList.size
    }

    override fun getItem(position: Int): Any {
        return commentsArrayList[position]
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
            convertView = inflater.inflate(R.layout.lv_comment_item, null, true)

            holder.text = convertView!!.findViewById(R.id.txtCommentWrited) as TextView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        holder.text!!.setText(commentsArrayList[position].text)

        val deleteButton: AppCompatImageButton = convertView!!.findViewById(R.id.trashIcon)

        deleteButton!!.setOnClickListener() {
            val db = DBHelper(this.context, null)
            Log.i("id", commentsArrayList[position].id.toString())
            commentsArrayList[position].id?.let { it1 -> db.deleteComment(it1) }
            reloadListItems()
        }
        return convertView
    }

    private inner class ViewHolder {
        var text: TextView? = null
    }
}