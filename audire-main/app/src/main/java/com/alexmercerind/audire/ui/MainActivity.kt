package com.alexmercerind.audire.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alexmercerind.audire.R
import com.alexmercerind.audire.databinding.ActivityMainBinding
import com.alexmercerind.audire.services.FindMusicTileService
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val identifyViewModel by viewModels<IdentifyViewModel>()
    val settingsViewModel by viewModels<SettingsViewModel>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // https://stackoverflow.com/a/50537193/12825435
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.content) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (!FindMusicTileService.handled || (!SettingsViewModel.autoStartHandled && settingsViewModel.autoStart.value)) {
                    FindMusicTileService.handled = true
                    SettingsViewModel.autoStartHandled = true
                    identifyViewModel.start()
                }
            }
        }
    }
}
