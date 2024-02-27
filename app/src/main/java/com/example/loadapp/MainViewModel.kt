package com.example.loadapp

import android.app.Application
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val app: Application) : AndroidViewModel(app) {
    private var downloadId = 0L
    private var downloadProgress = 0f
    private var downloadStatus: Int? = null
    private var downloadFileName: String? = null
    private var isDownloadInProgress = false

    private val _downloadProgressLiveData: MutableLiveData<Int> = MutableLiveData()
    val downloadProgressLiveData: LiveData<Int> = _downloadProgressLiveData.distinctUntilChanged()

    private val _downloadingFileLiveData: MutableLiveData<String?> = MutableLiveData()
    val downloadingFileLiveData: LiveData<String?> = _downloadingFileLiveData

    private val filter = IntentFilter().apply {
        addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        addAction(NOTIFICATION_ACTION_CLICKED)
    }

    fun download(url: String, title: CharSequence, subtitle: CharSequence) {
        if (isDownloadInProgress) {
            _downloadingFileLiveData.postValue(downloadFileName)
            return
        }
        val downloadRequest =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription(subtitle)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setRequiresCharging(false)
        val downloadManager =
            app.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(downloadRequest)
        downloadFileName = subtitle.toString()
        isDownloadInProgress = true
        updateDownloadProgress()
    }

    fun onReceive(intent: Intent?) {
        viewModelScope.launch(Dispatchers.IO) {
            readDownloadFile(intent)
            resetDownloadProgressToZero()
            postNotificationWithActionReceiver()
        }
    }


    private fun readDownloadFile(intent: Intent?) {
        val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (downloadId != -1L && downloadId != null) {
            val downloadManager =
                app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val descriptionIndex =
                    cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION)
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                downloadStatus = cursor.getInt(statusIndex)
                downloadFileName = cursor.getString(descriptionIndex)
            }
            cursor.close()
        }
    }

    private fun resetDownloadProgressToZero() {
        isDownloadInProgress = false
        downloadProgress = 0f
        _downloadProgressLiveData.postValue(0)
    }

    private fun postNotificationWithActionReceiver() {
        val statusIntent = Intent(app, BroadcastReceiverHandler::class.java).apply {
            action = NOTIFICATION_ACTION_CLICKED
            putExtra(DOWNLOAD_STATUS, downloadStatus)
            putExtra(DOWNLOAD_FILE_NAME, downloadFileName)
        }
        val statusPendingIntent = PendingIntent.getBroadcast(
            app,
            0,
            statusIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationManager = ContextCompat.getSystemService(
            app,
            NotificationManager::class.java
        )
        notificationManager?.sendNotification(
            downloadFileName ?: app.getString(R.string.app_name),
            downloadStatus ?: 0,
            app,
            statusPendingIntent
        )
    }

    fun registerReceiver(receiver: BroadcastReceiver) {
        app.registerReceiver(receiver, filter)
    }

    fun unregisterReceiver(receiver: BroadcastReceiver) {
        app.unregisterReceiver(receiver)
    }

    private fun updateDownloadProgress() = viewModelScope.launch(Dispatchers.IO) {
        val downloadManager = app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        downloadProgress = 0f
        var cursor: Cursor?
        while (downloadProgress < 100f) {
            cursor = downloadManager.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                val totalBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                val downloadedBytesIndex =
                    cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val totalBytes = cursor.getLong(totalBytesIndex)
                val downloadedBytes = cursor.getLong(downloadedBytesIndex)
                if (totalBytes != -1L) {
                    downloadProgress = ((downloadedBytes * 100f) / totalBytes)
                    _downloadProgressLiveData.postValue(downloadProgress.toInt())
                }
            }
            cursor?.close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(channelId: String, channelName: String) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setShowBadge(false)
        }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.enableVibration(true)
        notificationChannel.description = app.getString(R.string.download)
        val notificationManager =
            app.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}