package com.example.mswproject

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.text)

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyCyRBE0IR0jmxLf61_9jmlH3Fphb5p4LpI"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                val list = ArrayList<Place>()
                val jsonArray = response.getJSONArray("results")

                var x = 0
                while (x < jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(x)

                    list.add(
                        Place(
                        jsonObject.getString("name")
                    ))
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