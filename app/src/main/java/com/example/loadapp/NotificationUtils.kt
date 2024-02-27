package com.example.loadapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat


private const val NOTIFICATION_ID = 0
const val NOTIFICATION_ACTION_CLICKED = "NOTIFICATION_ACTION_CLICKED"
const val DOWNLOAD_STATUS = "DOWNLOAD_STATUS"
const val DOWNLOAD_FILE_NAME = "DOWNLOAD_FILE_NAME"


fun NotificationManager.sendNotification(
    fileName: String, downloadStatus: Int, appContext: Context, statusPendingIntent: PendingIntent
) {
    val contentIntent = Intent(appContext, MainActivity::class.java).apply {
        action = NOTIFICATION_ACTION_CLICKED
        putExtra(DOWNLOAD_STATUS, downloadStatus)
        putExtra(DOWNLOAD_FILE_NAME, fileName)
    }

    val contentPendingIntent = PendingIntent.getActivity(
        appContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val downloadImage = BitmapFactory.decodeResource(
        appContext.resources, R.drawable.download_icon
    )
    val bigPictureStyle = NotificationCompat.BigPictureStyle().bigPicture(downloadImage)
    val notificationBuilder =
        NotificationCompat.Builder(appContext, appContext.getString(R.string.download_channel))
            .setSmallIcon(R.drawable.baseline_cloud_download_24)
            .setContentText(appContext.getString(R.string.app_name)).setContentText(fileName)
            .setContentIntent(contentPendingIntent).setAutoCancel(true).setStyle(bigPictureStyle)
            .addAction(
                R.drawable.baseline_cloud_download_24,
                appContext.getString(R.string.check_status),
                statusPendingIntent
            ).setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, notificationBuilder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}