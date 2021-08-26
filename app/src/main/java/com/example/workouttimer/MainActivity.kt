package com.example.workouttimer

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat



class MainActivity : AppCompatActivity() {

     // TODO: Implement way to set custom time

    //Cannot be initialized with the value of the objects since onCreate needs to occur first
    private var timerTextBox: TextView? = null //set to null, value changed in onCreate()
    private var startButton: Button? = null //set to null, value changed in onCreate()

    private var countDownTimer: CountDownTimer? = null //set to null, value changed in startTimer()

    private var originalTimerNumber: Long = 6000

    private var timeLeftMilliseconds: Long = originalTimerNumber //Temporary, set to 10 minutes

    private var timerRunning: Boolean = false //Timer is not running at launch

    private var notificationManager: NotificationManagerCompat? = null

    //broadcast receiver object
    private var myReceiver: BroadcastReceiver = object: BroadcastReceiver() {
        //Method to run upon receiving the broadcast Intent
        override fun onReceive(context: Context?, intent: Intent?) {
            val x = context as MainActivity
            x.onNotificationClick()
        }
    }

    //Method to run on App Launch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //These class properties are now assigned values corresponding to the UI objects
        timerTextBox = findViewById(R.id.timerTextBox)
        startButton = findViewById(R.id.startTimerButton)

        //Create Notification Manager Object
        notificationManager = NotificationManagerCompat.from(this)

        //Update the time on the App to display the default
        updateTime()

        registerReceiver(myReceiver,  IntentFilter("BLA"))

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

    //Method called when button clicked and the timer is not running
    private fun startTimer(){

        countDownTimer = object : CountDownTimer(timeLeftMilliseconds, 1000) {
            //Method called every tick
            override fun onTick(millisUntilFinished: Long) {
                timeLeftMilliseconds = millisUntilFinished
                updateTime()
            }
            //Method for when timer finishes
            override fun onFinish() {
                sendNotification()
                stopTimer()
            }
        }.start()

        startButton?.text = getString(R.string.stop_button)
        timerRunning = true
    }

    //Called to stop the timer, also called on finish
    private fun stopTimer(){
        countDownTimer?.cancel()
        startButton?.text = getString(R.string.start_button)
        timerRunning = false
    }

    //Called to update the timer
    fun updateTime(){
        val minutesLeft = (timeLeftMilliseconds/60000).toInt()

        val secondsLeft = (timeLeftMilliseconds % 60000 / 1000).toInt()

        var timerString = "$minutesLeft:"

        if (secondsLeft < 10) timerString += "0"

        timerString += secondsLeft

        timerTextBox?.text = timerString

    }

    //Called when the button to start the timer is clicked
    fun onTimerButtonClick(view: View) {
        if (timerRunning) stopTimer()
        else startTimer()
    }

    //Called when timer hits 0
    fun sendNotification(){

        val intent = Intent("BLA")
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        val title = "TIME IS UP!"
        val message = "Click to reset the timer"

        val notification = NotificationCompat.Builder(this, NotificationsStuff.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_one)
            .setContentTitle(title)
            .setContentText(message)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent) //Put Intent in notification
            //.addAction()
            .build()

        notification.flags = Notification.FLAG_AUTO_CANCEL

        notificationManager?.notify(1, notification)

    }

    //Method to reset the timer: stop running timer, reset the time, then update the clock
    fun resetTimer(view: View){
        if (timerRunning) stopTimer()
        timeLeftMilliseconds = originalTimerNumber
        updateTime()
    }

    //Method to be called when the notification is clicked
    fun onNotificationClick(){
        resetTimer(this.startButton as View)
        startTimer()

    }

}