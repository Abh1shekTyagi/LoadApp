package com.example.loadapp

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.loadapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.createChannel(getString(R.string.download_channel), getString(R.string.download))
        initListener()
        initObservers()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initObservers() {
        viewModel.downloadProgressLiveData.observe(this) {
            it?.let { downloadProgress ->
                binding.downloadCustomButton.changeButtonProgress(downloadProgress)
            }
        }

        viewModel.downloadingFileLiveData.observe(this) {
            it?.let { fileName ->
                showToastMessage(getString(R.string.downloading_please_wait, fileName))
            }
        }

        viewModel.postNotification.observe(this) {
            it?.let {
                checkPermissionAndNotify()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionAndNotify() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            -> {
                viewModel.postNotificationWithAction()
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.postNotificationWithAction()
            } else {
                val snackBar =
                    Snackbar.make(binding.root, R.string.allow_notification, Snackbar.LENGTH_SHORT)
                snackBar.setAction(R.string.allow) {
                    openNotificationSetting()
                }
                snackBar.show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openNotificationSetting() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivity(intent)
    }

    private fun initListener() {
        customButtonOnClick()
    }

    private fun customButtonOnClick() {
        binding.downloadCustomButton.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            when (binding.radioGroup.checkedRadioButtonId) {
                R.id.glideRadioButton -> {
                    viewModel.download(
                        bigFileTestURL,
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