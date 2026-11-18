package com.appdigi.pocketprojector

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding

class MainActivity : Activity() {
    private var selectedUri: Uri? = null
    private var selectedIsVideo = false
    private var flipH = false
    private var flipV = false
    private var lensMode = true
    private var brightness = 1.0f

    private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selectedUri = it; selectedIsVideo = true; showSelection("Video siap diputar") }
    }
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selectedUri = it; selectedIsVideo = false; showSelection("Gambar siap ditampilkan") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8, 10, 24)
        window.navigationBarColor = Color.rgb(8, 10, 24)
        buildUi()
    }

    private lateinit var selectionLabel: TextView
    private lateinit var modeButton: Button

    private fun buildUi() {
        val scroll = ScrollView(this).apply { setBackgroundColor(Color.rgb(8, 10, 24)) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22)
        }
        scroll.addView(root)
        setContentView(scroll)

        root.addView(text("POCKET PROJECTOR", 25f, Color.WHITE, true))
        root.addView(text("STUDIO  •  ANDROID EDITION", 12f, Color.rgb(139, 160, 255), true))
        root.addView(space(22))
        root.addView(text("Gunakan layar Android sebagai sumber visual untuk sistem lensa eksternal.", 16f, Color.LTGRAY))
        root.addView(space(18))

        selectionLabel = text("Belum ada media dipilih", 14f, Color.rgb(190, 198, 220))
        root.addView(selectionLabel)
        root.addView(space(12))
        root.addView(actionButton("PILIH VIDEO", "Pilih video dari perangkat") { pickVideo.launch("video/*") })
        root.addView(actionButton("PILIH GAMBAR", "Pilih gambar dari perangkat") { pickImage.launch("image/*") })
        root.addView(space(16))

        modeButton = actionButton("MODE: LENS PROJECTION", "Tampilan fullscreen disiapkan untuk jalur lensa") {
            lensMode = !lensMode
            modeButton.text = if (lensMode) "MODE: LENS PROJECTION" else "MODE: SCREEN"
        }
        root.addView(modeButton)
        root.addView(actionButton("UJI POLA LENSA", "Tampilkan grid, silang, dan teks untuk uji fokus") {
            openProjection(pattern = true)
        })
        root.addView(space(8))
        root.addView(actionButton("MULAI FULLSCREEN", "Buka media terpilih dalam tampilan fullscreen") {
            if (selectedUri == null) {
                Toast.makeText(this, "Pilih video atau gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
            } else openProjection(pattern = false)
        })

        root.addView(space(20))
        root.addView(text("KOREKSI OPTIK", 14f, Color.rgb(139, 160, 255), true))
        root.addView(actionButton("BALIK HORIZONTAL: OFF", "Cermin kiri-kanan") {
            flipH = !flipH
            (it as Button).text = "BALIK HORIZONTAL: " + if (flipH) "ON" else "OFF"
        })
        root.addView(actionButton("BALIK VERTIKAL: OFF", "Cermin atas-bawah") {
            flipV = !flipV
            (it as Button).text = "BALIK VERTIKAL: " + if (flipV) "ON" else "OFF"
        })
        root.addView(space(18))
        root.addView(text("Kecerahan fullscreen", 14f, Color.LTGRAY))
        val seek = SeekBar(this).apply {
            max = 100
            progress = 100
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, user: Boolean) {
                    brightness = p.coerceAtLeast(5) / 100f
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }
        root.addView(seek)
        root.addView(space(16))
        root.addView(text("Kompatibilitas: Android 6.0 (API 23) ke atas. Hasil dan performa bergantung pada spesifikasi perangkat. Proyeksi nyata membutuhkan lensa/optik eksternal dan ruangan gelap; aplikasi tidak mengubah flash menjadi proyektor.", 13f, Color.rgb(170, 180, 205)))
        root.addView(space(24))
    }

    private fun showSelection(message: String) {
        selectionLabel.text = message + "\n" + (selectedUri?.lastPathSegment ?: "")
    }

    private fun openProjection(pattern: Boolean) {
        val intent = Intent(this, ProjectionActivity::class.java).apply {
            putExtra("pattern", pattern)
            putExtra("video", selectedIsVideo)
            putExtra("flipH", flipH)
            putExtra("flipV", flipV)
            putExtra("lensMode", lensMode)
            putExtra("brightness", brightness)
            selectedUri?.let { data = it; addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        }
        startActivity(intent)
    }

    private fun text(value: String, size: Float, color: Int, bold: Boolean = false) =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

    private fun space(height: Int) = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, height)
    }

    private fun actionButton(title: String, description: String, action: (View) -> Unit) =
        Button(this).apply {
            text = title
            isAllCaps = false
            textSize = 14f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(34, 46, 86))
            gravity = Gravity.CENTER
            setOnClickListener(action)
            contentDescription = description
            layoutParams = LinearLayout.LayoutParams(-1, 54).apply { bottomMargin = 8 }
        }
}
