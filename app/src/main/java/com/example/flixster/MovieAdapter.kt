package com.example.flixster

import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.Pair
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

const val MOVIE_EXTRA = "movie"
class MovieAdapter(private val activity: MainActivity,private val movies:List<Movie>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val THRESORD = 8.0
    private val TAG = "MovieAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder:RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        when(viewType){
            1->{
                val v1:View = inflater.inflate(R.layout.item_movie_full,parent,false)
                viewHolder = ViewHolderFull(v1)
            }
            0->{
                val v2:View = inflater.inflate(R.layout.item_movie,parent,false)
                viewHolder = ViewHolderCommon(v2)
            }
            else ->{
                val v3:View = inflater.inflate(R.layout.item_movie,parent,false)
                viewHolder = ViewHolderCommon(v3)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType){
            1->  {
                val vh1 = holder as ViewHolderFull
                vh1.bind(movies[position])
            }
            0->{
                val vh2 = holder as ViewHolderCommon
                vh2.bind(movies[position])
            }
            else ->{
                val vh3 = holder as ViewHolderCommon
                vh3.bind(movies[position])
            }
        }
    }

    override fun getItemCount() = movies.size

    override fun getItemViewType(position: Int): Int {
        if (movies[position].vote >= THRESORD){
            return 1
        }
        return 0
    }
/*
    item view
 */
    inner class ViewHolderCommon(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        private val ivPoster = itemView.findViewById<ImageView>(R.id.imageView)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvOverview = itemView.findViewById<TextView>(R.id.tvOverview)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(movie:Movie){
            tvTitle.text = movie.title
            tvOverview.text = movie.overview
            Glide.with(activity)
                .load(generateUrl(movie))
                .placeholder(R.mipmap.ic_launcher_round)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(RoundedCorners(25))
                .into(ivPoster)
        }

        override fun onClick(v: View?) {
            val movie = movies[adapterPosition]
            val intent = Intent(activity,DetailActivity::class.java)
            intent.putExtra("movie_title",movie.title)
            intent.putExtra(MOVIE_EXTRA,movie)
            val p1 = Pair<View,String>(tvTitle,"title")
            val p2 = Pair<View,String>(tvOverview,"overview")
            val p3 = Pair<View,String>(ivPoster,"imageView")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,p1,p2,p3)
            activity.startActivity(intent,options.toBundle())
        }
    }

    inner class ViewHolderFull(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener,Callback {
        private val ivPoster = itemView.findViewById<ImageView>(R.id.imageView)
        private val buttonPlay = itemView.findViewById<ImageView>(R.id.playButton)
        private val ytPlayerView = itemView.findViewById<YouTubePlayerView>(R.id.player)
        private var isPlaying = false
        private var ytPlayer: YouTubePlayer? = null

        init {
            itemView.setOnClickListener(this)
            ytPlayerView.setOnClickListener(){
                isPlaying = !isPlaying
                updateViewState()
            }
            buttonPlay.setOnClickListener(object :View.OnClickListener{
                override fun onClick(p0: View?) {
                    isPlaying = true
                    updateViewState()
                    playVideo()
                }

            })
        }

        private fun playVideo(){
            val call = Http.createCall(getTrailresUrl(movies[adapterPosition].movieId))
            call.enqueue(this)
        }

        fun bind(movie:Movie){
//            tvTitle.text = movie.title
//            tvOverview.text = movie.overview
            Glide.with(activity)
                .load(generateUrl(movie))
                .placeholder(R.mipmap.ic_launcher_round)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPoster)
        }

        override fun onClick(v: View?) {
            val movie = movies[adapterPosition]
            val intent = Intent(activity,DetailActivity::class.java)
            intent.putExtra("movie_title",movie.title)
            intent.putExtra(MOVIE_EXTRA,movie)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,ivPoster,"imageView")
            activity.startActivity(intent,options.toBundle())
        }

        private fun updateViewState(){
            if (!isPlaying){
                ytPlayer?.release()
                ytPlayerView.visibility = GONE
                ivPoster.visibility = VISIBLE
                buttonPlay.visibility = VISIBLE
            } else {
                ytPlayerView.visibility = VISIBLE
                ivPoster.visibility = GONE
                buttonPlay.visibility = GONE
            }
        }

        private fun initalizeYoutube(key:String){
            activity.runOnUiThread {
                ytPlayerView.initialize(YOUTUBE_API_KEY,object : YouTubePlayer.OnInitializedListener{
                    override fun onInitializationSuccess(
                        provider: YouTubePlayer.Provider?,
                        player: YouTubePlayer?,
                        p2: Boolean
                    ) {
                        Log.i(TAG, "onInitializationSuccess")
                        if (player != null) {
                            ytPlayer = player
                        }
                        player?.cueVideo(key)
                        player?.play()
                    }

                    override fun onInitializationFailure(
                        p0: YouTubePlayer.Provider?,
                        p1: YouTubeInitializationResult?
                    ) {
                        Log.e(TAG, "onInitializationFailure: ${p1.toString()}", )
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
            } catch (e: JSONException){
                Log.e(TAG, "onResponse: ",e )
            }
        }
    }

    fun generateUrl(movie: Movie):GlideUrl{
        var url:String = movie.posterUrl
        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            || movie.vote>=THRESORD){
            url = movie.backdropPosterUrl
        }
        val lh = LazyHeaders.Builder().addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36").build()
        val ret = GlideUrl(url,lh)
        Log.d(TAG, "generateUrl: ${movie.title}:")
        return ret
    }
}