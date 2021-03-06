package com.example.mswproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatTextView

class ListAdapter(val context: Context, val list: ArrayList<Place>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false)

        val placeName = view.findViewById(R.id.places_name) as AppCompatTextView
        val placeRating = view.findViewById(R.id.places_rating) as AppCompatTextView
        val placeOpeningHours = view.findViewById(R.id.places_opening_hours) as AppCompatTextView

        placeName.text = list[position].name // to string on the rest
        placeRating.text = list[position].rating.toString()
        placeOpeningHours.text = list[position].openingHours.toString()

        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }
}