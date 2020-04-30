package com.example.mswproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("fwaf", "fdef")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)


        searchButton.setOnClickListener(this)
    }

    private fun onButtonPress(latitude: Float?, longitude: Float?) {

    }

    override fun onClick(v: View?) {
        val textView = findViewById<TextView>(R.id.text)
        val latitudeString = findViewById<EditText>(R.id.latitude_text).text.toString()
        val latitude = latitudeString.toDouble()
        val longitudeString = findViewById<EditText>(R.id.longitude_text).text.toString()
        val longitude = longitudeString.toDouble()

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        //val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=cafe&keyword=surf&key=AIzaSyCyRBE0IR0jmxLf61_9jmlH3Fphb5p4LpI"
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${latitude},${longitude}&radius=1500&type=lodge&keyword=surf&key=AIzaSyCyRBE0IR0jmxLf61_9jmlH3Fphb5p4LpI"
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
            Response.ErrorListener { textView.text = "That didn't work!" })

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }
}
 // comments, api key, classes/interfaces, tests