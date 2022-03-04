package com.example.flixster

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.IgnoredOnParcel
import org.json.JSONException
import org.json.JSONObject
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val movieId:Int,
    private val posterPath:String,
    private val backdropPath:String,
    val vote: Double,
    val title:String,
    val overview:String,
) : Parcelable {
    @IgnoredOnParcel
    val posterUrl = "http://image.tmdb.org/t/p/w342$posterPath"
    @IgnoredOnParcel
    val backdropPosterUrl = "http://image.tmdb.org/t/p/original$backdropPath"


    companion object {
        private val TAG = "movie"

        fun fromJsonString(string:String):List<Movie>{
            val movies = mutableListOf<Movie>()

            try {
                val json = JSONObject(string)
                val results = json.getJSONArray("results")

                for (i in 0 until results.length()){
                    val movieJson = results.getJSONObject(i)
                    movies.add(
                        Movie(
                            movieJson.getInt("id"),
                            movieJson.getString("poster_path"),
                            movieJson.getString("backdrop_path"),
                            movieJson.getDouble("vote_average"),
                            movieJson.getString("title"),
                            movieJson.getString("overview")
                        )
                    )
                }
            } catch (e:JSONException){
                Log.e(TAG, "fromJsonString: ",e )
            }
            return movies
        }
    }

}