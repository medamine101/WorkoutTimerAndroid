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
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class MainActivity : AppCompatActivity() {

     // TODO: Implement way to set custom time

    //Cannot be initialized with the value of the objects since onCreate needs to occur first
    private var timerTextBox: TextView? = null //set to null, value changed in onCreate()

    private var startButton: Button? = null //set to null, value changed in onCreate()

    private var countDownTimer: CountDownTimer? = null //set to null, value changed in startTimer()

    var originalTimerNumber: Long = 6000

    var timeLeftMilliseconds: Long = originalTimerNumber //Temporary, set to 10 minutes

    private var timerRunning: Boolean = false //Timer is not running at launch

    private var notificationManager: NotificationManagerCompat? = null

    private val workoutTimerResetAction = "WORKOUT_TIMER_RESET"

    private var fragmentContainer: FrameLayout? = null

    private var timerChangeTool: TimerChanger? = null

    private var isToolRunning: Boolean
        get() {
            return timerChangeTool != null //Return true if timerChangeTool fragment exists
        }
        @Suppress("UNUSED_PARAMETER")
        set(value) = Unit //Setting a value does nothing

    //broadcast receiver object
    private var myReceiver: BroadcastReceiver = object: BroadcastReceiver() {
        //Method to run upon receiving the broadcast Intent
        override fun onReceive(context: Context?, intent: Intent?) {
            //Ensuring that the intent matches what is wanted
            if (intent?.action == workoutTimerResetAction){
                val givenContext = context as MainActivity
                givenContext.onNotificationClick()
                givenContext.notificationManager?.cancel(1)
            }
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

        registerReceiver(myReceiver,  IntentFilter(workoutTimerResetAction))

        fragmentContainer = findViewById(R.id.fragment_container)

    }

    //Unregister the broadcast receiver when user quits app
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
        val minutesLeft = (timeLeftMilliseconds / 60000).toInt()

        val secondsLeft = (timeLeftMilliseconds % 60000 / 1000).toInt()

        var timerString = "$minutesLeft:"

        if (secondsLeft < 10) timerString += "0"

        timerString += secondsLeft

        timerTextBox?.text = timerString

    }

    //The view parameter is not used but needed for Button onClick to work
    @Suppress("UNUSED_PARAMETER")
    fun onTimerButtonClick(view: View) {
        if (timerRunning) stopTimer()
        else startTimer()
    }

    //Called when timer hits 0
    fun sendNotification(){

        //Create the two intents for the notification
        val openAppIntent = Intent(this, MainActivity::class.java) //Intent to open app
        val resetTimerIntent = Intent(workoutTimerResetAction) //Intent to reset timer

        //Create the two pending intents for the notification
        val openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE)
        val resetTimerPendingIntent = PendingIntent.getBroadcast(this, 0, resetTimerIntent, PendingIntent.FLAG_IMMUTABLE)

        val title = "TIME IS UP!"
        val message = "Click to reset the timer"

        val notification = NotificationCompat.Builder(this, NotificationChannelCreator.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_one)
            .setContentTitle(title)
            .setContentText(message)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(openAppPendingIntent)
            .addAction(R.drawable.ic_one, "Reset Timer", resetTimerPendingIntent)
            .build()

        notification.flags = Notification.FLAG_AUTO_CANCEL

        notificationManager?.notify(1, notification)

    }

    //Method to reset the timer: stop running timer, reset the time, then update the clock
    //The view parameter is not used but needed for Button onClick to work
    @Suppress("UNUSED_PARAMETER")
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

    //Called when the notification is pressed
    override fun onNewIntent(intent: Intent?) {
        this.resetTimer(this.startButton as View) //Timer is reset
        super.onNewIntent(intent)
   }

    //Method to be used when the textbox is clicked
    //The view parameter is not used but needed for Button onClick to work
    @Suppress("UNUSED_PARAMETER")
    fun onTimerTextBoxClick(view: View){
        if (isToolRunning) closeTimerChangerFragment()
        else openTimerChangerFragment()

    }

    private fun openTimerChangerFragment(){

        //Do not allow opening the timer changer fragment if the timer is running
        if (timerRunning) return

        timerChangeTool =  TimerChanger.newInstance()
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom)
        transaction.addToBackStack(null)
        transaction.add(R.id.fragment_container, timerChangeTool as Fragment, "TIMER_CHANGER").commit()

    }

    fun closeTimerChangerFragment(){
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom)
        transaction.remove(timerChangeTool as Fragment).commit()
        timerChangeTool = null
    }

}