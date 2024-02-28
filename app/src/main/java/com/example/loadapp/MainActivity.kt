package com.example.loadapp

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.loadapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        )[MainViewModel::class.java]
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            viewModel.onReceive(p1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.createChannel(getString(R.string.download_channel), getString(R.string.download))
        initListener()
        initObservers()
    }

    private fun initObservers() {
        viewModel.downloadProgressLiveData.observe(this) {
            it?.let { downloadProgress ->
                binding.downloadCustomButton.changeButtonProgress(downloadProgress)
                if (it == 100) showToastMessage(getString(R.string.download_success))
            }
        }

        viewModel.downloadingFileLiveData.observe(this) {
            it?.let { fileName ->
                showToastMessage(getString(R.string.downloading_please_wait, fileName))
            }
        }
    }

    private fun initListener() {
        customButtonOnClick()
    }

    private fun customButtonOnClick() {
        binding.downloadCustomButton.setOnClickListener {
            when (binding.radioGroup.checkedRadioButtonId) {
                R.id.glideRadioButton -> {
                    viewModel.download(
                        glideURL,
                        getString(R.string.app_name),
                        getString(R.string.glide)
                    )
                }

                R.id.loadAppRadioButton -> {
                    viewModel.download(
                        loadAppURL,
                        getString(R.string.app_name),
                        getString(R.string.loadApp)
                    )
                }

                R.id.retrofitRadioButton -> {
                    viewModel.download(
                        retrofitURL,
                        getString(R.string.app_name),
                        getString(R.string.retrofit)
                    )
                }

                else -> {
                    showToastMessage(getString(R.string.select_file))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        viewModel.registerReceiver(receiver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterReceiver(receiver)
    }

    private fun showToastMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val bigFileTestURL = "https://link.testfile.org/PDF200MB"
        private const val retrofitURL = "https://github.com/square/retrofit"
        private const val loadAppURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val glideURL = "https://github.com/bumptech/glide"
    }
}