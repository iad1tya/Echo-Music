package com.alexmercerind.audire.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.alexmercerind.audire.databinding.ActivityAboutBinding
import androidx.core.net.toUri

class AboutActivity : AppCompatActivity() {
    companion object {
        private const val GITHUB = "https://github.com/alexmercerind/audire"
        private const val LICENSE = "https://github.com/alexmercerind/audire/blob/main/LICENSE"
        private const val PRIVACY = "https://github.com/alexmercerind/audire/wiki/Privacy-Policy-%5BPlay-Store%5D"

        private const val DEVELOPER_GITHUB = "https://github.com/alexmercerind"
        private const val DEVELOPER_X = "https://x.com/alexmercerind"
    }

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            binding.descriptionTextView.text = listOf(
                info.versionName,
                "(${if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) info.versionCode else info.longVersionCode})"
            ).joinToString(" ")
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        binding.materialToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        setOnClickListener(binding.githubLinearLayout, GITHUB)
        setOnClickListener(binding.licenseLinearLayout, LICENSE)
        setOnClickListener(binding.privacyLinearLayout, PRIVACY)
        setOnClickListener(binding.developerGitHubLinearLayout, DEVELOPER_GITHUB)
        setOnClickListener(binding.developerXLinearLayout, DEVELOPER_X)
    }

    private fun setOnClickListener(view: View, uri: String) {
        view.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
                startActivity(intent)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
