package com.example.loadapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BroadcastReceiverHandler : BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        p1?.let {
            val activityIntent = Intent(context, DetailActivity::class.java).apply {
                action = NOTIFICATION_ACTION_CLICKED
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(DOWNLOAD_FILE_NAME, it.getStringExtra(DOWNLOAD_FILE_NAME))
                putExtra(DOWNLOAD_STATUS, it.getIntExtra(DOWNLOAD_STATUS, 0))
            }
            context?.startActivity(activityIntent)
        }
    }
}