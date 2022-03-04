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
import com.google.android.youtube.player.YouTubeBaseActivity
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import org.json.*

private const val URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed"
const val TIMEOUT:Long = 3
class MainActivity : BaseActivity() , Callback{
    private val TAG = "flixsterMain"
    private val movies = mutableListOf<Movie>()
    private lateinit var rvMovies:RecyclerView
    private lateinit var movieAdapter:MovieAdapter
    private var jsonText:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val call = Http.createCall(URL)
        call.enqueue(this)
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
                    jsonText = null
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

    override fun onFailure(call: Call, e: IOException) {
        Log.e(TAG, "onFailure: ",e )
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

}