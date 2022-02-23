package com.example.flixster

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class MovieAdapter(private val context:Context,private val movies:List<Movie>)
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

    inner class ViewHolderCommon(itemView: View):RecyclerView.ViewHolder(itemView){
        private val ivPoster = itemView.findViewById<ImageView>(R.id.imageView)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvOverview = itemView.findViewById<TextView>(R.id.tvOverview)
        fun bind(movie:Movie){
            tvTitle.text = movie.title
            tvOverview.text = movie.overview
            Glide.with(context)
                .load(generateUrl(movie))
                .placeholder(R.mipmap.ic_launcher_round)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPoster)
        }
    }

    inner class ViewHolderFull(itemView: View):RecyclerView.ViewHolder(itemView){
        private val ivPoster = itemView.findViewById<ImageView>(R.id.imageView)
//        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
//        private val tvOverview = itemView.findViewById<TextView>(R.id.tvOverview)

        fun bind(movie:Movie){
//            tvTitle.text = movie.title
//            tvOverview.text = movie.overview
            Glide.with(context)
                .load(generateUrl(movie))
                .placeholder(R.mipmap.ic_launcher_round)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPoster)
        }
    }

    fun generateUrl(movie: Movie):GlideUrl{
        var url:String = movie.posterUrl
        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            || movie.vote>=THRESORD){
            url = movie.backdropPosterUrl
        }
        val lh = LazyHeaders.Builder().addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36").build()
        val ret = GlideUrl(url,lh)
        Log.d(TAG, "generateUrl: ${movie.title}:")
        return ret

    }
}