package com.wack.droplet

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.random.Random

class DropletAnimation(context: Context, private val surfaceView: SurfaceView) : SurfaceHolder.Callback, Runnable {

    private val surfaceHolder: SurfaceHolder = surfaceView.holder
    private var drawingThread: Thread? = null
    private var running = false
    private var touchX: Float = 0f
    private var touchY: Float = 0f

    private val paint: Paint = Paint().apply {
        color = Color.rgb(135, 206, 235)
        style = Paint.Style.FILL
    }

    private val flareList = mutableListOf<Flare>()

    init {
        surfaceHolder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // surfaceView 크기가 변경 되엇을 때
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stop()
    }

    private fun start() {
        running = true
        drawingThread = Thread(this).apply { start() }
    }

    private fun stop() {
        running = false
        drawingThread?.join()
    }

    override fun run() {
        while (running) {
            val canvas: Canvas? = surfaceHolder.lockCanvas(null)
            try {
                synchronized(surfaceHolder) {
                    canvas?.apply {
                        drawColor(Color.BLACK)
                        //Draw
                        flareList.forEach { flare ->
                            drawCircle(flare.x, flare.y, flare.size.toFloat(), paint)
                            flare.update()
                        }

                        //화면 밖으로 나간 파티클 제거
                        flareList.removeAll { flare -> flare.y < 0 }

                        // 터치햇을때, 생겨나는 파티클 생성하고, flareList에 추가
                        if (touchX != 0f && touchY != 0f) {
                            flareList.add(Flare(touchX, touchY))
                            touchX = 0f
                            touchY = 0f
                        }
                    }
                }
            } finally {
                canvas?.let { surfaceHolder.unlockCanvasAndPost(it) }
            }
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        touchX = event.x
        touchY = event.y
        return true
    }

    private inner class Flare(val startX: Float, val startY: Float) {
        var x: Float = startX
        var y: Float = startY
        var speed: Float = (Random.nextInt(MIN_SPEED, MAX_SPEED) * SPEED_MULTIPLIER).toFloat()
        var size: Int = Random.nextInt(MIN_SIZE, MAX_SIZE)

        fun update() {
            y -= speed
        }
    }

    companion object {
        private const val MAX_FLARE_COUNT = 10
        private const val MIN_SPEED = 5
        private const val MAX_SPEED = 20
        private const val SPEED_MULTIPLIER = 0.5
        private const val MIN_SIZE = 10
        private const val MAX_SIZE = 30
    }
}