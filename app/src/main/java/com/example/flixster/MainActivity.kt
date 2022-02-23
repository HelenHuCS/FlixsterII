package com.example.flixster

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import org.json.*

private const val URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed"

class MainActivity : AppCompatActivity() {
    private val TAG = "flixsterMain"
    private val movies = mutableListOf<Movie>()
    private lateinit var rvMovies:RecyclerView
    private lateinit var movieAdapter:MovieAdapter
    private var jsonText:String? = null

    private val TIMEOUT:Long = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.d(TAG, "onCreate: set lanscape")
            supportActionBar?.hide()
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE
        }else {
            supportActionBar?.show()
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_IMMERSIVE.inv()
        }
        setContentView(R.layout.activity_main)
        rvMovies = findViewById(R.id.rvMovies)

        movieAdapter = MovieAdapter(this,movies)
        rvMovies.adapter = movieAdapter
        rvMovies.layoutManager = LinearLayoutManager(this)

        jsonText = savedInstanceState?.getString("json",null)
//        jsonText = testText
        if (jsonText != null){
            generateMovies()
            return
        }
        Log.d(TAG, "onCreate: json = $jsonText")

        val client = OkHttpClient.Builder()
            .readTimeout(TIMEOUT,TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }.build()

        val request = Request.Builder()
            .url(URL)
            .addHeader("user-agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
            .addHeader("content-type","application/json")
            .get()
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "onFailure: ",e )
                jsonText = testText
                generateMovies()
            }

            override fun onResponse(call: Call, response: Response) {
                jsonText = response.body?.string()
                Log.i(TAG, "onResponse: Json Data $jsonText")
                try {
                    if (jsonText != null) {
                        generateMovies()
                        Log.i(TAG, "onResponse: Movie list $movies")
                    }else{
                        Log.e(TAG, "onResponse: empty response", )
                    }

                } catch (e:JSONException){
                    Log.e(TAG, "onResponse: ",e )
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }

    private fun generateMovies(){
        runOnUiThread(object :Runnable{
            override fun run() {
                if (jsonText == null || jsonText!!.isEmpty()){
                    return
                }
                val l = Movie.fromJsonString(jsonText!!)
                Log.d(TAG, "run: movies = $l")
                movies.addAll(l)
                if (movies.isEmpty()){
                    jsonText = testText
                }
                movieAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: save json = $jsonText")
        outState.putString("json",jsonText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        jsonText = savedInstanceState.getString("json",null)
        Log.d(TAG, "onRestoreInstanceState: load json = $jsonText")
    }

}