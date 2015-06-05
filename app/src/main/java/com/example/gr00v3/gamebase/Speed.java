package com.example.gr00v3.gamebase;

/**
 * Created by Gr00v3 on 05/28/2015.
 */
public class Speed {

    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_LEFT = -1;
    public static final int DIRECTION_UP = -1;
    public static final int DIRECTION_DOWN = 1;

    //velocity values on respective axises
    private float xv = 1;
    private float yv = 1;

    private int xDirection = DIRECTION_RIGHT;
    private int yDirection = DIRECTION_UP;

    public Speed()
    {
        this.xv = 10;
        this.yv = 10;
    }


    //GET/SET velocity

    public float getXv()
    {
        return this.xv;
    }
    public float getYv()
    {
        return this.yv;
    }

    public void setXv(float xv)
    {
        this.xv = xv;
    }
    public void setYv(float xv)
    {
        this.yv = yv;
    }

    public int getxDirection()
    {
        return xDirection;
    }
    public int getyDirection()
    {
        return yDirection;
    }

    public void setxDirection(int xDirection)
    {
        this.xDirection = xDirection;
    }
    public void setyDirection(int yDirection)
    {
        this.yDirection = yDirection;
    }


    //flip the direction on either axis:

    public void flipxDirection()
    {
        this.xDirection = xDirection * -1;
    }
    public void flipyDirection()
    {
        this.yDirection = yDirection * -1;
    }

}
