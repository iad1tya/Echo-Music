package com.alexmercerind.audire.ui

import android.app.SearchManager
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.IntentCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.drawable.CrossfadeDrawable
import coil.load
import coil.request.CachePolicy
import com.alexmercerind.audire.databinding.ActivityMusicBinding
import com.alexmercerind.audire.mappers.toSearchQuery
import com.alexmercerind.audire.mappers.toShareText
import com.alexmercerind.audire.models.Music
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MusicActivity : AppCompatActivity() {
    companion object {
        const val MUSIC = "MUSIC"
    }

    private lateinit var music: Music
    private lateinit var binding: ActivityMusicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        binding = ActivityMusicBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(binding.scrollView) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.buttonGroup) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = 16,
                left = 16 + insets.left,
                right = 16 + insets.right,
                bottom = 16 + insets.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }

        music = IntentCompat.getSerializableExtra(intent, MUSIC, Music::class.java)!!

        binding.titleTextView.text = music.title
        if (music.artists.isNotEmpty()) {
            binding.artistsTextView.text = music.artists
        } else {
            binding.artistsMaterialCardView.visibility = View.GONE
        }
        if (music.album != null) {
            binding.albumTextView.text = music.album
        } else {
            binding.albumMaterialCardView.visibility = View.GONE
        }
        if (music.label != null) {
            binding.labelTextView.text = music.label
        } else {
            binding.labelMaterialCardView.visibility = View.GONE
        }
        if (music.year != null) {
            binding.yearTextView.text = music.year
        } else {
            binding.yearMaterialCardView.visibility = View.GONE
        }
        binding.coverImageView.load(
            music.cover,
            ImageLoader.Builder(this)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build()
        ) {
            crossfade(true)
        }

        lifecycleScope.launch {
            delay(2000L)
            binding.titleTextView.isSelected = true
        }

        binding.searchButton.setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra(SearchManager.QUERY, music.toSearchQuery())
                })
        }
        binding.shareButton.setOnClickListener {
            startActivity(
                Intent.createChooser(
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "image/jpeg"
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        clipData = ClipData.newRawUri(music.title, imageUri)
                        putExtra(Intent.EXTRA_TEXT, music.toShareText())
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                    },
                    music.title
                )
            )
        }
    }

    private val imageUri: Uri? by lazy {
        return@lazy runCatching {
            val drawable = binding.coverImageView.drawable as CrossfadeDrawable
            val bitmap = drawable.end!!.toBitmap()
            val cache = File(cacheDir, "images")
            val image = File(cache, "cover.jpg")
            cache.mkdirs()
            FileOutputStream(image).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                image
            )
        }.getOrElse {
            it.printStackTrace()
            null
        }
    }
}
