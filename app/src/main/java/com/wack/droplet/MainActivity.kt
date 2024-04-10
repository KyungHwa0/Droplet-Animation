package com.wack.droplet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.wack.droplet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dropletAnimation: DropletAnimation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dropletAnimation = DropletAnimation(this, binding.surfaceView)
    }

    override fun onResume() {
        super.onResume()
        binding.surfaceView.holder.addCallback(dropletAnimation)
    }

    override fun onPause() {
        super.onPause()
        binding.surfaceView.holder.removeCallback(dropletAnimation)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dropletAnimation.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}