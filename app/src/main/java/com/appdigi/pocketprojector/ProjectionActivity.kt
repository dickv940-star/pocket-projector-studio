package com.appdigi.pocketprojector

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

class ProjectionActivity : Activity() {
    private var player: ExoPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUi()

        val flipX = if (intent.getBooleanExtra("flipH", false)) -1f else 1f
        val flipY = if (intent.getBooleanExtra("flipV", false)) -1f else 1f
        val brightness = intent.getFloatExtra("brightness", 1f).coerceIn(0.05f, 1f)
        window.attributes = window.attributes.apply { screenBrightness = brightness }

        container = FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }
        setContentView(container)

        if (intent.getBooleanExtra("pattern", false)) {
            container.addView(LensTestPattern(this).apply {
                scaleX = flipX
                scaleY = flipY
            }, FrameLayout.LayoutParams(-1, -1))
        } else {
            val uri: Uri? = intent.data
            if (uri == null) {
                finish()
                return
            }
            if (intent.getBooleanExtra("video", false)) {
                val playerView = PlayerView(this).apply {
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setBackgroundColor(Color.BLACK)
                    scaleX = flipX
                    scaleY = flipY
                }
                container.addView(playerView, FrameLayout.LayoutParams(-1, -1))
                player = ExoPlayer.Builder(this).build().also {
                    playerView.player = it
                    it.setMediaItem(MediaItem.fromUri(uri))
                    it.prepare()
                    it.playWhenReady = true
                }
            } else {
                val image = ImageView(this).apply {
                    setBackgroundColor(Color.BLACK)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    setImageURI(uri)
                    scaleX = flipX
                    scaleY = flipY
                }
                container.addView(image, FrameLayout.LayoutParams(-1, -1))
            }
        }
        scheduleTimer()
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun scheduleTimer() {
        timerRunnable = Runnable { finish() }
        handler.postDelayed(timerRunnable!!, 30L * 60L * 1000L)
    }

    override fun onDestroy() {
        timerRunnable?.let { handler.removeCallbacks(it) }
        player?.release()
        player = null
        super.onDestroy()
    }
}
 
private class LensTestPattern(context: android.content.Context) : View(context) {
    private val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: android.graphics.Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        canvas.drawColor(Color.BLACK)
        paint.color = Color.WHITE
        paint.strokeWidth = 2f
        paint.style = android.graphics.Paint.Style.STROKE
        val step = (w / 12f).coerceAtLeast(24f)
        var x = 0f
        while (x <= w) { canvas.drawLine(x, 0f, x, h, paint); x += step }
        var y = 0f
        while (y <= h) { canvas.drawLine(0f, y, w, y, paint); y += step }
        paint.strokeWidth = 5f
        canvas.drawLine(w / 2f, 0f, w / 2f, h, paint)
        canvas.drawLine(0f, h / 2f, w, h / 2f, paint)
        paint.style = android.graphics.Paint.Style.FILL
        paint.textAlign = android.graphics.Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 28f
        canvas.drawText("POCKET PROJECTOR STUDIO", w / 2f, h * 0.18f, paint)
        paint.textSize = 20f
        canvas.drawText("LENS FOCUS TEST  •  GRID + CROSSHAIR", w / 2f, h * 0.24f, paint)
        canvas.drawCircle(w / 2f, h / 2f, minOf(w, h) * 0.12f, paint.apply {
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 3f
        })
        paint.style = android.graphics.Paint.Style.FILL
        canvas.drawText("TOP", w / 2f, h * 0.08f, paint)
        canvas.drawText("BOTTOM", w / 2f, h * 0.94f, paint)
    }
}
