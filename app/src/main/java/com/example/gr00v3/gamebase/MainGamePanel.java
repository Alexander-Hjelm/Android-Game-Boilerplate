package com.example.gr00v3.gamebase;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.app.Activity;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Gr00v3 on 05/25/2015.
 */
public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = MainGamePanel.class.getSimpleName();     //Logging tag

    private MainThread thread;  //our game loop
    private Character character;    //our draggable character
    private String avgFps;

    private SpriteAnimated spriteAnimated;


    public MainGamePanel(Context context) {
        super(context);

        // adding the callback (this) to the surface holder to intercept events
        // This line sets the current class (MainGamePanel) as the handler for the events happening on the actual surface.
        getHolder().addCallback(this);

        //create character and load bitmap
        character = new Character(BitmapFactory.decodeResource(getResources(), R.drawable.ball), 10, 10);
        //create new game loop thread,          pass current holder and this panel
        thread = new MainThread(getHolder(), this);

        //Create our sprite
        spriteAnimated = new SpriteAnimated(BitmapFactory.decodeResource(getResources(), R.drawable.soldier_sprites)
                , 10, 50    // initial position
                , 30, 47    // width and height of sprite
                , 5, 4);    //fps, number of frames


        // make the GamePanel focusable so it can handle events
        //makes our Game Panel focusable, which means it can receive focus so it can handle events.
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);    //set running flag of thread
        thread.start();             //Start thread w/ built in method
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //signal thread to shut down and wait for it to finish
        boolean retry = true;
        while(retry)
        {
            try
            {
                thread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
                //try again to shut down the thread
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN)        //if pressed finger down
        {
            //delegate event handling to the character
            character.handleActionDown((int) event.getX(), (int) event.getY());

            if (event.getY() > this.getHeight() - 50)        //estimate lower part of the screen
            {
                //end game loop and exit activity
                thread.setRunning(false);
                ((Activity) this.getContext()).finish();
            } else {
                //Log touch coords
                Log.d(TAG, "Coords: x = " + event.getX() + ", y = " + event.getY());
            }
        }

        if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
            //defining the gestures
            if(character.isTouched())
            {
                character.setX((int)event.getX());
                character.setY((int)event.getY());

                Log.d(TAG, "Bitmap coords should now be: " + character.getX() + ", " + character.getY());
            }
        }

        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            //touch was released
            if(character.isTouched())
            {
                character.setTouched(false);
            }
        }

        return true;    //ACTION_MOVE only works if onTouchEvent reutrns true
        //return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // fills the canvas with black
        canvas.drawColor(Color.BLACK);

        // Draw our "sprite" png
       character.draw(canvas);
        //super.onDraw(canvas);

        //Draw our sprite
        spriteAnimated.draw(canvas);

        displayFps(canvas, avgFps);
    }

    public void update() {
        //Game logic goes here

        //The collision detection of the lone droid

        //check collision with right wall if heading right
        if (character.getSpeed().getxDirection() == Speed.DIRECTION_RIGHT
                && character.getX() + character.getBitmap().getWidth() / 2 >= getWidth())
        {
            character.getSpeed().flipxDirection();
        }

        //check collision with right wall if heading left
        if (character.getSpeed().getxDirection() == Speed.DIRECTION_LEFT
                && character.getX() - character.getBitmap().getWidth() / 2 <= 0)
        {
            character.getSpeed().flipxDirection();
        }

        //check collision with right wall if heading down
        if (character.getSpeed().getyDirection() == Speed.DIRECTION_DOWN
                && character.getY() + character.getBitmap().getHeight() / 2 >= getHeight())
        {
            character.getSpeed().flipyDirection();
        }

        //check collision with right wall if heading up
        if (character.getSpeed().getyDirection() == Speed.DIRECTION_UP
                && character.getY() - character.getBitmap().getHeight() / 2 <= 0)
        {
            character.getSpeed().flipyDirection();
            Log.d(TAG, "y = " + character.getY() + ", bitmap height = " + character.getBitmap().getHeight() / 2);
        }



        //Update gameobjects
        spriteAnimated.update(System.currentTimeMillis());

        character.update();
    }

    public void setAvgFps(String Fps)
    {
        this.avgFps = Fps;
    }

    public void displayFps(Canvas canvas, String fps) {
        if (canvas != null && fps != null)
        {
            Paint paint = new Paint();
            paint.setARGB(255, 255, 255, 255);
            canvas.drawText(fps, this.getWidth() - 100, 20, paint);
        }

    }
}
