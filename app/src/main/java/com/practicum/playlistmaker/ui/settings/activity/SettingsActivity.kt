package com.practicum.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel.themeSettings.observe(this) { isDarkEnabled ->
            binding.themeSwitcher.isChecked = isDarkEnabled
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeThemeSettings(isChecked)
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        binding.supportButton.setOnClickListener {
            viewModel.openSupport()
        }

        binding.licenseButton.setOnClickListener {
            viewModel.openTerms()
        }

    }

}