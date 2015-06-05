package com.example.gr00v3.gamebase;
//package com.example.gr00v3.gamebase.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Gr00v3 on 05/27/2015.
 */
public class Character {

    //this has a bitmap, x and y coords, and get/set for all 3

    private Bitmap bitmap;
    private int x;
    private int y;

    private Speed speed;

    //if character is touched/picked up
    private boolean touched;

    public Character(Bitmap bitmap, int x, int y)
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;

        this.speed = new Speed();
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }
    public int getX()
    {
        return this.x;
    }
    public void setX(int x)
    {
        this.x = x;
    }
    public int getY()
    {
        return this.y;
    }
    public void setY(int y)
    {
        this.y = y;
    }




    //functionality related to touched

    public boolean isTouched()
    {
        return touched;
    }
    public void setTouched(boolean touched)
    {
        this.touched = touched;
    }

    public Speed getSpeed()
    {
        return this.speed;
    }

    //Draw the bitmap

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(this.bitmap, this.x - bitmap.getWidth() / 2,  this.y - bitmap.getHeight() / 2, null);
    }

    //Handle touch action
    //If the action happened inside the area of our droid’s bitmap we’ll set its touched status to true
    public void handleActionDown(int eventX, int eventY)
    {
        //try if touch was over this sprite
        if (eventX >= (x - bitmap.getWidth()/2) && eventX <= (x + bitmap.getWidth()/2))
        {
            if (eventY >= (y - bitmap.getHeight()/2) && eventY <= (y + bitmap.getHeight()/2))
            {
                //bitmap touched
                this.setTouched(true);
            }
            else
            {
                this.setTouched(false);
            }
        }
        else
        {
            this.setTouched(false);
        }
    }


    public void update() {
        //game logic goes here

        if(!touched)
        {
            //MOVE IT!
            x += (speed.getXv() * speed.getxDirection());
            y += (speed.getYv() * speed.getyDirection());
        }
    }
}
