package com.example.mswproject

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONObject


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val locationButton = findViewById<Button>(R.id.location_button)

        searchButton.setOnClickListener(this)
        locationButton.setOnClickListener(this)
    }

    private fun placeSearch(lat: Double, lon: Double) {
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputManager.hideSoftInputFromWindow(
            currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${lat},${lon}&radius=1000&type=lodge&keyword=surf&key=AIzaSyCyRBE0IR0jmxLf61_9jmlH3Fphb5p4LpI"
        Log.d("click", url)

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                val list = ArrayList<Place>()
                val jsonArray = response.getJSONArray("results")

                var x = 0
                while (x < jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(x)

                    var openString = ""
                    if (jsonObject.has("opening_hours")) {
                        val openingHours = jsonObject.getJSONObject("opening_hours")
                        val open = openingHours.getBoolean("open_now")
                        if (open) {
                            openString = "Open now"
                        } else if (!open) {
                            openString = "Closed"
                        }
                    } else {
                        openString = "Opening times unavailable"
                    }
                    list.add(
                        Place(
                            jsonObject.getString("name"),
                            jsonObject.getInt("rating"),
                            openString
                        )
                    )
                    x++
                }

                val adapter = ListAdapter(context = this, list = list)
                places_list.adapter = adapter
            },
            Response.ErrorListener {error -> Log.e("Error", "Something went wrong $error") })

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.search_button -> {
                val latitudeString = findViewById<EditText>(R.id.latitude_text).text.toString()
                val latitude = latitudeString.toDouble()
                val longitudeString = findViewById<EditText>(R.id.longitude_text).text.toString()
                val longitude = longitudeString.toDouble()

                placeSearch(latitude, longitude)
            }
            R.id.location_button -> {
                val url = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyCyRBE0IR0jmxLf61_9jmlH3Fphb5p4LpI"
                // Instantiate the RequestQueue.
                val queue = Volley.newRequestQueue(this)

                val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, null,
                    Response.Listener { response ->
                        val locationObj = response.getJSONObject("location")
                        val lat = locationObj.getString("lat").toDouble()
                        val lon = locationObj.getString("lng").toDouble()

                        placeSearch(lat, lon)
                    },
                    Response.ErrorListener { error -> Log.e("Error", "Something went wrong $error")})

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest)
            }
        }

    }
}
 // comments, api key, classes/interfaces, tests, error handling