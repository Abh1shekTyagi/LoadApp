package com.example.loadapp

import android.app.DownloadManager
import android.app.NotificationManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.loadapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()
        val intent = intent
        if (intent?.action == NOTIFICATION_ACTION_CLICKED) {
            val status = intent.getIntExtra(DOWNLOAD_STATUS, 0)
            val fileName = intent.getStringExtra(DOWNLOAD_FILE_NAME)
            initUI(status, fileName)
        }
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        )
        notificationManager?.cancelNotifications()
    }

    private fun initUI(status: Int, fileName: String?) {
        binding.downloadedFileName.text = fileName
        binding.downloadStatus.text = when (status) {
            DownloadManager.STATUS_SUCCESSFUL -> {
                binding.downloadStatus.setTextColor(resources.getColor(R.color.tealBlue))
                getString(R.string.success)
            }

            DownloadManager.STATUS_PENDING -> {
                binding.downloadStatus.setTextColor(Color.BLACK)
                getString(R.string.pending)
            }

            DownloadManager.STATUS_RUNNING -> {
                binding.downloadStatus.setTextColor(Color.BLACK)
                getString(R.string.running)
            }

            DownloadManager.STATUS_PAUSED -> {
                binding.downloadStatus.setTextColor(Color.BLACK)
                getString(R.string.paused)
            }

            DownloadManager.STATUS_FAILED -> {
                binding.downloadStatus.setTextColor(Color.RED)
                getString(R.string.success)
            }

            else -> {
                binding.downloadStatus.setTextColor(Color.RED)
                getString(R.string.fail)
            }
        }
    }

    private fun initListeners() {
        binding.button.setOnClickListener {
            onBackPressed()
        }
    }
}