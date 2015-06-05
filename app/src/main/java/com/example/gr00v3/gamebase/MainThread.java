package com.example.gr00v3.gamebase;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.text.DecimalFormat;

/**
 * Created by Gr00v3 on 05/25/2015.
 *
 * This is the actual game loop
 */
public class MainThread extends Thread
{
    private static final String TAG = MainThread.class.getSimpleName();     //Logging tag

    //refs
    private SurfaceHolder surfaceHolder;
    private MainGamePanel gamePanel;

    //flag to hold game state
    private boolean running;

    // desired fps
    private final static int    MAX_FPS = 50;
    // maximum number of frames to be skipped
    private final static int    MAX_FRAME_SKIPS = 5;
    // the frame period
    private final static int    FRAME_PERIOD = 1000 / MAX_FPS;

    ///////////////////////////////*Stuff for recording fps *///////////////////////////////////////

    private DecimalFormat df = new DecimalFormat("0.##");  //2 decimal points
    //read every second
    private final static int STAT_INTERVAL = 1000;
    //The average will be calculated by storing the last 10 FPSs
    private final static int FPS_HISTORY_NR = 10;
    //last time status was stored
    private long lastStatusScore = 0;
    //the status time counter
    private long statusIntervalTimer = 0l;
    //the number of frames skipped since the game started
    private long totalFramesSkipped = 0l;
    //number of frames skipped in a store cycle (1 sec)
    private long framesSkippedPerStatCycle = 0l;

    //number of rendered frames in an interval
    private int frameCountPerStatCycle = 0;
    private long totalFrameCount = 0l;
    // the last FPS values
    private double fpsStore[];
    //the number of times the stat has been read
    private long statsCount = 0;
    //the average FPS since the game started
    private double averageFPS = 0.0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Constructor
    public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }


    public void setRunning(boolean boolIn)
    {
        this.running = boolIn;
    }

    @Override
    public void run() {

        Canvas canvas;

        //log to logcat
        Log.d(TAG, "Starting new Game loop");

        //initialise timing elements for stat gathering
        initTimingElements();

        long beginTime;
        long timeDiff;
        int sleepTime;
        int framesSkipped;

        while (running)
        {
            //clear canvas
            canvas = null;
            //update game state
            //render state to screen

            // try locking the canvas for exclusive pixel editing on the surface
            try
            {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)
                {
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;  // resetting the frames skipped

                    // update game state
                    this.gamePanel.update();
                    // draws the canvas on the panel
                    //call onDraw method on our main game panel.
                    this.gamePanel.onDraw(canvas);

                    //Calculate how long the cycle took,    update and onDraw will have taken some time
                    timeDiff = System.currentTimeMillis() - beginTime;

                    //calculate sleep time
                    sleepTime = (int)(FRAME_PERIOD - timeDiff);

                    if(sleepTime > 0)
                    {
                        //We wait...
                        try
                        {
                            //send the thread to sleep for a short period of time
                            //useful for saving battery
                            Thread.sleep(sleepTime);
                        }
                        catch (InterruptedException e) {}
                    }

                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS)
                    {
                        //We need to catch up, update without rendering
                        this.gamePanel.update();

                        //add frame period to check if we are in next frame yet
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;

                    }

                    if (framesSkipped > 0)
                    {
                        Log.d(TAG, "Skipped:" + framesSkipped);
                    }

                    ///////////FPS AND STATISTICS////////////
                    framesSkippedPerStatCycle += framesSkipped;
                    //Calling the coroutine to store the gathered statistics
                    storeStats();

                }
            }
            finally
            {
                // in case of an exception the surface is not left in an inconsistent state
                if (canvas != null)
                {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }


    /**
     * The statistics - it is called every cycle, it checks if time since last
     * store is greater than the statistics gathering period (1 sec) and if so
     * it calculates the FPS for the last period and stores it.
     *
     *  It tracks the number of frames per period. The number of frames since
     *  the start of the period are summed up and the calculation takes part
     *  only if the next period and the frame count is reset to 0.
     */
    private void storeStats() {
        frameCountPerStatCycle++;
        totalFrameCount++;

        //check the actual time
        statusIntervalTimer += (System.currentTimeMillis() - statusIntervalTimer);

        if (statusIntervalTimer >= lastStatusScore + STAT_INTERVAL)
        {
            //Calculate the actual frames per status check interval
            double actualFps = (double)(frameCountPerStatCycle / (STAT_INTERVAL / 1000));

            //Stores the lattest fps in the array
            fpsStore[(int) statsCount % FPS_HISTORY_NR] = actualFps;

            //increases the number of times statistics was calculated
            statsCount++;

            double totalFps = 0.0;
            //sum up the stored fps values
            for(int i = 0; i < FPS_HISTORY_NR; i++)
            {
                totalFps += fpsStore[i];
            }

            //obtain the average
            if (statsCount < FPS_HISTORY_NR)
            {
                //In case we skipped some frame
                averageFPS = totalFps / statsCount;
            }
            else
            {
                averageFPS = totalFps / FPS_HISTORY_NR;
            }

            //saving the number of total frames skipped
            totalFramesSkipped += framesSkippedPerStatCycle;
            //resetting the counters after a status record (1 sec)
            framesSkippedPerStatCycle = 0;
            statusIntervalTimer = 0;
            frameCountPerStatCycle = 0;

            statusIntervalTimer = System.currentTimeMillis();
            lastStatusScore = statusIntervalTimer;
            Log.d(TAG, "Average FPS: " + df.format(averageFPS));
            gamePanel.setAvgFps("FPS: " + df.format(averageFPS));
        }
    }

    private void initTimingElements()
    {
        //Initialise timing elements
        fpsStore = new double[FPS_HISTORY_NR];
        for (int i = 0; i < FPS_HISTORY_NR; i++)
        {
            fpsStore[i] = 0.0;
        }
        Log.d( TAG + ".initTimingElements()", "Timing elements for stats initialized");
    }
}
