package com.itp226.periodicupdates;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

// This service runs periodically.
// When this service is killed, it broadcasts a request to the
// RestartReceiver. The receiver will check the sensor setting
// and determine whether to restart the service.
// At present, this service does not do much. It only increments
// a counter every few seconds and broadcasts a request to alert
// the user whenever the counter hits some condition.
public class SensorService extends Service {
    private static final String myId = SensorService.class.getSimpleName();

    private long TimerDelay = 1000; // Amount of delay before timer starts
    private long TimerPeriod = 5000; // Time interval between successive task executions
    public int counter=0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        // prevent the system from restarting this service if it is killed.
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(myId, "onDestroy");
        // Stop the timer
        if (timer!=null) {
            timer.cancel();
            timer=null;
        }
        // send new broadcast to restart the service
        // when this service is destroyed.
        Intent broadcastIntent = new Intent("com.itp226.periodicupdates.RestartReceiverIntent");
        sendBroadcast(broadcastIntent);
    }

    private Timer timer;
    private TimerTask timerTask;


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        timerTask = new TimerTask() {
            public void run() {
                // The current simple task is to increment the counter
                // When the counter is divisible by some value, alert the user
                // TODO replace the current simple task with data access
                Log.i(" Timer ", " counter = "+ (counter++));
                if (counter%4==0) {
                    Intent broadcastIntent = new Intent("com.itp226.periodicupdates.AlertReceiverIntent");
                    sendBroadcast(broadcastIntent);
                }
            }
        };

        //schedule the timer to start after a delay and to execute every X second
        timer.schedule(timerTask, TimerDelay, TimerPeriod); //
    }

}
