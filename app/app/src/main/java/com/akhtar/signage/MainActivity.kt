package com.akhtar.signage

import android.net.Uri
import android.os.*
import android.view.View
import android.view.SurfaceView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

class MainActivity : AppCompatActivity() {

    private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var imageView: ImageView

    private val imageDuration = 5000L
    private val cameraDuration = 5000L

    private var camIndex = 0
    private var showImage = true

    private val cameras = listOf(
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/101",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/201",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/301",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/401",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/501",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/601",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/701",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/801",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/901",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/1001",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/1101",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/1201",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/1401",
        "rtsp://admin:12345678a@192.168.0.130:554/Streaming/Channels/1501"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageOverlay)
        imageView.setImageResource(R.drawable.image1)

        libVLC = LibVLC(this)
        mediaPlayer = MediaPlayer(libVLC)

        val surface = findViewById<SurfaceView>(R.id.cctvSurface)
        mediaPlayer.attachViews(surface, null, false, false)

        startLoop()
    }

    private fun startLoop() {
        val handler = Handler(Looper.getMainLooper())

        handler.post(object : Runnable {
            override fun run() {
                if (showImage) {
                    imageView.visibility = View.VISIBLE
                    handler.postDelayed({
                        showImage = false
                        run()
                    }, imageDuration)
                } else {
                    imageView.visibility = View.GONE
                    playCamera(cameras[camIndex])
                    handler.postDelayed({
                        camIndex = (camIndex + 1) % cameras.size
                        showImage = true
                        run()
                    }, cameraDuration)
                }
            }
        })
    }

    private fun playCamera(url: String) {
        mediaPlayer.stop()
        val media = Media(libVLC, Uri.parse(url))
        mediaPlayer.media = media
        mediaPlayer.play()
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        libVLC.release()
        super.onDestroy()
    }
}
