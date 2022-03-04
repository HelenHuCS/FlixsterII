package com.example.flixster

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

const val YOUTUBE_API_KEY = "AIzaSyBzBUqME_KI9FJACpGpH47bt3gvEpL049Y"
fun getTrailresUrl(id: Int):String{
    val url = "https://api.themoviedb.org/3/movie/$id/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed"
    Log.d(TAG, "getTrailresUrl: $url")
    return url
}
private const val TAG = "DetailActivity"
class DetailActivity : BaseActivity() , Callback{

    private lateinit var tvTitle: TextView
    private lateinit var tvOverview: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var ytPlayer:YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        tvTitle = findViewById(R.id.tvTitle)
        tvOverview = findViewById(R.id.tvOverview)
        ratingBar = findViewById(R.id.rbVoteAvarage)
        ytPlayer = findViewById(R.id.player)

        val movie = intent.getParcelableExtra<Movie>(MOVIE_EXTRA) as Movie
        Log.i(TAG, "onCreate: $movie")
        tvTitle.text = movie.title
        tvOverview.text = movie.overview
        ratingBar.rating = movie.vote.toFloat()

        val call = Http.createCall(getTrailresUrl(movie.movieId))
        call.enqueue(this)
    }

    private fun initalizeYoutube(key:String){
        runOnUiThread(){
            ytPlayer.initialize(YOUTUBE_API_KEY,object : YouTubePlayer.OnInitializedListener{
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider?,
                    player: YouTubePlayer?,
                    p2: Boolean
                ) {
                    Log.i(TAG, "onInitializationSuccess")
                    player?.cueVideo(key)
                }

                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
                ) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    override fun onFailure(call: Call, e: IOException) {
        Log.e(TAG, "onFailure: ",e )
    }

    override fun onResponse(call: Call, response: Response) {
        Log.i(TAG, "onResponse: ")
        try {
            val jsonText = response.body?.string()
            Log.d(TAG, "onResponse: $jsonText")
            val json = JSONObject(jsonText)
            val results = json.getJSONArray("results")
            if (results.length() == 0){
                Log.w(TAG, "onResponse: no movie found" )
                return
            }

            val movieTrailerJson = results.getJSONObject(0)
            val youtubeKey = movieTrailerJson.getString("key")
            initalizeYoutube(youtubeKey)
        } catch (e:JSONException){
            Log.e(TAG, "onResponse: ",e )
        }
    }
}