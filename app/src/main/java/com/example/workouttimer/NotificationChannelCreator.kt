package com.example.workouttimer

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationChannelCreator: Application(){

    //Allow the channel ID to be accessed without an instance of NotificationChannelCreator
    companion object{
        var CHANNEL_1_ID = "channel1"
    }

    //Runs on app launch
    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()

    }

    //Creates notification channels on app launch, called by onCreate()
    private fun createNotificationChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel1 = NotificationChannel(CHANNEL_1_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "This is Channel 1"
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel1)

        }


    }






}