package com.example.mswproject

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback {
    val ZOOM_LEVEL = 13f

    var mMap: GoogleMap? = null
    val markers = ArrayList<Marker?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment : SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val searchButton = findViewById<Button>(R.id.search_button)
        val locationButton = findViewById<Button>(R.id.location_button)

        searchButton.setOnClickListener(this)
        locationButton.setOnClickListener(this)
    }

    private fun placeSearch(lat: Double, lon: Double) {
        // hide keyboard after search
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputManager.hideSoftInputFromWindow(
            currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )

        // clear markers from previous search
        mMap?.clear()
        markers.clear()

        val radiusText = findViewById<EditText>(R.id.radius_text).text.toString()
        val rad = radiusText.toInt()

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${lat},${lon}&radius=${rad}&type=lodge&keyword=surf&key=${getString(R.string.api_key)}"
        Log.d("click", url)

        // Request a response from the provided URL
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                val list = ArrayList<Place>()
                val jsonArray = response.getJSONArray("results")
                // for each place
                var x = 0
                while (x < jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    // get info
                    val name = jsonObject.getString("name")
                    val rating = jsonObject.getInt("rating")

                    // change opening hours boolean to string if it exists
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

                    // add marker to map
                    val geometryObj = jsonObject.getJSONObject("geometry")
                    val locationObj = geometryObj.getJSONObject("location")
                    val lat = locationObj.getDouble("lat")
                    val lon = locationObj.getDouble("lng")
                    val markerOptions = MarkerOptions()
                        .title(name)
                        .position(LatLng(lat, lon))
                    val marker = mMap?.addMarker(markerOptions)
                    markers.add(marker)

                    list.add(
                        Place(
                            name,
                            rating,
                            openString
                        )
                    )
                    x++
                }

                if (list.isNotEmpty()) {
                    // set camera to bounds of all markers
                    val builder = LatLngBounds.Builder()
                    for (marker in markers) {
                        builder.include(marker?.position)
                    }
                    val bounds = builder.build()
                    val move = CameraUpdateFactory.newLatLngBounds(bounds, 0)
                    mMap?.moveCamera(move)
                }

                val adapter = ListAdapter(context = this, list = list)
                places_list.adapter = adapter

                // zoom out so so markers on edge are included
                val zoom = CameraUpdateFactory.zoomOut()
                mMap?.moveCamera(zoom)

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
                val url = "https://www.googleapis.com/geolocation/v1/geolocate?key=${getString(R.string.api_key)}"
                // Instantiate the RequestQueue.
                val queue = Volley.newRequestQueue(this)

                val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, null,
                    Response.Listener { response ->
                        val locationObj = response.getJSONObject("location")
                        val lat = locationObj.getString("lat").toDouble()
                        val lon = locationObj.getString("lng").toDouble()

                        placeSearch(lat, lon)

                        // add marker for current location
                        val markerOptions = MarkerOptions()
                            .title("Me")
                            .position(LatLng(lat, lon))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        val marker = mMap?.addMarker(markerOptions)
                        markers.add(marker)
                        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), ZOOM_LEVEL))
                    },
                    Response.ErrorListener { error -> Log.e("Error", "Something went wrong $error")})

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest)
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        with(googleMap) {
            mMap = googleMap;
        }
    }
}
 // comments, tests, error handling