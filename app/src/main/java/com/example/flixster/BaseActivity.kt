package com.example.flixster

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.youtube.player.YouTubeBaseActivity

private const val TAG = "baseActivity"
open class BaseActivity: YouTubeBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.d(TAG, "onCreate: set lanscape")
            actionBar?.hide()
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE
        }else {
            actionBar?.show()
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
            window.decorView.systemUiVisibility =window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_IMMERSIVE.inv()
        }
    }
}